package com.priyanshu.booking.service;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    @Autowired
    private RedissonClient redissonClient;

    private static final int MAX_REQUESTS = 5;

    public boolean isAllowed(String userEmail) {
        String key = "rate:" + userEmail;

        RAtomicLong counter = redissonClient.getAtomicLong(key);

        long count = counter.incrementAndGet();

        if (count == 1) {
            counter.expire(1, TimeUnit.SECONDS);
        }

        return count <= MAX_REQUESTS;
    }
}