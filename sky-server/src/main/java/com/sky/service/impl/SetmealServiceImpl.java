package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public void insertSetmeal(SetmealDTO setmealDTO) {
        //分别插入Setmeal和SetmealDish表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.insertSetmeal(setmeal);
        //需要主键回显
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealId);
            setmealDishMapper.insertSetmealDish(setmealDish);
        }
    }

    @Override
    public PageResult querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.querysetmealByPage(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteSetmeal(List<Long> ids) {
        setmealMapper.deleteSetmeal(ids);
    }

    @Override
    public SetmealVO getSetmealById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        Setmeal setmeal = new Setmeal();
        setmeal = setmealMapper.getSetmealById(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setCategoryName(categoryMapper.getCategoryNameById(setmeal.getCategoryId()));
        setmealVO.setSetmealDishes(setmealDishMapper.queryBySetmealId(id));
        return setmealVO;
    }

    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //setmealDTO中的数据分别插入两个表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateSetmeal(setmeal);
        //还是同样的思路：先删除所有的setmealDish
        setmealDishMapper.deleteSetmealDish(setmealDTO.getId());
        //，再插入
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDishMapper.insertSetmealDish(setmealDish);
        }
    }

    @Override
    public void convertSatus(Integer status, Long id) {
        //如果套餐内有停售菜品，则不能启售
        List<Integer> statusList = setmealDishMapper.getSetmealDishStatus(id);
        if(statusList.contains(StatusConstant.DISABLE)){
            throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }
        else {
            setmealMapper.convertStatus(status,id);
        }

    }


    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }
}
