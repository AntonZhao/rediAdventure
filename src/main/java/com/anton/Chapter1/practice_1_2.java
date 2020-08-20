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
public class practice_1_2 {

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
        User user = new User().setName("anton").setAge(25).setSex("男").setBirth(new Date(1111));
        System.out.println(user);

        String jsonString = JSON.toJSONString(user);
        System.out.println(jsonString);

        // 直接用String来存储
        jedis.set("user_" + user.getName(), jsonString);
        User user_anton = JSON.parseObject(jedis.get("user_anton"), User.class);
        System.out.println(user_anton);
        System.out.println(user_anton.equals(user));

        // 用hash存 一个用户一个hash 或者 一个hash里存多个用户

        String key = "hash_user_" + user.getName();
        jedis.hset(key, "name", user.getName());
        jedis.hset(key, "age", user.getAge().toString());
        jedis.hset(key, "sex", user.getSex());
        jedis.hset(key, "birth", user.getBirth().toString());



    }

    public static void main(String[] args) {
//        User user = new User().setName("anton").setAge(25).setSex("男").setBirth(new Date(1111));
//        System.out.println(user);
//
//        String jsonString = JSON.toJSONString(user);
//        System.out.println(jsonString);
//        User user1 = JSON.parseObject(jsonString, User.class);
//        System.out.println(user1);
//        System.out.println(user1.equals(user));
    }

}
