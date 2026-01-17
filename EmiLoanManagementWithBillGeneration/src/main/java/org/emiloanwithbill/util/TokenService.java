package org.emiloanwithbill.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


public class TokenService {

    private static final Map<String, String> TOKENS = new ConcurrentHashMap<>();

    public static CompletableFuture<String> generateTokenAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            String token = UUID.randomUUID().toString();
            TOKENS.put(token, username);
            return token;
        });
    }

    public static CompletableFuture<Boolean> validateTokenAsync(String token) {
        return CompletableFuture.supplyAsync(() -> TOKENS.containsKey(token));
    }

    public static void removeToken(String token) {
        TOKENS.remove(token);
    }
}
