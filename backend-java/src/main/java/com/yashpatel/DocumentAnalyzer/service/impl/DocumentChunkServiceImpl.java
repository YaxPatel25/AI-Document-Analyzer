package com.yashpatel.DocumentAnalyzer.service.impl;

import com.yashpatel.DocumentAnalyzer.entity.Document;
import com.yashpatel.DocumentAnalyzer.entity.DocumentChunk;
import com.yashpatel.DocumentAnalyzer.repository.DocumentChunkRepository;
import com.yashpatel.DocumentAnalyzer.repository.DocumentRepository;
import com.yashpatel.DocumentAnalyzer.service.DocumentChunkService;
import com.yashpatel.DocumentAnalyzer.util.TextChunker;
import com.yashpatel.DocumentAnalyzer.util.TextPreprocessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DocumentChunkServiceImpl
        implements DocumentChunkService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final TextPreprocessor textPreprocessor;
    private final TextChunker textChunker;

    @Override
    public List<DocumentChunk> createChunks(
            UUID documentId,
            String text) {

        Document document = documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));

        String cleanedText =
                textPreprocessor.clean(text);

        List<String> chunks =
                textChunker.chunk(cleanedText);

        List<DocumentChunk> documentChunks =
                new java.util.ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {

            String chunkContent = chunks.get(i);

            DocumentChunk chunk = DocumentChunk.builder()
                    .document(document)
                    .chunkIndex(i)
                    .content(chunkContent)
                    .characterCount(chunkContent.length())
                    .build();

            documentChunks.add(chunk);
        }

        return documentChunkRepository.saveAll(
                documentChunks
        );
    }

    @Override
    public List<DocumentChunk> getChunks(UUID documentId) {

        return documentChunkRepository
                .findByDocumentIdOrderByChunkIndex(documentId);
    }
}