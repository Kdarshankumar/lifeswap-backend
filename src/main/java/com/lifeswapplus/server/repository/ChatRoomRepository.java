package com.lifeswapplus.server.repository;

import com.lifeswapplus.server.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<ChatRoom> findBySenderIdOrReceiverId(Long senderId, Long receiverId);

    @Query("""
        SELECT cr
        FROM ChatRoom cr
        JOIN FETCH cr.sender
        JOIN FETCH cr.receiver
        WHERE cr.sender.id = :userId OR cr.receiver.id = :userId
    """)
    List<ChatRoom> findChatRoomsWithUsers(@Param("userId") Long userId);
}
