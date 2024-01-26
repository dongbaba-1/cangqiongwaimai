package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    CategoryService categoryService;

    @PostMapping
    @ApiOperation("新增分类")
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO,@RequestHeader("token") String jwt){
        log.info("执行新增分类");

        String secretKey = jwtProperties.getAdminSecretKey();
        log.info("secretKey :{}",secretKey);
        //Long userId = Long.valueOf(JwtUtil.parseJWT(secretKey,jwt).get(JwtClaimsConstant.EMP_ID).toString());

        categoryService.insertCategory(categoryDTO);

        return Result.success();

    }

    /**
     * 分页查询分类
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result<PageResult> queryCategoryByPage(@ModelAttribute CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult page = categoryService.queryCategoryByPage(categoryPageQueryDTO);
        return Result.success(page);
    }

    /**
     * 启动、禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result categoryStatusConvert(@PathVariable Integer status,
                                        @RequestParam Long id){
        categoryService.statusConvert(status,id);

        return Result.success();

    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("执行分类修改");
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result delCategory(@RequestParam("id") Long id){
        log.info("执行删除分类");
        categoryService.delCategory(id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("按类型查询分类")
    public Result queryByType(@RequestParam("type") Integer type){
        List<Category> categoryList= categoryService.queryByType(type);
        return Result.success(categoryList);
    }

}
