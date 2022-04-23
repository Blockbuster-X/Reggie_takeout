package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.entity.Category;
import com.example.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Blockbuster
 * @date 2022/4/18 20:50:45 星期一
 */

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){

        categoryService.save(category);

        return R.success("新增分类成功");
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, wrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        // categoryService.removeById(ids); 该方法是没有经过检查有没有关联

        // 封装了一个 remove 方法，逻辑定义在 CategoryServiceImpl
        categoryService.remove(ids);

        return R.success("删除成功");
    }

    /**
     * 修改功能
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类列表
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        // 条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 添加条件 对比type值
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());

        // 排序 根据sort升序后再根据更新时间倒序
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }

}
