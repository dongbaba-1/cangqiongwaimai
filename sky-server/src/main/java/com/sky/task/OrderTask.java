package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void processTimeoutOrder(){
        log.info("定时查看是否有超时订单：{}", LocalDateTime.now());
        //查询订单状态为待支付的订单，查询下单时间离现在是否超过了15分钟

        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);

        //遍历修改订单状态
        for (Orders order : list) {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);

        }

    }

    /**
     * 每日一点把处于派送中状态的订单全部设置为已完成
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processFinishedOrder(){
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);//触发LocalDateTime.now()为1点，因此真正处理时间为12点
        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,time);
        for (Orders orders : list) {
            orders.setStatus(Orders.COMPLETED);


        }
    }
}
