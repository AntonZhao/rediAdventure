package com.anton.Chapter1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

public class RedisDelayingQueue<T> {

    private final String FAILED_REMOVE = "FAILED_REMOVE";
    private final String VALUES_EMPTY = "VALUES_EMPTY";

    static class TaskItem<T> {
        public String id;
        public T msg;
    }

    // fastjson序列化对象存在generic类型时，需要使用TypeReference
    private Type TaskType = new TypeReference<TaskItem<T>>() {
    }.getType();

    private Jedis jedis;
    private String queueKey;

    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }

    public void delay(T msg) {
        TaskItem<T> task = new TaskItem<>();
        // 分配唯一uuid
        task.id = UUID.randomUUID().toString();
        task.msg = msg;
        // fastjson序列化
        String s = JSON.toJSONString(task);
        // 塞入延时队列，5s后再试
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, s);
    }

    public void loop() {
        while (!Thread.interrupted()) {
            // 只取一条
//            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
//            if (values.isEmpty()) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                continue;
//            }
//            String s = values.iterator().next();
//            System.out.println(s);
//            if (jedis.zrem(queueKey, s) > 0) {
//                // fastjson 反序列化
//                TaskItem<T> task = JSON.parseObject(s, TaskType);
//                this.handleMsg(task.msg);
//            }
            String lua = "local temp = redis.call('zrangebyscore', KEYS[1], '0', ARGV[1], 'limit', '0', '1')\n" +
                    "if #temp > 0 then\n" +
                    "    if redis.call('zrem', KEYS[1], temp[1]) > 0 then\n" +
                    "        return temp[1];\n" +
                    "    else\n" +
                    "        return 'FAILED_REMOVE'\n" +
                    "    end\n" +
                    "else\n" +
                    "    return 'VALUES_EMPTY'\n" +
                    "end";
            String eval = jedis.eval(lua, 1, queueKey, System.currentTimeMillis() + "").toString();
            if (VALUES_EMPTY.equals(eval)) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (FAILED_REMOVE.equals(eval)) {
                System.out.println("hahah");
                continue;
            }
            TaskItem<T> task = JSON.parseObject(eval, TaskType);
            this.handleMsg(task.msg);
        }
    }

    private void handleMsg(T msg) {
        System.out.println(Thread.currentThread().getName() + " 消费了 " + msg);
    }
}
