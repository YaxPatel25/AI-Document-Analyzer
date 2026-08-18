package com.yashpatel.DocumentAnalyzer.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextChunker {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_CHUNK_OVERLAP = 200;

    public List<String> chunk(String text) {

        return chunk(
                text,
                DEFAULT_CHUNK_SIZE,
                DEFAULT_CHUNK_OVERLAP
        );
    }

    public List<String> chunk(
            String text,
            int chunkSize,
            int overlap) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        if (chunkSize <= 0) {
            throw new IllegalArgumentException(
                    "Chunk size must be greater than zero"
            );
        }

        if (overlap < 0 || overlap >= chunkSize) {
            throw new IllegalArgumentException(
                    "Overlap must be >= 0 and < chunk size"
            );
        }

        List<String> chunks = new ArrayList<>();

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + chunkSize,
                    text.length()
            );

            String chunk = text
                    .substring(start, end)
                    .trim();

            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }

            if (end == text.length()) {
                break;
            }

            start = end - overlap;
        }

        return chunks;
    }
}