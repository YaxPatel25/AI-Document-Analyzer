package com.yashpatel.DocumentAnalyzer.service.impl;

import com.yashpatel.DocumentAnalyzer.config.GroqConfig;
import com.yashpatel.DocumentAnalyzer.dto.groq.GroqRequest;
import com.yashpatel.DocumentAnalyzer.dto.groq.GroqResponse;
import com.yashpatel.DocumentAnalyzer.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroqService implements AIService {

    private final GroqConfig groqConfig;
    private final RestClient restClient;

    // Groq free tier limits
    private static final int MAX_INPUT_CHARACTERS = 24000; // ~6000 tokens
    private static final int MAX_OUTPUT_TOKENS = 1024;
    private static final long MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024; // 20MB

    @Override
    public String summarize(String text) {
        try {
            // Check text size
            if (text == null || text.isBlank()) {
                return "No extractable text found in this document.";
            }

            if (text.length() > MAX_INPUT_CHARACTERS) {
                return "Document text is too large to summarize. Maximum allowed size is "
                        + MAX_INPUT_CHARACTERS + " characters. Your document has "
                        + text.length() + " characters. Please upload a smaller document.";
            }
            String prompt = """
                    Summarize this text in few lines:

                    %s
                    """.formatted(text);

            GroqRequest request = new GroqRequest(
                    groqConfig.getModel(),
                    List.of(
                            new GroqRequest.Message("system",
                                    "You are a professional document summarizer. Always respond in clear bullet points."),
                            new GroqRequest.Message("user", prompt)),
                    0.3,
                    MAX_OUTPUT_TOKENS);

            GroqResponse response = restClient.post()
                    .uri("https://api.groq.com/openai/v1/chat/completions")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + groqConfig.getApiKey())
                    .body(request)
                    .retrieve()
                    .body(GroqResponse.class);
            System.out.println("API Response: " + response.getUsage());

            return response.getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new RuntimeException("Groq API quota exceeded. Please try again later.");
        } catch (Exception e) {
            throw new RuntimeException("AI summarization failed: " + e.getMessage());
        }
    }

    // Call this from DocumentServiceImpl before summarize()
    public String validateFileSize(long fileSizeBytes) {
        if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
            return "File too large. Maximum allowed size is 20MB. Your file is "
                    + (fileSizeBytes / (1024 * 1024)) + "MB.";
        }
        return null;
    }
}