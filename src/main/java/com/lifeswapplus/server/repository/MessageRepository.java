package com.lifeswapplus.server.repository;

import com.lifeswapplus.server.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 🔹 Get all messages in a chat sorted by time
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // ✅ Mark messages as READ (receiver opened chat)
    @Transactional
    @Modifying
    @Query("""
        UPDATE Message m
        SET m.isRead = true
        WHERE m.chatRoom.id = :chatRoomId
          AND m.sender.id <> :userId
          AND m.isRead = false
    """)
    void markMessagesAsRead(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId
    );

    // 🔹 Count unread messages for a user in a chat
    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.chatRoom.id = :chatRoomId
          AND m.sender.id <> :userId
          AND m.isRead = false
    """)
    long countUnreadMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId
    );

    // 🔹 Get last message of a chat
    @Query("""
    SELECT m
    FROM Message m
    WHERE m.chatRoom.id = :chatRoomId
    ORDER BY m.createdAt DESC
    LIMIT 1
""")
    Message findLastMessage(@Param("chatRoomId") Long chatRoomId);

    // 🔥 Get last READ message id (receiver side)
    @Query("""
    SELECT MAX(m.id)
    FROM Message m
    WHERE m.chatRoom.id = :chatRoomId
      AND m.sender.id <> :userId
      AND m.isRead = true
""")
    Long findLastReadMessageId(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId
    );

}
