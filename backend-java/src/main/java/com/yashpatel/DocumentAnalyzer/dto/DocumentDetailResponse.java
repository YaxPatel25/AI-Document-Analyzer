package com.yashpatel.DocumentAnalyzer.dto;

import java.util.UUID;

public record DocumentDetailResponse(
        UUID id,
        String originalFileName,
        String extractedText
) {
}