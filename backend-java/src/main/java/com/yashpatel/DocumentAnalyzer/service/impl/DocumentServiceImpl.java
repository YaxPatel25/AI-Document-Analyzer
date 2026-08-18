package com.yashpatel.DocumentAnalyzer.service.impl;

import lombok.RequiredArgsConstructor;
import com.yashpatel.DocumentAnalyzer.repository.DocumentRepository;
import com.yashpatel.DocumentAnalyzer.dto.AIResponse;
import com.yashpatel.DocumentAnalyzer.dto.DocumentDetailResponse;
import com.yashpatel.DocumentAnalyzer.dto.DocumentResponse;
import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;
import com.yashpatel.DocumentAnalyzer.entity.Document;
import com.yashpatel.DocumentAnalyzer.entity.DocumentStatus;
import com.yashpatel.DocumentAnalyzer.service.AIService;
import com.yashpatel.DocumentAnalyzer.service.DocumentService;
import com.yashpatel.DocumentAnalyzer.service.DocumentChunkService;
import com.yashpatel.DocumentAnalyzer.util.DocxExtractor;
import com.yashpatel.DocumentAnalyzer.util.PdfExtractor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

        private final DocumentRepository documentRepository;
        private final AIService aiService;
        private final DocumentChunkService documentChunkService;

        @Override
        public UploadResponse uploadDocument(MultipartFile file) {

                // Validate file type first
                String contentType = file.getContentType();

                if (contentType == null ||
                                !List.of(
                                                "application/pdf",
                                                "text/plain",
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                                .contains(contentType)) {

                        return new UploadResponse(
                                        null,
                                        "Invalid file type. Only PDF, TXT, and DOCX are allowed.");
                }
                try {

                        String originalFileName = file.getOriginalFilename();

                        String storedFileName = UUID.randomUUID() + "-" + originalFileName;

                        Path storagePath = Paths.get("storage");

                        Files.createDirectories(storagePath);

                        Path filePath = storagePath.resolve(storedFileName);

                        file.transferTo(filePath);

                        String extractedText = "";

                        if ("application/pdf".equals(contentType)) {
                                extractedText = PdfExtractor.extractText(filePath.toFile());
                        } else if ("text/plain".equals(contentType)) {
                                extractedText = Files.readString(filePath);
                        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                        .equals(contentType)) {
                                // docx extraction — needs apache poi dependency
                                extractedText = DocxExtractor.extractText(filePath.toFile());
                        }

                        Document document = Document.builder()
                                        .originalFileName(originalFileName)
                                        .storedFileName(storedFileName)
                                        .fileSize(file.getSize())
                                        .contentType(contentType)
                                        .uploadedAt(LocalDateTime.now())
                                        .extractedText(extractedText)
                                        .status(DocumentStatus.UPLOADED)
                                        .build();

                        document = documentRepository.save(document);

                        documentChunkService.createChunks(
                                document.getId(),
                                extractedText
                        );

                        return new UploadResponse(
                                        document.getId(),
                                        "File uploaded successfully");

                } catch (Exception e) {
                        throw new RuntimeException("File upload failed", e);
                }
        }

        @Override
        public List<DocumentResponse> getAllDocuments() {

                return documentRepository.findAll()
                                .stream()
                                .map(document -> new DocumentResponse(
                                                document.getId(),
                                                document.getOriginalFileName(),
                                                document.getContentType(),
                                                document.getFileSize(),
                                                document.getUploadedAt(),
                                                document.getStatus(),
                                                document.getErrorMessage()))
                                .toList();
        }

        @Override
        public Resource downloadDocument(UUID documentId) {

                try {

                        Document document = documentRepository
                                        .findById(documentId)
                                        .orElseThrow(() -> new RuntimeException("Document not found"));

                        Path filePath = Paths.get("storage")
                                        .resolve(document.getStoredFileName());

                        Resource resource = new UrlResource(filePath.toUri());

                        if (!resource.exists()) {
                                throw new RuntimeException("File not found");
                        }

                        return resource;

                } catch (MalformedURLException e) {
                        throw new RuntimeException("Download failed", e);
                }
        }

        @Override
        public DocumentDetailResponse getDocumentById(UUID id) {

                Document document = documentRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException("Document not found"));

                return new DocumentDetailResponse(
                                document.getId(),
                                document.getOriginalFileName(),
                                document.getExtractedText());
        }

        @Override
        public DocumentDetailResponse getSummarizedDocumentById(UUID id) {

                Document document = documentRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException("Document not found"));

                // If summary already exists, return it (don't call AI again)
                if (document.getAiAnalysis() != null && !document.getAiAnalysis().isBlank()) {
                        return new DocumentDetailResponse(
                                        document.getId(),
                                        document.getOriginalFileName(),
                                        document.getAiAnalysis());
                }

                // Validate file size only if AIService is GroqService
                if (aiService instanceof GroqService groqService) {
                        String validationError = groqService.validateFileSize(document.getFileSize());
                        if (validationError != null) {
                                document.setStatus(DocumentStatus.FAILED);
                                document.setErrorMessage(validationError);

                                documentRepository.save(document);

                                return new DocumentDetailResponse(
                                                document.getId(),
                                                document.getOriginalFileName(),
                                                validationError); // show error as summary text in UI
                        }
                }

                try {

                        document.setStatus(DocumentStatus.PROCESSING);
                        documentRepository.save(document);

                        // Call AI only when summary is requested for the first time
                        AIResponse aiResponse = aiService.summarize(document.getExtractedText());

                        document.setAiAnalysis(
                                        aiResponse.summary());

                        document.setPromptTokens(
                                        aiResponse.promptTokens());

                        document.setCompletionTokens(
                                        aiResponse.completionTokens());

                        document.setTotalTokens(
                                        aiResponse.totalTokens());
                        document.setStatus(DocumentStatus.COMPLETED);
                        document.setErrorMessage(null);

                        documentRepository.save(document);

                        return new DocumentDetailResponse(
                                        document.getId(),
                                        document.getOriginalFileName(),
                                        aiResponse.summary());

                } catch (Exception e) {

                        document.setStatus(DocumentStatus.FAILED);
                        document.setErrorMessage(e.getMessage());
                        documentRepository.save(document);

                        throw new RuntimeException(
                                        "AI summarization failed",
                                        e);
                }
        }
}