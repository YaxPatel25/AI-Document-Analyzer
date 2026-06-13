package com.yashpatel.DocumentAnalyzer.service;

import com.yashpatel.DocumentAnalyzer.dto.AIResponse;

public interface AIService {
    AIResponse summarize(String text);
}