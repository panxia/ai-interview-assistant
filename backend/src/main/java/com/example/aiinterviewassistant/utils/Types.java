package com.example.aiinterviewassistant.utils;

import java.util.List;

public class Types {
    public static class ChatMessage {
        public String role;
        public String content;
    }

    public static class ChatRequest {
        public List<ChatMessage> messages;
        public Double temperature;
        public Integer maxTokens;
    }

    public static class Choice {
        public int index;
        public ChatMessage message;
        public String finish_reason;
    }

    public static class Usage {
        public Integer prompt_tokens;
        public Integer completion_tokens;
        public Integer total_tokens;
    }

    public static class ChatResponse {
        public String id;
        public String object;
        public Long created;
        public List<Choice> choices;
        public Usage usage;
    }
}
