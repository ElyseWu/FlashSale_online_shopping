package com.jiuzhang.seckill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Slf4j
@Service
public class RedisService {
    @Resource
    private JedisPool jedisPool;
    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public RedisService setValue(String key, Long value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value.toString());
        jedisClient.close();
        return this;
    }

    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }
    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        Jedis jedisClient = jedisPool.getResource();
        String value = jedisClient.get(key);
        jedisClient.close();
        return value;
    }


    /**
     * 缓存中库存判断和扣减
     * @param key
     * @return
     * @throws Exception
     */
    public boolean stockDeductValidator(String key) {
        try(Jedis jedisClient = jedisPool.getResource()) {
            String script = "if redis.call('exists',KEYS[1]) == 1 then\n" +
                                    " local stock = tonumber(redis.call('get',KEYS[1]))\n" +
                                    " if( stock <=0 ) then\n" +
                                        " return -1\n" +
                                    " end;\n" +
                                    " redis.call('decr',KEYS[1]);\n" +
                                    " return stock - 1;\n" +
                            " end;\n" +
                            " return -1;";
            Long stock = (Long) jedisClient.eval(script,
                    Collections.singletonList(key), Collections.emptyList());
            if (stock < 0) {
                System.out.println("库存不足");
                return false;
            } else {
                System.out.println("恭喜，抢购成功");
            }
            return true;
        } catch (Throwable throwable) {
            System.out.println("库存扣减失败：" + throwable.toString());
            return false;
        }
    }

    public void revertStock(String key) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(key);
        jedisClient.close();
    }

    public boolean isInLimitMember(long activityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        boolean sismember = jedisClient.sismember("seckillActivity_users:" +
                activityId, String.valueOf(userId));
        jedisClient.close();
        log.info("userId:{} activityId:{} 在已购名单中:{}", userId, activityId,
                sismember);
        return sismember;
    }

    public void addLimitMember(long seckillActivityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("seckillActivity_users:" + seckillActivityId,  String.valueOf(userId));
        jedisClient.close();
    }

    public void removeLimitMember(Long seckillActivityId, Long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        jedisClient.close();
    }
}
