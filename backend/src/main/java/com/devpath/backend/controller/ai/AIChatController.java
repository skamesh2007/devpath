package com.devpath.backend.controller.ai;

import com.devpath.backend.service.ai.AIChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AIChatController {

    private final AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return aiChatService.ask(question);
    }
}