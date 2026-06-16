package com.devpath.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    public String ask(String question){
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
