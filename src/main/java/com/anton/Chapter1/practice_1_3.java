package com.anton.Chapter1;

import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * 1. 定义一个用户信息结构体，然后使用fastjson对用户信息对象进行序列化和反序列化，再使用Jedis对Redis缓存的用户信息进行存和取。
 * <p>
 * 2. 如果用hash来存，如何封装比较合适？
 */
public class practice_1_3 {

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

        RedisWithReentrantLock RedisLock = new RedisWithReentrantLock(jedis);

        Thread t1 = new Thread(() -> {
            System.out.println(RedisLock.lock("anton"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(RedisLock.unlock("anton"));
        }, "t1");

        Thread t2 = new Thread(() -> {
            System.out.println(RedisLock.lock("anton"));
            if (RedisLock.lock("anton") == true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(RedisLock.unlock("anton"));
            } else {
                System.out.println(Thread.currentThread().getName() + " 没有拿到锁");
            }
        }, "t2");

        t1.start();
        Thread.sleep(1000);
        t2.start();

        t1.join();
        t2.join();
    }


}
