package com.demo.redis.started.lock;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;


/**
 * RedisLockRegistry test
 *
 * @author Dean
 * @date 2021-06-24
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLockRegistryTests {

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Test
    public void testLock() {
        // 获取锁
        Lock lock = redisLockRegistry.obtain("dean");
        Assert.assertNotNull(lock);
        // 加锁
        lock.lock();
    }
}
