package com.jiuzhang.seckill.service;

import com.alibaba.fastjson.JSON;
import com.jiuzhang.seckill.db.dao.SeckillActivityDao;
import com.jiuzhang.seckill.db.po.Order;
import com.jiuzhang.seckill.db.po.SeckillActivity;
import com.jiuzhang.seckill.mq.RocketMQService;
import com.jiuzhang.seckill.util.SnowFlake;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillActivityService {

    @Resource
    private RedisService redisService;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RocketMQService rocketMQService;

    private SnowFlake snowFlake = new SnowFlake(1, 1);

    /**
     * 判断商品是否还有库存
     * @param activityId 商品ID
     * * @return
     * */

    public boolean seckillStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return redisService.stockDeductValidator(key);
    }

    public Order createOrder(long seckillActivityId, long userId) throws
            Exception {
        /*
         * 1.创建订单
         */
        SeckillActivity seckillActivity =
                seckillActivityDao.querySeckillActivityById(seckillActivityId);
        Order order = new Order();
//采用雪花算法生成订单ID
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setSeckillActivityId(seckillActivity.getId());
        order.setUserId(userId);
        order.setOrderAmount(seckillActivity.getSeckillPrice().longValue());
        /*
         *2.发送创建订单消息
         */
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));
        return order;
    }
}
