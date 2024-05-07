package edu.usc.csci310.project.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 3;
    private static final int ATTEMPT_INCREMENT = 1;
    private static final int BLOCK_DURATION_MINUTES = 1;
    private static final int ATTEMPT_EXPIRATION_SECONDS = 30;

    private LoadingCache<String, Integer> attemptCache;
    private LoadingCache<String, Boolean> blockCache;

    public LoadingCache<String, Integer> getAttemptCache() {
        return this.attemptCache;
    }

    public LoadingCache<String, Boolean> getBlockCache() {
        return this.blockCache;
    }

    public LoginAttemptService() {
        attemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(ATTEMPT_EXPIRATION_SECONDS, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });

        blockCache = CacheBuilder.newBuilder()
                .expireAfterWrite(BLOCK_DURATION_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Boolean>() {
                    public Boolean load(String key) {
                        return false;
                    }
                });
    }

    public void loginSucceeded(String key) {
        attemptCache.invalidate(key);
        blockCache.invalidate(key);
    }

    public void loginFailed(String key) {
        Integer attempts = attemptCache.getIfPresent(key);
        if (attempts == null) {
            attempts = 0;
        }
        attempts += ATTEMPT_INCREMENT;
        attemptCache.put(key, attempts);
        if (attempts >= MAXIMUM_NUMBER_OF_ATTEMPTS) {
            blockCache.put(key, true);
        }
    }

    public boolean isBlocked(String key) {
        Boolean isBlocked = blockCache.getIfPresent(key);
        return isBlocked != null && isBlocked;
    }
}
