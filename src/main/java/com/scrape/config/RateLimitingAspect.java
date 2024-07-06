package com.scrape.config;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import com.scrape.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class RateLimitingAspect {

    @Autowired
    private HttpServletRequest request;

    private static final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private static final int REQUEST_LIMIT = 1;
    private static final long TIME_LIMIT = 3000; // 3 seconds

    @Before("@annotation(RateLimited)")
    public void beforeRequest() {
        String userIpAddress = request.getRemoteAddr();
        AtomicInteger count = requestCounts.computeIfAbsent(userIpAddress, k -> new AtomicInteger(0));
        if (count.incrementAndGet() > REQUEST_LIMIT) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }
        if (requestCounts.size() == 1) {
            resetRequestCounts();
        }
    }

    private void resetRequestCounts() {
        new Thread(() -> {
            try {
                Thread.sleep(TIME_LIMIT);
                requestCounts.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
