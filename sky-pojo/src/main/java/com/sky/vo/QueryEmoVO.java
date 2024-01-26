package com.sky.vo;

import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryEmoVO implements Serializable {
    private Integer total;
    private List<Employee> records;

}
