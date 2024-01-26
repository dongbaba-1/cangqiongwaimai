package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {


    @AutoFill(value = OperationType.INSERT)
    void insertDish(Dish dish);

    void insertDishFlavor(DishFlavor dishFlavor);
}
