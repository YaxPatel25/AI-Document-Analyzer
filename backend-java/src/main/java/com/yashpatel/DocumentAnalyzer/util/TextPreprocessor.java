package com.yashpatel.DocumentAnalyzer.util;

import org.springframework.stereotype.Component;

@Component
public class TextPreprocessor {

    public String clean(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        String cleaned = text;

        // Normalize Windows line endings
        cleaned = cleaned.replace("\r\n", "\n");

        // Normalize carriage returns
        cleaned = cleaned.replace("\r", "\n");

        // Remove excessive spaces/tabs
        cleaned = cleaned.replaceAll("[ \\t]+", " ");

        // Remove excessive blank lines
        cleaned = cleaned.replaceAll("\\n{3,}", "\n\n");

        // Remove spaces around newlines
        cleaned = cleaned.replaceAll(" *\\n *", "\n");

        return cleaned.trim();
    }
}