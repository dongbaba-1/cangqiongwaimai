package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     */
    @Override
    public void insertEmp(EmployeeDTO employeeDTO, Long userId){
        //接下来调用持久层mapper操作数据库
        //传给持久层的数据还是要使用实体类，因此进行一个对象转换
        Employee employee = new Employee();

        /*employee.setId(employeeDTO.getId());
        employee.setName(employeeDTO.getName());
        .....*/
        //直接使用属性拷贝避免重复操作
        BeanUtils.copyProperties(employeeDTO,employee);
        //添加DTO对象中缺少的属性
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8)));
        employee.setStatus(StatusConstant.ENABLE);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //创建人id通过解析jwt实现
//        employee.setCreateUser(userId);
//        employee.setUpdateUser(userId);
        //使用ThreadLocal获取保存的比那变量
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insertEmp(employee);

    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult queryByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //Page<Employee> page = employeeService.queryByPage(employeePageQueryDTO);
        Page<Employee> page = employeeMapper.queryBypage(employeePageQueryDTO);
        Long total = page.getTotal();
        List<Employee> records = page.getResult();
        //return employeeMapper.queryBypage(employeePageQueryDTO.getName());
        return new PageResult(total,records);
    }

    @Override
    public void statusConvert(Integer status, Long id) {
        employeeMapper.statusConvert(status,id);
    }

    //保留一个原始实现方法
    @Override
    public void updateEmp(EmployeeDTO employeeDTO, Long userId) {
        employeeMapper.updateEmp(employeeDTO,userId);
    }

    @Override
    public Employee queryById(Long id) {
        return  employeeMapper.queryById(id);
    }


}
