package com.lifeswapplus.server.presence;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    private UserPresenceService presenceService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // STEP 1: Try from session first
        String userId = (String) accessor.getSessionAttributes().get("userId");

        //  STEP 2: Fallback → from headers (first time connect)
        if (userId == null) {
            userId = accessor.getFirstNativeHeader("userId");

            if (userId != null) {
                accessor.getSessionAttributes().put("userId", userId);
            }
        }

        System.out.println("CONNECT HEADERS: " + accessor.toNativeHeaderMap());
        System.out.println("USER ID FOUND: " + userId);

        if (userId != null) {
            presenceService.userConnected(userId);

            // ✅ 1. send full list FIRST
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/presence",
                    presenceService.getOnlineUsers()
            );

            // ✅ 2. then broadcast
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    Map.of("userId", userId, "online", true)
            );

            System.out.println(userId + " ONLINE");
        } else {
            System.out.println("❌ userId is NULL on connect!");
        }
    }
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("userId");

        if (userId != null) {

            // 🔥 DELAY DISCONNECT (CRITICAL FIX)
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // wait 1 sec

                    presenceService.userDisconnected(userId);

                    boolean stillOnline = presenceService.isOnline(userId);

                    if (!stillOnline) {
                        messagingTemplate.convertAndSend(
                                "/topic/presence",
                                Map.of(
                                        "userId", userId,
                                        "online", false,
                                        "lastSeen", System.currentTimeMillis()
                                )
                        );

                        System.out.println(userId + " OFFLINE");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}