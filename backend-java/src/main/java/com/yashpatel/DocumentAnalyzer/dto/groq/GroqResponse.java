package com.yashpatel.DocumentAnalyzer.dto.groq;

import lombok.Data;
import java.util.List;

@Data
public class GroqResponse {

    private List<Choice> choices;

    private Usage usage;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String content;
    }

    @Data
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}