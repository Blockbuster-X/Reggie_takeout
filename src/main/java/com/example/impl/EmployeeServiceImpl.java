package com.example.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Employee;
import com.example.mapper.EmployeeMapper;
import com.example.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/13 16:46:07 星期三
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
