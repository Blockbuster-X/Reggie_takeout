package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.DishDto;
import com.example.entity.Dish;

import java.util.List;

/**
 * @author Blockbuster
 * @date 2022/4/13 16:44:29 星期三
 */
public interface DishService extends IService<Dish>  {

    // 新增菜品，同时插入对应的口味数据，需操作两张表
    void saveWithFlavor(DishDto dishDto);

    // 批量删除菜品，同时删除口味
    void deleteWithFlavor(List<Long> ids);

    // 菜品停售 起售及批量
    void statusChangeWithDish(List<Long> ids, int status);

    // 根据 id 查询菜品信息和对应的口味信息，回显
    DishDto getByIdWithFlavor(Long id);

    // 点提交修改菜品
    void updateWithFlavor(DishDto dishDto);
}
