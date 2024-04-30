package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from sky_take_out.orders where id = #{id}")
    Orders getById(Long id);

    void update(Orders orders);

    /**
     * 根据状态统计订单数量
     */
    @Select("select count(id) from sky_take_out.orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from sky_take_out.orders where user_id = #{userId} and number = #{orderNumber}")
    Orders getByUserIdAndOrderNumber(Long userId, String orderNumber);


    void updateStatusById(Orders order);

    @Select("select  * from sky_take_out.orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);

    Integer countByMap(Map map);

    Double sumByMap(Map map);
}
