package com.lifeswapplus.server.controller;

import com.lifeswapplus.server.model.ChatRoom;
import com.lifeswapplus.server.model.Message;
import com.lifeswapplus.server.service.ChatService;
import com.lifeswapplus.server.dto.ChatListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // ✅ Start chat between two users
    @PostMapping("/start")
    public ChatRoom startChat(@RequestBody Map<String, Long> body) {
        return chatService.startChat(
                body.get("senderId"),
                body.get("receiverId")
        );
    }

    // ✅ Get chat messages (page load)
    @GetMapping("/{chatRoomId}")
    public List<Message> getChatMessages(@PathVariable Long chatRoomId) {
        return chatService.getChatMessages(chatRoomId);
    }

    // ✅ Get all chats of a user
    @GetMapping("/my/{userId}")
    public List<ChatListDTO> getUserChats(@PathVariable Long userId) {
        return chatService.getUserChatList(userId);
    }

    // ✅ Mark messages as READ
    @PutMapping("/read/{chatRoomId}/{userId}")
    public void markChatAsRead(
            @PathVariable Long chatRoomId,
            @PathVariable Long userId
    ) {
        chatService.markChatAsRead(chatRoomId, userId);
    }
}
