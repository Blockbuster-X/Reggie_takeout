package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.entity.Employee;
import com.example.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Blockbuster
 * @date 2022/4/13 16:50:04 星期三
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    // JSON 格式接受需要加 @RequestBody 注解
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        // 用 DigestUtils 工具类对密码 md5 加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 查数据库比对 username 是唯一约束
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null) {
            return R.error("登录失败");
        }

        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        if (emp.getStatus() == 0) {
            return R.error("账号被禁用");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        // 设置初始密码 需要 md5 加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设定创建时间， 更新时间（用了 mybatisPlus 自动填充 MyMetaObjectHandler 就不用自己设定）
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        // 获得当前登录用户id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        // 添加到数据库
        employeeService.save(employee);

        return R.success("添加用户成功");
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    // 前端需要 total records 参数，所以需要 page 泛型
    public R<Page> page(int page, int pageSize, String name){
        Page pageInfo = new Page(page, pageSize);
        // 构造条件过滤器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        wrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工状态，修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        //employee.setUpdateTime(LocalDateTime.now());
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("用户不存在");
    }


}
