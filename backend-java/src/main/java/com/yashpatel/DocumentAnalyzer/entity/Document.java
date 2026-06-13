package com.yashpatel.DocumentAnalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalFileName;

    private String storedFileName;

    private Long fileSize;

    private String contentType;

    private LocalDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(columnDefinition = "TEXT")
    private String aiAnalysis;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;
}