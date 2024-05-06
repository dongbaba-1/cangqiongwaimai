package com.sky.controller.user;

import com.sky.constant.MessageConstant;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.exception.OrderBusinessException;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.time.Duration;
import java.util.UUID;

@RestController("userOrderController")
@Slf4j
@Api(tags = "用户端订单相关接口")
@RequestMapping("/user/order")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/submit")
    @ApiOperation("用户下单接口")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单，参数：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        //生成UUID，存放进redis
        String token = UUID.randomUUID().toString();
        //15分钟不付款自动取消订单，同时删除redis中的该key
        redisTemplate.opsForValue().setIfAbsent(token,"true", Duration.ofMinutes(15));

        orderSubmitVO.setUuid(token);

        return Result.success(orderSubmitVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult> historyOrders(int page, int pageSize, Integer status){
        PageResult pageResult = orderService.historyOrders(page,pageSize,status);

        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetailQuery(@PathVariable("id") Long id){
        log.info("查询订单详情，id:{}",id);

        OrderVO orderVO = orderService.orderDetailQuery(id);

        return Result.success(orderVO);

    }

    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id)throws Exception{
        //取消订单也应该要删除redis里的UUID
        log.info("取消订单：{}",id);

        orderService.cancleOrder(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    @PutMapping("/payment")
    @ApiOperation("支付订单")
    public Result<String> payOrder(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        String arriveTime;
        //判断UUID是否存在，存在，删除该key，执行付款流程；不存在，则是重复付款
        if(redisTemplate.opsForValue().get(ordersPaymentDTO.getUuid()) == null) {
            //不存在,则是重复付款
            throw new OrderBusinessException(MessageConstant.ORDER_ALREADY_PAIED);
        }
        else{
            //存在，执行正常付款流程并删除该key
            arriveTime = orderService.payOrder(ordersPaymentDTO);
            Boolean isSuccess = redisTemplate.delete(ordersPaymentDTO.getUuid());
            if(Boolean.TRUE.equals(isSuccess)){
                log.info("成功删除redis中的key");
            }

        }
        return Result.success(arriveTime);

    }

}
