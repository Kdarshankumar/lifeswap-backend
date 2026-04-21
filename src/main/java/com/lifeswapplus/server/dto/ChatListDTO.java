package com.lifeswapplus.server.dto;

import com.lifeswapplus.server.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class ChatListDTO {

    private Long chatRoomId;

    @JsonIgnoreProperties({
            "password",
            "skills",
            "matches",
            "sentChats",
            "receivedChats",
            "hibernateLazyInitializer",
            "handler"
    })
    private User sender;

    @JsonIgnoreProperties({
            "password",
            "skills",
            "matches",
            "sentChats",
            "receivedChats",
            "hibernateLazyInitializer",
            "handler"
    })
    private User receiver;

    private long unreadCount;
    private String lastMessage;
    private String lastMessageTime;
    private String lastMessageType;

    public ChatListDTO(
            Long chatRoomId,
            User sender,
            User receiver,
            long unreadCount,
            String lastMessage,
            String lastMessageTime,
            String lastMessageType
    ) {
        this.chatRoomId = chatRoomId;
        this.sender = sender;
        this.receiver = receiver;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageType = lastMessageType;
    }

    public Long getChatRoomId() { return chatRoomId; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public long getUnreadCount() { return unreadCount; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
}
