package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public DishVO queryDishById(Long id) {

        //查询除falvors以外的信息
        DishVO dishVO = dishMapper.queryDishById(id);
        //查询flavors by id
        dishVO.setFlavors(dishMapper.queryFlavorsByDishId(id));
        return dishVO;
    }

    @Override
    public void deleteDish(List<Long> ids) {

        //启售菜品状态的菜品不能删除
        List<Integer> statueList = dishMapper.queryDishStatue(ids);
        List<Dish> setmealDishList = dishMapper.querySetmealDish(ids);
        if(statueList.contains(1)){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //被套餐关联的菜品不能删除
        else if(!setmealDishList.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        else{
            //删除菜品时关联的口味也要删除
            dishMapper.deleteDish(ids);
        }


    }

    @Override
    public PageResult queryDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.queryDishByPage(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    @Override
    public void insertDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insertDish(dish);
        //还要获取当前添加菜的主键值id
        Long id = dish.getId();
        if(dishFlavorList != null && !dishFlavorList.isEmpty()){
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            //批量插入flavor数据，在sql层完成遍历
            dishFlavorMapper.insertBatch(dishFlavorList);
        }
    }

    @Override
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        Long id = dish.getId();
        dishMapper.updateDish(dish);

        //删除原有口味，
        dishFlavorMapper.delOriginFlavor(id);

        //再重新插入
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if(dishFlavorList != null && !dishFlavorList.isEmpty()){
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            //批量更改flavor数据，在sql层完成遍历
            dishFlavorMapper.insertBatch(dishFlavorList);
        }
    }

    @Override
    public void convertStatus(Integer status, Long id) {
        dishMapper.convertStatus(status,id);
        //包含菜品的套餐同时停售
        List<SetmealDish> setmealDishList = setmealDishMapper.queryByDishId(id);
        if(!setmealDishList.isEmpty()){
            for (SetmealDish setmealDish : setmealDishList) {
                setmealMapper.convertSetmealStatus(setmealDish.getSetmealId());
            }
        }
    }

    @Override
    public List<Dish> queryByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.queryDishByCategoryId(categoryId);
        return dishList;
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    @Override
    public Integer getCategoryIdByDishId(Long id) {
        return dishMapper.getCategoryIdByDishId(id);
    }
}
