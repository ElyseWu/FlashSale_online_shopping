package com.jiuzhang.seckill.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {

    @Resource
    private RedisService service;

    @Test
    void setValue() {
        String value = service.setValue("test:1", 100L).getValue("test:1");
        assertEquals(Long.valueOf(value), 100L);
    }

    @Test
    void getValue() {
        String value = service.getValue("test:1");
        assertEquals(Long.valueOf(value), 100L);
    }

    @Test
    void stockDeductValidator() {
        boolean result = service.stockDeductValidator("test:1");
        assertTrue(result);
        String value = service.getValue("test:1");
        assertEquals(Long.valueOf(value), 99L);
    }
}