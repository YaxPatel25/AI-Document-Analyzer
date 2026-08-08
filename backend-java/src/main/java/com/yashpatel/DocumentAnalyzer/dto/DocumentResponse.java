package com.yashpatel.DocumentAnalyzer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.yashpatel.DocumentAnalyzer.entity.DocumentStatus;

public record DocumentResponse(
        UUID id,
        String originalFileName,
        String contentType,
        Long fileSize,
        LocalDateTime uploadedAt,
        DocumentStatus status,
        String errorMessage) {
}