package com.jiuzhang.seckill;

import com.jiuzhang.seckill.service.RedisService;
import com.jiuzhang.seckill.service.SeckillActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {
    @Resource
    private RedisService redisService;
    @Resource
    private SeckillActivityService seckillActivityService;

    @Test
    public void stockTest(){
        String value = redisService.setValue("stock:19",10L).getValue("stock:19");
        System.out.println(value);
    }

    @Test
    public void getStockTest() {
        String stock = redisService.getValue("stock:19");
        System.out.println(stock);
    }

    @Test
    public void pushSeckillInfoToRedisTest() {
        seckillActivityService.pushSeckillInfoToRedis(19);
    }
}
