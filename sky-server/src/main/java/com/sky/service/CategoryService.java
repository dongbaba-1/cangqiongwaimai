package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void insertCategory(CategoryDTO categoryDTO);

    PageResult queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void statusConvert(Integer status, Long id);

    void updateCategory(CategoryDTO categoryDTO);

    void delCategory(Long id);

    List<Category> queryByType(Integer type);
}
