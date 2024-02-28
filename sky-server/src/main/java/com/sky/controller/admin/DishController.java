package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result insertDish(@RequestBody DishDTO dishDTO){
        dishService.insertDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result queryDishByPage(@ModelAttribute DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.queryDishBypage(dishPageQueryDTO);
        return Result.success(pageResult);

    }

}
