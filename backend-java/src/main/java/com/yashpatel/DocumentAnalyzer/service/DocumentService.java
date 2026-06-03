package com.yashpatel.DocumentAnalyzer.service;

import com.yashpatel.DocumentAnalyzer.dto.DocumentResponse;
import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    UploadResponse uploadDocument(MultipartFile file);
    List<DocumentResponse> getAllDocuments();

}
