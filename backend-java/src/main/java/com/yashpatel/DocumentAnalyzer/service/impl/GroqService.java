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

    @Override
    public String summarize(String text) {
        try {
            String prompt = """
                    Summarize this text in few lines:

                    %s
                    """.formatted(text);

            GroqRequest request = new GroqRequest(
                    groqConfig.getModel(),
                    List.of(
                            new GroqRequest.Message(
                                    "user",
                                    prompt)));

            GroqResponse response = restClient.post()
                    .uri("https://api.groq.com/openai/v1/chat/completions")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + groqConfig.getApiKey())
                    .body(request)
                    .retrieve()
                    .body(GroqResponse.class);

            return response.choices()
                    .getFirst()
                    .message()
                    .content();
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new RuntimeException("Groq API quota exceeded. Please try again later.");
        } catch (Exception e) {
            throw new RuntimeException("AI summarization failed: " + e.getMessage());
        }
    }
}