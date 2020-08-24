package com.anton.Chapter1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class practice_1_9_funnelRateLimiter {
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
    public void t1() throws InterruptedException {
        FunnelRateLimiter funnelRateLimiter = new FunnelRateLimiter();

//        for (int i = 0; i < 20; i++) {
//            System.out.println(funnelRateLimiter.isActionAllowed("anton", "reply", 15, 0.5f));
//        }

        while (true) {
            Thread.sleep(1000);
            System.out.println(funnelRateLimiter.isActionAllowed("anton", "reply", 15, 0.5f));
        }
    }
}
