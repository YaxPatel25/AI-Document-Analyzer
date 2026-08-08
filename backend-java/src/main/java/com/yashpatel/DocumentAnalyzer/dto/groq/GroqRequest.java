package com.yashpatel.DocumentAnalyzer.dto.groq;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class GroqRequest {

    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;

    @Data
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}