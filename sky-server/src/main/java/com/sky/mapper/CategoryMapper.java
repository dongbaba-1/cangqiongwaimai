package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {


    Page<Category> queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void statusConvert(Integer status, Long id);

    @AutoFill(value = OperationType.INSERT)//切入点在接口中描述
    void insertCategory(Category category);

    @AutoFill(value = OperationType.UPDATE)//切入点在接口中描述
    void updateCategory(Category category);


    @Delete("delete from sky_take_out.category where id = #{id}")
    void delCategory(Long id);

    List<Category> queryByType(Integer type);

    List<Dish> queryDishByCategoryId(Long id);

    List<Setmeal> querySetmealByCategoryId(Long id);

    String getCategoryNameById(Long categoryId);
}
