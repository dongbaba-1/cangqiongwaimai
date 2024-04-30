package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void insertCategory(CategoryDTO categoryDTO) {
        //接下来调用持久层mapper操作数据库
        //传给持久层的数据还是要使用实体类，因此进行一个对象转换
        Category category = new Category();
        //直接使用属性拷贝避免重复操作
        BeanUtils.copyProperties(categoryDTO,category);
        //添加DTO对象中缺少的对象
        category.setStatus(StatusConstant.DISABLE);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        //使用ThreadLocal获取保存当前用户id
//        category.setUpdateUser(BaseContext.getCurrentId());
//        category.setCreateUser(BaseContext.getCurrentId());
        categoryMapper.insertCategory(category);
    }

    @Override
    public PageResult queryCategoryByPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.queryCategoryByPage(categoryPageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    public void statusConvert(Integer status, Long id) {
        categoryMapper.statusConvert(status,id);
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.updateCategory(category);
    }

    @Override
    public void delCategory(Long id) {
        //查找dish表中是否有category_id为待删除分类的id的dish

        List<Dish> dishList = categoryMapper.queryDishByCategoryId(id);
        List<Setmeal> setmealList = categoryMapper.querySetmealByCategoryId(id);
        if(!dishList.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        else if(setmealList.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        else{
            categoryMapper.delCategory(id);
        }

    }

    @Override
    public List<Category> queryByType(Integer type) {
        return categoryMapper.queryByType(type);
    }
}
