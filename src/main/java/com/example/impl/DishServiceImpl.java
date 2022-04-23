package com.example.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.dto.DishDto;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.mapper.DishMapper;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Blockbuster
 * @date 2022/4/18 22:30:23 星期一
 */

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        // 保存菜品基本信息
        this.save(dishDto);

        // 获取菜品 id
        Long disId = dishDto.getId();

        // 取出 口味列表
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 遍历列表 为每一个 DishFlavor 添加菜品 id
        flavors = flavors.stream().map((item) -> {
            item.setDishId(disId);
            return item;
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量删除菜品，同时删除口味
     * @param ids
     */
    @Override
    public void deleteWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        // 查询套餐状态
        wrapper.in(Dish::getId, ids);
        wrapper.eq(Dish::getStatus, 1);

        long count = this.count(wrapper);

        if (count > 0){
            throw new CustomException("部分菜品售卖中，不能删除");
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(DishFlavor::getDishId, ids);

        dishFlavorService.remove(queryWrapper);

    }

    /**
     * 批量停售 起售
     * @param ids
     */
    @Override
    public void statusChangeWithDish(List<Long> ids, int status) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        wrapper.in(!ids.isEmpty(), Dish::getId, ids);
        wrapper.eq(Dish::getStatus, status);

        long count = this.count(wrapper);


        if (count == 0){

            // update Dish set `status` = 0 WHERE id IN (...)

            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Dish::getId, ids).set(Dish::getStatus, status);
            this.update(updateWrapper);

        }else throw new CustomException("部分菜品已起售（停售）");
    }

    /**
     * 根据 id 查询菜品信息和对应的口味信息，回显
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 根据Dish_id 查询 Dish（菜品）
        Dish dish = this.getById(id);

        // 因为 Dish 没有口味属性，所以要封装到 DishDto
        DishDto dishDto = new DishDto();

        // 复制属性
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(DishFlavor::getDishId, dish.getId());

        // 查询口味 DishFlavor 返回口味 list
        List<DishFlavor> list = dishFlavorService.list(wrapper);

        // 设定 dishDto 口味属性
        dishDto.setFlavors(list);

        return dishDto;
    }


    /**
     * 点提交修改菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新 Dish 表基本信息
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(DishFlavor::getDishId, dishDto.getId());

        // 先移除口味 在重新添加口味 方便做法
        dishFlavorService.remove(wrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        // 遍历列表 为每一个 DishFlavor 添加菜品 id
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
