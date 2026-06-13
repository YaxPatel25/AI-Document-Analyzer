package com.yashpatel.DocumentAnalyzer.dto;

public record AIResponse(
        String summary,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
) {
}