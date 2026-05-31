package com.yashpatel.DocumentAnalyzer.repository;

import com.yashpatel.DocumentAnalyzer.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository
        extends JpaRepository<Document, UUID> {
}