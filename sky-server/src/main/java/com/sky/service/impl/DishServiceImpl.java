package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Override
    public PageResult queryDishBypage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.queryDishByPage(dishPageQueryDTO);
        Long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total,records);
    }

    @Override
    public void insertDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insertDish(dish);
        //还要获取当前添加菜的id
        Long id = dish.getId();
        if(dishFlavorList != null){
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
                dishMapper.insertDishFlavor(dishFlavor);
            });
        }

    }
}
