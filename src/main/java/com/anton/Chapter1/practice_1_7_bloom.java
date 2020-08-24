package com.anton.Chapter1;

import io.rebloom.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class practice_1_7_bloom {
    private Jedis jedis;


    @Before
//    public void before() {
//        this.jedis = new Jedis("127.0.0.1", 6379);
//    }


    @After
//    public void after() {
//        jedis.close();
//    }

    @Test
    public void t1() {
        long start = System.currentTimeMillis();

        Client client = new Client("127.0.0.1", 6379);

        String key = "anton_bf";

        for (int i = 0; i < 100000; i++) {
//            client.add(key, "user" + i);
//            boolean ret = client.exists(key, "user" + i);
//            if (!ret) {
//                System.out.println(i);
//                break;
//            }
        }

        System.out.println("执行用时：" + (System.currentTimeMillis() - start) / 1000);

        client.close();
    }

    @Test
    public void t2() {
        String set = jedis.set("w", "", new SetParams().nx().ex(5));
        System.out.println(set);

    }

}
