package com.sky.controller.admin;


import com.github.pagehelper.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
public class SetmealController {


    @Autowired
    SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result insertSetmeal(@RequestBody SetmealDTO setmealDTO){

        setmealService.insertSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.querySetmealByPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//全部删除
    public Result deleteSetmeal(@RequestParam("ids") List<Long> ids){
        setmealService.deleteSetmeal(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.getSetmealById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("更新套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//全部删除
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐启售停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)//全部删除
    public Result convertStatus(@PathVariable Integer status,
                                @RequestParam("id") Long id){
        setmealService.convertSatus(status,id);
        return Result.success();
    }


}
