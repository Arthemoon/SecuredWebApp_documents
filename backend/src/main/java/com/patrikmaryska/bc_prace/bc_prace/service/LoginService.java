package com.patrikmaryska.bc_prace.bc_prace.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

@Service
public class LoginService {

    private final int MAX_ATTEMPT = 5;

    private LoadingCache<String, Integer> attemptsCache;
    private static Logger loginLogger = LoggerFactory.getLogger("login");


    public LoginService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(4, TimeUnit.HOURS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void loginSucceeded(String username, String key) {
        attemptsCache.invalidate(key);
        loginLogger.info("Successful login:  " + username + " ip address:" + key);
    }

    public void loginFailed(String username, String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
        loginLogger.warn("Unsuccessful login:  " + username + " ip address:" + key);
    }

    public boolean isBlocked(String key) {
         try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
