package com.yashpatel.DocumentAnalyzer.service;

import com.yashpatel.DocumentAnalyzer.entity.DocumentChunk;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkService {

    List<DocumentChunk> createChunks(
            UUID documentId,
            String text
    );

    List<DocumentChunk> getChunks(UUID documentId);
}