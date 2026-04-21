package com.lifeswapplus.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Chat room this message belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonIgnoreProperties({"sender", "receiver"})
    private ChatRoom chatRoom;

    // 🔹 Sender of the message
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String fileUrl;

    private String type;

    private LocalDateTime createdAt;

    // 🔹 Read status (ONLY receiver can change this)
    @Column(nullable = false)
    private Boolean isRead;


    // 🔹 Auto set values before saving
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.type == null) {
            this.type = "TEXT";
        }
    }

    public Message() {}

    public Message(ChatRoom chatRoom, User sender, String content, String fileUrl, String type) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.fileUrl = fileUrl;
        this.type = type;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
