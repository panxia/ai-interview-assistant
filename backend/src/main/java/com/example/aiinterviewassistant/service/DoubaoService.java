package com.example.aiinterviewassistant.service;

import com.example.aiinterviewassistant.utils.Types.ChatMessage;
import com.example.aiinterviewassistant.utils.Types.ChatRequest;
import com.example.aiinterviewassistant.utils.Types.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoubaoService {

    private final WebClient webClient;

    @Value("${doubao.model}")
    private String modelId;

    public DoubaoService(
            @Value("${doubao.apiBase:https://ark.cn-beijing.volces.com/api/v3}") String apiBase,
            @Value("${doubao.apiKey}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(apiBase)
                .defaultHeader("Authorization", "Bearer " + (apiKey == null ? "" : apiKey))
                .build();
    }

    public Mono<ChatResponse> createChatCompletion(ChatRequest requestBody) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", modelId);
        payload.put("messages", mapMessages(requestBody.messages));
        if (requestBody.temperature != null) {
            payload.put("temperature", requestBody.temperature);
        }
        if (requestBody.maxTokens != null) {
            payload.put("max_tokens", requestBody.maxTokens);
        }
        payload.put("stream", false);

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(ChatResponse.class);
    }

    private List<Map<String, String>> mapMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(m -> {
                    Map<String, String> mm = new HashMap<>();
                    mm.put("role", m.role);
                    mm.put("content", m.content);
                    return mm;
                })
                .toList();
    }
}
