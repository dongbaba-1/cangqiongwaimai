package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select * from sky_take_out.setmeal_dish where dish_id = #{id}")
    List<SetmealDish> queryByDishId(Long id);

    void insertSetmealDish(SetmealDish setmealDish);

    List<SetmealDish> queryBySetmealId(Long id);

    void deleteSetmealDish(Long id);

    List<Integer> getSetmealDishStatus(Long id);
}
