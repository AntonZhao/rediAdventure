package com.anton.Chapter1;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class SimpleRateLimiter {
    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount, int temp) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long nowTime = System.currentTimeMillis();

        Pipeline pipeline = jedis.pipelined();
        pipeline.multi();
        pipeline.zadd(key, nowTime, temp + "___" + nowTime);
        pipeline.zremrangeByScore(key, 0, nowTime - period * 1000);

        Response<Long> count = pipeline.zcard(key);
        pipeline.expire(key, period + 1);

        pipeline.exec();
        pipeline.close();

        return count.get() <= maxCount;
    }
}
