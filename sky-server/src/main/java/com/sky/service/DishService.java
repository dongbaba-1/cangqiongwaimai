package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void insertDish(DishDTO dishDTO);

    PageResult queryDishByPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);

    DishVO queryDishById(Long id);

    void updateDish(DishDTO dishDTO);

    void convertStatus(Integer status, Long id);

    List<Dish> queryByCategoryId(Long categoryId);


    List<DishVO> listWithFlavor(Dish dish);

    Integer getCategoryIdByDishId(Long id);
}
