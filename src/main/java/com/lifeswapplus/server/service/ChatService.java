package com.lifeswapplus.server.service;

import com.lifeswapplus.server.model.ChatRoom;
import com.lifeswapplus.server.model.Message;
import com.lifeswapplus.server.model.User;
import com.lifeswapplus.server.repository.ChatRoomRepository;
import com.lifeswapplus.server.repository.MessageRepository;
import com.lifeswapplus.server.repository.UserRepository;
import com.lifeswapplus.server.dto.ChatListDTO;
import com.lifeswapplus.server.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /* =========================
       MARK CHAT AS READ (✓✓)
       ========================= */
    @Transactional
    public void markChatAsRead(Long chatRoomId, Long userId) {

        // 1️⃣ Mark unread messages as read (receiver side)
        messageRepository.markMessagesAsRead(chatRoomId, userId);

        // 2️⃣ Get last READ message id (correct logic)
        Long lastReadMessageId =
                messageRepository.findLastReadMessageId(chatRoomId, userId);

        // 3️⃣ Notify sender with exact read boundary
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId + "/read",
                Map.of(
                        "chatRoomId", chatRoomId,
                        "readerId", userId,
                        "lastReadMessageId", lastReadMessageId
                )
        );
    }

    /* =========================
       Start or get chat room
       ========================= */
    public ChatRoom startChat(Long senderId, Long receiverId) {

        ChatRoom existingRoom =
                chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                        .orElse(chatRoomRepository
                                .findBySenderIdAndReceiverId(receiverId, senderId)
                                .orElse(null));

        if (existingRoom != null) return existingRoom;

        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        ChatRoom room = new ChatRoom();
        room.setSender(sender);
        room.setReceiver(receiver);

        return chatRoomRepository.save(room);
    }

    /* =========================
       SAVE MESSAGE (WebSocket)
       ========================= */
    public Message saveWebSocketMessage(Message message) {

        ChatRoom room = chatRoomRepository
                .findById(message.getChatRoom().getId())
                .orElseThrow();

        User sender = userRepository
                .findById(message.getSender().getId())
                .orElseThrow();

        message.setChatRoom(room);
        message.setSender(sender);
        //message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(false);// ALWAYS unread on creation

        Message saved = messageRepository.save(message);

        Long receiverId =
                sender.getId().equals(room.getSender().getId())
                        ? room.getReceiver().getId()
                        : room.getSender().getId();

        // 🔥 Notify receiver (unread increment)
        messagingTemplate.convertAndSend(
                "/topic/chat/user/" + receiverId,
                saved
        );

        return saved;
    }

    /* =========================
       Get messages
       ========================= */
    public List<Message> getChatMessages(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    /* =========================
       Chat list
       ========================= */
    public List<ChatListDTO> getUserChatList(Long userId) {

        List<ChatRoom> rooms =
                chatRoomRepository.findChatRoomsWithUsers(userId);


        return rooms.stream().map(room -> {

            long unreadCount =
                    messageRepository.countUnreadMessages(room.getId(), userId);

            Message lastMessage =
                    messageRepository.findLastMessage(room.getId());

            String lastMsgText = null;
            String lastMsgTime = null;
            String lastMsgType = null;

            if (lastMessage != null) {
                lastMsgText = lastMessage.getContent();
                lastMsgTime = lastMessage.getCreatedAt().toString();
                lastMsgType = lastMessage.getType();
            }

            return new ChatListDTO(
                    room.getId(),
                    room.getSender(),
                    room.getReceiver(),
                    unreadCount,
                    lastMsgText,
                    lastMsgTime,
                    lastMsgType
            );
        }).toList();
    }

    /* =========================
       Compatibility method
       ========================= */
    public Message sendMessage(Long chatRoomId, Long senderId, String content, String fileUrl, String type) {

        ChatRoom room = chatRoomRepository.findById(chatRoomId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();

        Message message = new Message();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(content);
        message.setContent(content);

        if (fileUrl != null && !fileUrl.isEmpty()) {
            message.setFileUrl(fileUrl);
            message.setType(type);
        } else {
            message.setFileUrl(null);
            message.setType("TEXT");
        }

        return saveWebSocketMessage(message);
    }
}
