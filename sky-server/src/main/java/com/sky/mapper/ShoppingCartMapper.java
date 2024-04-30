package com.sky.mapper;


import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(Long userId);

    List<ShoppingCart> queryIfExit(ShoppingCart shoppingCart);

    void plusNumber(Integer number,Long id);

    void plusNumberAndAmount(Integer number, BigDecimal currentAmountBd, Long id);

    void add(ShoppingCart shoppingCart);

    @Select("select * from sky_take_out.shopping_cart where dish_id = #{dishId}")
    ShoppingCart getByDishId(Long dishId);

    @Delete("delete from sky_take_out.shopping_cart where dish_id = #{dishId}")
    void deleteDishByDishID(Long dishId);


    void sub(ShoppingCart shoppingCart);

    @Select("select * from sky_take_out.shopping_cart where setmeal_id  = #{setmealId}")
    ShoppingCart getBySetmealId(Long setmealId);

    @Delete("delete from sky_take_out.shopping_cart where setmeal_id = #{setmealId}")
    void deleteBySealmealID(Long setmealId);


    @Delete("delete from sky_take_out.shopping_cart where user_id = #{currentId}")
    void clean(Long currentId);


    void insertBatch(List<ShoppingCart> shoppingCartList);
}
