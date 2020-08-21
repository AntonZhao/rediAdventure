## 1. Redis基础数据结构

### Redis安装

```bash
# 去仓库拉指定版本的redis
docker pull redis:4.0.10
# 一定要下载对应版本的配置文件
docker run -p 6379:6379 --name redis4_10 -v ~/Software/docker/redis.conf:/etc/redis/redis.conf -v ~/Software/docker/data:/data -d redis:4.0.10 redis-server /etc/redis/redis.conf --appendonly yes
```

### 5种基础数据结构

**String**

动态字符串，冗余分配方式。

小于1MB，成倍扩容，大于1MB则一次加1MB，最大512MB

**List**

就像java里的`LinkedList`，双向链表。

插入删除是O(1)，索引定位是O(N)

- 元素比较少的时候，使用`ziplist`，压缩列表，一块连续内存存储
- 元素比较多的时候，使用`quicklist`，就是使用双向指针串起来多个ziplist

**Hash**

数组 + 链表

为了不堵塞服务，使用渐进式rehash结构，rehash的时候保留新旧两个hash，查询时会同时查询两个hash结构？，循序渐进的迁移知道完成。

优点：如果使用hash存储用户信息，可以部分获取，相比String保存整个用户信息且必须一次性读取，可以节省流量。

缺点：hash结构存储消耗高于单个字符串。

**Set**

一个 value 为 null 的特殊字典。

**ZSet**

score ：排序权重，数据结构：跳表

每个元素里都有 L0,L1,L2 多级，可以这么理解 L0 是最底下一层，间隔是0；L1是倒数第二层，间隔是1.。。。

跳表采用一个随机策略决定新元素可以拥有多少层。

### 容器型数据结构通用规则

1. 如果没有就创建
2. 如果没了就删除

### 过期时间

过期的单位是对象

重新set会使过期时间消失

## 2. redis 实现分布式锁

就是利用 `set key value ex [time] nx`

超时问题：lua脚本
```lua
if redis.call("get",KEYS[1]) == ARGV[1] then
    return redis.call("del",KEYS[1])
else
    return 0
end
```

可重入怎么实现：笨，ThreadLocal呀
- 参考 com.anton.Chapter1.RedisWithReentrantLock

## 3. redis 实现消息队列

利用 list 的 `lpush和rpop` || `rpush和lpop`

队列空了咋办？
- 加b呀，blpop/brpop/blpush/brpush
- b就是blocking，可以阻塞

锁冲突了咋办嘛？
- 实现一个延时队列 
- 参考 com.anton.Chapter1.RedisDelayingQueue