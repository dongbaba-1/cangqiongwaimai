package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.QueryEmoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();

        //把员工id加入jwt载荷中
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工登出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")

    public Result insertEmp(@RequestBody EmployeeDTO employeeDTO, @RequestHeader("token") String jwt){
        //前端传递的实际上是Json格式的数据
        //@RequestBody EmployeeDTO employeeDTO 将该数据封装为一个EmployeeDTO对象
        log.info("新增员工:{}",employeeDTO);
        log.info("jwt :{}",jwt);
        String secretKey = jwtProperties.getAdminSecretKey();
        log.info("secretKey :{}",secretKey);
        Long userId = Long.valueOf(JwtUtil.parseJWT(secretKey,jwt).get(JwtClaimsConstant.EMP_ID).toString());
        employeeService.insertEmp(employeeDTO,userId);
        return Result.success();
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询员工")
    public Result<PageResult> queryEmpByPage(@ModelAttribute EmployeePageQueryDTO employeePageQueryDTO){
       // List<Employee> records = employeeService.queryByPage(employeePageQueryDTO);

//        Integer total = records.size();
//        QueryEmoVO queryEmoVO = new QueryEmoVO(total, records);
//        return Result.success(queryEmoVO);
        PageResult pageResult = employeeService.queryByPage(employeePageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 启用\禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用\\禁用员工账号")
    public Result StatusConvert(@PathVariable Integer status,
                                @RequestParam Long id){
        employeeService.statusConvert(status,id);
        return Result.success();

    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result queryById(@PathVariable Long id){
        Employee employee = employeeService.queryById(id);
        return Result.success(employee);
    }


    /**
     * 编辑员工
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑员工")
    public Result updateEmp(@RequestBody EmployeeDTO employeeDTO,@RequestHeader("token") String jwt){
        String secretKey = jwtProperties.getAdminSecretKey();
        log.info("secretKey :{}",secretKey);
        Long userId = Long.valueOf(JwtUtil.parseJWT(secretKey,jwt).get(JwtClaimsConstant.EMP_ID).toString());
        employeeService.updateEmp(employeeDTO,userId);
        return Result.success();
    }
}
