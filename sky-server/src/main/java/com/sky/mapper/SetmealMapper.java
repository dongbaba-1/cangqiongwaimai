package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {


    @Update("update sky_take_out.setmeal set status = 0 where id = #{SetmealId}")
    void convertSetmealStatus(Long setmealId);

    @AutoFill(OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    Page<SetmealVO> querysetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmeal(List<Long> ids);


    Setmeal getSetmealById(Long id);

    @AutoFill(OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    void convertStatus(Integer status, Long id);


    /**
     * 根据套餐id查询菜品选项
     * @param id
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from sky_take_out.setmeal_dish sd left join sky_take_out.dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long id);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    Integer countByMap(Map map);
}
