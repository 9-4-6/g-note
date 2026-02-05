# Redis 与 MySQL 数据一致性解决方案
**核心原则**：先更 MySQL，再删 Redis（规避绝大多数并发脏数据问题）
**一致性选型**：90% 业务用**最终一致性**，仅金融/库存/订单等核心场景用**强一致性**

## 一、核心问题
### 问题1
更新 MySQL 成功，删除 Redis 失败 → 缓存留存旧值
### 问题2
并发脏写：缓存失效后，线程A查MySQL旧值未写完，线程B更MySQL删Redis，最终A将旧值写入Redis

## 二、最终一致性解决方案（主流）
### 方案1：延迟双删（轻量，中小型/读多写少业务）
#### 执行流程
1. 更新 MySQL 成功
2. 立即删除 Redis 对应 key（第一次）
3. 异步延迟 **500ms-2s** 再删一次 Redis（第二次，清空并发旧值）
#### 关键
- 延迟时间 > 业务查MySQL+写Redis耗时
- 删Redis失败做**3次内指数退避重试**，失败记日志兜底
#### 极简伪代码（Java）
```java
if (mysqlMapper.update(data) > 0) {
    String key = "user:info:" + data.getId();
    redisTemplate.delete(key); // 立即删
    executorService.submit(() -> {
        Thread.sleep(1000);
        redisTemplate.delete(key); // 延迟删
    });
}
```

### 方案2：Canal + Binlog 同步（分布式，中大型/微服务业务）
#### 核心原理
监听 MySQL Binlog 日志，解析更新事件自动同步 Redis，解耦应用层缓存操作
#### 架构流程
MySQL(开启ROW格式Binlog) → Canal 解析 → MQ 削峰 → 消费端删/更 Redis
#### 优势
分布式一致、容错性强、应用层无需写缓存代码，失败依赖MQ重试+死信告警

## 三、强一致性解决方案（核心场景专用）
### 实现核心
**Redisson分布式锁**（锁key与缓存key一致），将读/写操作串行化，保证原子性
### 1. 读缓存强一致（解决并发脏写）
```java
public UserInfo get(Long id) {
    String key = "user:info:" + id;
    UserInfo info = redisTemplate.opsForValue().get(key);
    if (info != null) return info;
    RLock lock = redissonClient.getLock(key);
    try {
        if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
            // 双重检查缓存
            if ((info = redisTemplate.opsForValue().get(key)) != null) return info;
            info = mysqlMapper.selectById(id);
            if (info != null) redisTemplate.opsForValue().set(key, info, 30, TimeUnit.MINUTES);
            return info;
        }
        Thread.sleep(100);
        return get(id); // 重试
    } finally { if (lock.isHeldByCurrentThread()) lock.unlock(); }
}
```
### 2. 写数据库强一致（解决更新并发）
```java
public boolean update(UserInfo data) {
    String key = "user:info:" + data.getId();
    RLock lock = redissonClient.getLock(key);
    try {
        if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
            if (mysqlMapper.updateById(data) > 0) {
                redisTemplate.delete(key);
                return true;
            }
            return false;
        }
        Thread.sleep(100);
        return update(data); // 重试
    } finally { if (lock.isHeldByCurrentThread()) lock.unlock(); }
}
```
### 代价
串行化降低并发度、引入分布式锁复杂度、依赖Redis集群规避单点

## 四、方案对比&选型
| 解决方案               | 一致性 | 复杂度 | 性能 | 适用场景                     |
|------------------------|--------|--------|------|------------------------------|
| 基础方案（先更后删）| 弱一致 | 极低   | 最高 | 测试/低并发小型业务          |
| 延迟双删+重试          | 最终一致 | 低     | 高   | 读多写少/中小型业务          |
| Canal + Binlog         | 最终一致 | 中     | 中高 | 分布式微服务/中大型业务      |
| Redisson分布式锁       | 强一致 | 中     | 中   | 金融/库存/订单等核心场景     |

**选型原则**：常规业务用延迟双删，分布式用Canal，核心数据用分布式锁

## 五、避坑核心技巧
1. 缓存必设**过期时间**（30±随机分钟），兜底脏数据+避免雪崩
2. 坚持**删缓存而非更缓存**，杜绝并发脏写
3. 布隆过滤器拦截无效key，解决缓存穿透
4. Redis操作失败做有限重试，关键失败触发告警
5. 分布式锁用Redisson，自动续期避免死锁

## 总结
1. 所有方案基于「先更MySQL，再删Redis」核心原则；
2. 最终一致性为主流，强一致性仅核心场景使用；
3. 任何方案都需给缓存设过期时间，作为最后兜底。