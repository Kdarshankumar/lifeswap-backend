package com.lifeswapplus.server.presence;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Service
public class UserPresenceService {

    private final ConcurrentHashMap<String, Integer> userSessions = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> lastSeen = new ConcurrentHashMap<>();

    // User comes online
    public void userConnected(String userId) {
        userSessions.put(userId, userSessions.getOrDefault(userId, 0) + 1);
    }

    // User goes offline
    public void userDisconnected(String userId) {
        if (!userSessions.containsKey(userId)) return;

        int count = userSessions.get(userId) - 1;

        if (count <= 0) {
            userSessions.remove(userId);

            lastSeen.put(userId, System.currentTimeMillis());
        } else {
            userSessions.put(userId, count);
        }
    }

    public boolean isOnline(String userId) {
        return userSessions.containsKey(userId);
    }

    public Set<String> getOnlineUsers() {
        return userSessions.keySet();
    }

    public Long getLastSeen(String userId) {
        return lastSeen.get(userId);
    }

}