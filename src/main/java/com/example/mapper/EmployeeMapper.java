package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Blockbuster
 * @date 2022/4/13 16:43:28 ζζδΈ
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
