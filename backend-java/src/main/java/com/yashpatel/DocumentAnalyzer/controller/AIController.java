package com.yashpatel.DocumentAnalyzer.controller;

import com.yashpatel.DocumentAnalyzer.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/test-ai")
    public String test() {

        return aiService.summarize("""
                Java is a programming language.
                Spring Boot is used for backend development.
                PostgreSQL is a relational database.
                React is used for frontend applications.
                Docker helps package applications.
                """);
    }
}