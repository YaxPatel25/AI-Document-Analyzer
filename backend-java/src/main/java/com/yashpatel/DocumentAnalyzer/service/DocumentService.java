package com.yashpatel.DocumentAnalyzer.service;

import com.yashpatel.DocumentAnalyzer.dto.DocumentDetailResponse;
import com.yashpatel.DocumentAnalyzer.dto.DocumentResponse;
import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.UUID;

public interface DocumentService {

    UploadResponse uploadDocument(MultipartFile file);

    List<DocumentResponse> getAllDocuments();

    Resource downloadDocument(UUID documentId);

    DocumentDetailResponse getDocumentById(UUID id);

    DocumentDetailResponse getSummarizedDocumentById(UUID id);
}
