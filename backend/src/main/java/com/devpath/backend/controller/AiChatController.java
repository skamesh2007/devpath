package com.devpath.backend.controller;

import com.devpath.backend.service.AiChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@CrossOrigin()
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService){
        this.aiChatService = aiChatService;
    }


    @GetMapping("/ask")
    public String ask(@RequestParam String question){
        return aiChatService.ask(question);
    }

}
