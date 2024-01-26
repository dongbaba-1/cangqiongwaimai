package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from sky_take_out.employee where username = #{username}")
    Employee getByUsername(String username);


    @Insert("insert into sky_take_out.employee values (null,#{username},#{name},#{password}," +
            "#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime}," +
            "#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT) //切入点在接口中描述
    void insertEmp(Employee employee);

    //Page<Employee> queryBypage(String name);
    Page<Employee> queryBypage(EmployeePageQueryDTO employeePageQueryDTO);


    void statusConvert(Integer status, Long id);

    @Select("select * from sky_take_out.employee where id = #{id};")
    Employee queryById(Long id);

    @AutoFill(value = OperationType.UPDATE)//切入点在接口中描述
    void updateEmp(EmployeeDTO employeeDTO, Long userId);
}
