package com.yashpatel.DocumentAnalyzer.service.impl;

import lombok.RequiredArgsConstructor;
import com.yashpatel.DocumentAnalyzer.repository.DocumentRepository;
import com.yashpatel.DocumentAnalyzer.dto.DocumentDetailResponse;
import com.yashpatel.DocumentAnalyzer.dto.DocumentResponse;
import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;
import com.yashpatel.DocumentAnalyzer.entity.Document;
import com.yashpatel.DocumentAnalyzer.service.AIService;
import com.yashpatel.DocumentAnalyzer.service.DocumentService;
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

        @Override
        public UploadResponse uploadDocument(MultipartFile file) {

                try {

                        String originalFileName = file.getOriginalFilename();

                        String storedFileName = UUID.randomUUID() + "-" + originalFileName;

                        Path storagePath = Paths.get("storage");

                        Files.createDirectories(storagePath);

                        Path filePath = storagePath.resolve(storedFileName);

                        file.transferTo(filePath);

                        String extractedText = "";

                        if ("application/pdf".equals(file.getContentType())) {
                                extractedText = PdfExtractor.extractText(
                                                filePath.toFile());
                        }

                        String summary = aiService.summarize(extractedText);

                        System.out.println(summary);

                        Document document = Document.builder()
                                        .originalFileName(originalFileName)
                                        .storedFileName(storedFileName)
                                        .fileSize(file.getSize())
                                        .contentType(file.getContentType())
                                        .uploadedAt(LocalDateTime.now())
                                        .extractedText(extractedText)
                                        .aiAnalysis(summary)
                                        .build();

                        document = documentRepository.save(document);

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
                                                document.getUploadedAt()))
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
}