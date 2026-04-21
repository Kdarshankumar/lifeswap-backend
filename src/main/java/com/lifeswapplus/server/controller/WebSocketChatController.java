package com.lifeswapplus.server.controller;

import com.lifeswapplus.server.dto.ChatMessageDTO;
import com.lifeswapplus.server.model.Message;
import com.lifeswapplus.server.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /* ---------------- TYPING ---------------- */
    @MessageMapping("/chat.typing")
    public void typing(@Payload Map<String, Object> payload) {

        Long chatRoomId = Long.valueOf(payload.get("chatRoomId").toString());

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/typing",
                payload
        );
    }

    /* ---------------- SEND MESSAGE ---------------- */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO dto) {

        // 1️⃣ Save message (DB = source of truth)
        Message saved = chatService.sendMessage(
                dto.getChatRoomId(),
                dto.getSenderId(),
                dto.getContent(),
                dto.getFileUrl(),
                dto.getType()
        );

        // 2️⃣ Push message to chat screen
        messagingTemplate.convertAndSend(
                "/topic/chat/" + dto.getChatRoomId(),
                Map.of(
                        "id", saved.getId(),
                        "content", saved.getContent(),
                        "fileUrl", saved.getFileUrl(),
                        "type", saved.getType(),
                        "sender", saved.getSender(),
                        "createdAt", saved.getCreatedAt(),
                        "clientId", dto.getClientId()
                )
        );
    }
}
