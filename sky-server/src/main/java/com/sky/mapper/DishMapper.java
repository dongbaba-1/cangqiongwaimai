package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {


    @AutoFill(value = OperationType.INSERT)
    void insertDish(Dish dish);

    void insertDishFlavor(DishFlavor dishFlavor);

    Page<DishVO> queryDishByPage(DishPageQueryDTO dishPageQueryDTO);
}
