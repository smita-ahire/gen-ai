package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;
    @GetMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestParam("prompt") String prompt) {

        return ResponseEntity.ok(Map.of("response",chatService.getChat(prompt)));
    }

}

