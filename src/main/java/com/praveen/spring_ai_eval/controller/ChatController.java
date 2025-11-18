package com.praveen.spring_ai_eval.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;


    @Value("classpath:/promptTemplates/hrPolicy.st")
    private Resource hrPolicyTemplate;


    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam("message") String message
    ) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/prompt-stuffing")
    public String promptStuffing(
            @RequestParam("message") String message
    ){
        return chatClient
                .prompt()
                .system(hrPolicyTemplate)
                .user(message)
                .call()
                .content();
    }
}
