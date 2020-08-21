package com.anton.Chapter1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 1. 定义一个用户信息结构体，然后使用fastjson对用户信息对象进行序列化和反序列化，再使用Jedis对Redis缓存的用户信息进行存和取。
 * <p>
 * 2. 如果用hash来存，如何封装比较合适？
 */
public class practice_1_4 {

    private Jedis jedis;
    private JedisPool jedisPool;


    @Before
    public void before() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setMaxWaitMillis(1000 * 100);
        config.setTestOnBorrow(true);
        this.jedisPool = new JedisPool(config, "127.0.0.1", 6379);
    }

    @After
    public void after() {
        jedisPool.close();
    }

    @Test
    public void t1() {

        RedisDelayingQueue<String> queue = new RedisDelayingQueue<String>(jedisPool.getResource(), "q-demo3");

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                queue.delay("anton" + i);
            }
        }, "producer");

        Thread consumer1 = new Thread(() -> {
            queue.loop();
        }, "consumer1");

        Thread consumer2 = new Thread(() -> {
            queue.loop();
        }, "consumer2");

        producer.start();
        consumer1.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer2.start();

        try {
            producer.join();
            Thread.sleep(6000);
            consumer1.interrupt();
            consumer2.interrupt();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
