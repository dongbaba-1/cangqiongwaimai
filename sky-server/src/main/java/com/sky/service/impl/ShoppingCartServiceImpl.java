package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;
    @Override
    public List<ShoppingCart> list() {
        //根据当前用户userID查询
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.list(userId);
    }

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //存进表中的还是ShoppingCart类型
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //通过ThreadLocal获取并设置UserId
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //先通过查询判断购物车是否已存在该菜品.这里就算有查出来也只有一条，用List接收是为了方便后面的操作
        List<ShoppingCart> list= shoppingCartMapper.queryIfExit(shoppingCart);
        //存在则更新number+1
        if(list != null && list.size() > 0){
            Integer num = list.get(0).getNumber();
            int current_amount = list.get(0).getAmount().intValue() + list.get(0).getAmount().intValue()
                    / list.get(0).getNumber();
            BigDecimal current_amount_bd = new BigDecimal(current_amount);
            shoppingCartMapper.plusNumber(num + 1,list.get(0).getId());
        }
        else {//若不存在，再插入
            //插入的是单个菜，就去查单个菜的表。是套餐就去查套餐的表获取image name等字段信息
            if(shoppingCartDTO.getDishId() !=  null){//加入的菜品
                DishVO dishVO = dishMapper.queryDishById(shoppingCart.getDishId());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());
                BigDecimal price = dishVO.getPrice();
                shoppingCart.setAmount(price);
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());

            }
            else{//加入的是套餐
                Setmeal setmeal = setmealMapper.getSetmealById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.add(shoppingCart);
        }
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //判断要删的是菜品还是套餐
        if (shoppingCartDTO.getDishId() != null){//删除菜品
            //如果数目为1，则直接删除，否则数量减一
            ShoppingCart shoppingCart = shoppingCartMapper.getByDishId(shoppingCartDTO.getDishId());
            if(shoppingCart.getNumber() == 1){
                shoppingCartMapper.deleteDishByDishID(shoppingCart.getDishId());
            }
            else{

//                int current_amount = shoppingCart.getAmount().intValue() - shoppingCart.getAmount().intValue()
//                        / shoppingCart.getNumber();
//                BigDecimal current_amount_bd = new BigDecimal(current_amount);
                Integer current_number = shoppingCart.getNumber() - 1;
                shoppingCart.setNumber(current_number);
                //shoppingCart.setAmount(current_amount_bd);
                shoppingCartMapper.sub(shoppingCart);
            }

        }
        else {//删除套餐
            //如果数目为1，则直接删除，否则数量减一
            ShoppingCart shoppingCart = shoppingCartMapper.getBySetmealId(shoppingCartDTO.getSetmealId());
            if(shoppingCart.getNumber() == 1){
                shoppingCartMapper.deleteBySealmealID(shoppingCart.getSetmealId());
            }
            else{

//                int current_amount = shoppingCart.getAmount().intValue() - shoppingCart.getAmount().intValue()
//                        / shoppingCart.getNumber();
//                BigDecimal current_amount_bd = new BigDecimal(current_amount);
                Integer current_number = shoppingCart.getNumber() - 1;
                shoppingCart.setNumber(current_number);
                //shoppingCart.setAmount(current_amount_bd);
                shoppingCartMapper.sub(shoppingCart);
            }
        }
    }

    @Override
    public void clean() {
        //把该用户对应的东西直接全部删了
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }
}
