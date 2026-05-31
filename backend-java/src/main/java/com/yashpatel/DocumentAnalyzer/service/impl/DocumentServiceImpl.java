package com.yashpatel.DocumentAnalyzer.service.impl;

import lombok.RequiredArgsConstructor;
import com.yashpatel.DocumentAnalyzer.repository.DocumentRepository;
import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;
import com.yashpatel.DocumentAnalyzer.entity.Document;
import com.yashpatel.DocumentAnalyzer.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
public UploadResponse uploadDocument(MultipartFile file) {

    try {

        String originalFileName =
                file.getOriginalFilename();

        String storedFileName =
                UUID.randomUUID() + "-" + originalFileName;

        Path storagePath =
                Paths.get("storage");

        Files.createDirectories(storagePath);

        Path filePath =
                storagePath.resolve(storedFileName);

        file.transferTo(filePath);

        Document document =
                Document.builder()
                        .originalFileName(originalFileName)
                        .storedFileName(storedFileName)
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .uploadedAt(LocalDateTime.now())
                        .build();

        document = documentRepository.save(document);

        return new UploadResponse(
                document.getId(),
                "File uploaded successfully"
        );

    } catch (Exception e) {
        throw new RuntimeException("File upload failed", e);
    }
}
}