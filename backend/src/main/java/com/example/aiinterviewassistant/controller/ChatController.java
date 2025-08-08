package com.example.aiinterviewassistant.controller;

import com.example.aiinterviewassistant.service.DoubaoService;
import com.example.aiinterviewassistant.utils.Types.ChatMessage;
import com.example.aiinterviewassistant.utils.Types.ChatRequest;
import com.example.aiinterviewassistant.utils.Types.ChatResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final DoubaoService doubaoService;

    @Value("${assistant.systemPrompt:你是一个专业的面试官，擅长结构化提问与基于STAR法的追问，回答请简洁、条理清晰，并在必要时给出改进建议。}")
    private String systemPrompt;

    public ChatController(DoubaoService doubaoService) {
        this.doubaoService = doubaoService;
    }

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        List<ChatMessage> msgs = new ArrayList<>();
        ChatMessage sys = new ChatMessage();
        sys.role = "system";
        sys.content = systemPrompt;
        msgs.add(sys);
        if (request.messages != null) {
            msgs.addAll(request.messages);
        }
        ChatRequest merged = new ChatRequest();
        merged.messages = msgs;
        merged.temperature = request.temperature;
        merged.maxTokens = request.maxTokens;

        return doubaoService.createChatCompletion(merged);
    }
}
