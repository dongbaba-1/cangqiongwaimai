package com.sky.controller.user;


import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;



    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> setmealDish(Long categoryId) {
        log.info("根据分类ID查询菜品，包括菜品口味");

        //放进redis时是什么类型，查出来就用什么类型接收，最后强转一下
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get("dish_" + categoryId);
        //使用RedisTemplate操作
        //首先判断缓存中是否存在该分类菜品，key统一命名为dish_categoryId
        if(list == null){//没查到，说明没在redis缓存中
            //去数据库查并保存在redis中
            Dish dish = new Dish();
            dish.setCategoryId(categoryId);
            dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
            list = dishService.listWithFlavor(dish);
            //放的是List类型，存放时通过序列化转换为redis string
            redisTemplate.opsForValue().set("dish_" + categoryId,list);
        }
        //查到了，就直接返回
        return Result.success(list);
    }

}
