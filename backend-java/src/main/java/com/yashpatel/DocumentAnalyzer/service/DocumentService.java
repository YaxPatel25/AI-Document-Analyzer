package com.yashpatel.DocumentAnalyzer.service;

import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    UploadResponse uploadDocument(MultipartFile file);

}
