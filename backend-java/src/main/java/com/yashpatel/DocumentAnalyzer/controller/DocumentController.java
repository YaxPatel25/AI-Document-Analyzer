package com.yashpatel.DocumentAnalyzer.controller;

import com.yashpatel.DocumentAnalyzer.dto.UploadResponse;
import com.yashpatel.DocumentAnalyzer.service.DocumentService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import com.yashpatel.DocumentAnalyzer.dto.DocumentResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file
    ) {

        UploadResponse response =
                documentService.uploadDocument(file);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {

        return ResponseEntity.ok(
                documentService.getAllDocuments()
        );
    }
}
