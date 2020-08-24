package com.anton.Chapter1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class practice_1_6 {
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
        String key = "anton_hyperloglog";

        jedis.del(key);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 999999; i++) {
            jedis.pfadd(key, "user" + i);
        }

        long total = jedis.pfcount(key);
        System.out.printf("%d %d\n", 999999, total);
        System.out.println((System.currentTimeMillis() - start) / 1000);
    }

    public static void main(String[] args) {

    }
}
