package com.anton.Chapter1;

import io.rebloom.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class practice_1_8_rateLimiter {
    private Jedis jedis;


    @Before
    public void before() {
        this.jedis = new Jedis("127.0.0.1", 6379);
    }


    @After
    public void after() {
        jedis.close();
    }

    @Test
    public void t1() {
        SimpleRateLimiter rateLimiter = new SimpleRateLimiter(jedis);

        for (int i = 0; i < 100; i++) {
            System.out.println(rateLimiter.isActionAllowed("anton", "shoot", 60, 5, i));
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
