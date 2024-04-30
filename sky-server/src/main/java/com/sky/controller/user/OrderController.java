package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
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
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;

@RestController("userOrderController")
@Slf4j
@Api(tags = "用户端订单相关接口")
@RequestMapping("/user/order")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单接口")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单，参数：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

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
        String arriveTime = orderService.payOrder(ordersPaymentDTO);

        return Result.success(arriveTime);

    }

}
