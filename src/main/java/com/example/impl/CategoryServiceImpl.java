package com.example.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.Setmeal;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.example.service.DishService;
import com.example.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Blockbuster
 * @date 2022/4/18 20:48:59 星期一
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);

        long count = dishService.count(dishLambdaQueryWrapper);

        // 查询当前分类是否关联菜品，如果关联，抛出一个业务异常
        if (count > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 根据 id 进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        long count1 = setmealService.count(setmealLambdaQueryWrapper);

        // 查询当前分类是否关联套餐，如果关联了，抛出一个业务异常
        if (count1 > 0) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 正常删除分类
        super.removeById(ids);

    }
}
