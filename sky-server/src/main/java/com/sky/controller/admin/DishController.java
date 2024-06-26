package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    public Result insertDish(@RequestBody DishDTO dishDTO){
        //先更新数据库
        dishService.insertDish(dishDTO);
        //再删除对应redis缓存
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public  Result<PageResult> queryDishByPage(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.queryDishByPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteDish(@RequestParam("ids")List<Long> ids){
        //先更新数据库
        dishService.deleteDish(ids);
        //再清除缓存
        for (Long id : ids) {
            Integer categoryId = dishService.getCategoryIdByDishId(id);
            String deleteKey = "dish_" + categoryId.toString();
            redisTemplate.delete(deleteKey);
        }
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> queryDishById(@PathVariable Long id){

        DishVO dishVO = dishService.queryDishById(id);
        return Result.success(dishVO);
    }


    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        //先更新数据库
        dishService.updateDish(dishDTO);
        //清除对应缓存
        String deleteKey = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(deleteKey);

        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品启售、停售")
    public Result convertStatus(@PathVariable Integer status,
                                @RequestParam("id") Long id){
        //先更新数据库
        dishService.convertStatus(status,id);

        //清除缓存
        Integer categgoryId = dishService.getCategoryIdByDishId(id);
        String deleteKey = "dish_" + categgoryId.toString();
        redisTemplate.delete(deleteKey);

        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> setmealAddDish(@RequestParam("categoryId") Long categoryId){
        List<Dish> dishList = dishService.queryByCategoryId(categoryId);
        return Result.success(dishList);
    }

}
