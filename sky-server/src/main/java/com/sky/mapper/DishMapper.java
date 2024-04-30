package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {


    @AutoFill(value = OperationType.INSERT)
    void insertDish(Dish dish);

    //void insertDishFlavor(DishFlavor dishFlavor);

    Page<DishVO> queryDishByPage(DishPageQueryDTO dishPageQueryDTO);

    void deleteDish(List<Long> ids);

    List<Integer> queryDishStatue(List<Long> ids);

    List<Dish> querySetmealDish(List<Long> ids);

    DishVO queryDishById(Long id);

    List<DishFlavor> queryFlavorsByDishId(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    void convertStatus(Integer status, Long id);

    @Select("select * from sky_take_out.dish where category_id = #{categoryId}")
    List<Dish> queryDishByCategoryId(Long categoryId);

    List<DishVO> userQueryByCategoriId(Long categoryId);

    List<Dish> list(Dish dish);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
