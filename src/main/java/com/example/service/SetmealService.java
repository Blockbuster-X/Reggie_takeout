package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.SetmealDto;
import com.example.entity.Setmeal;

import java.util.List;

/**
 * @author Blockbuster
 * @date 2022/4/18 20:48:20 星期一
 */


public interface SetmealService extends IService<Setmeal> {
    // 添加套餐
    void saveWithDish(SetmealDto setmealDto);

    // 删除套餐
    void removeWithDish(List<Long> ids);

    // 停售套餐 起售套餐
    void statusChangeWithDish(List<Long> ids, int status);

    // 根据 id 查询套餐信息和对应的菜品信息，回显
    SetmealDto getByIdWithDish(Long id);

    // 点保存修改菜品
    void updateSetmealWithDish(SetmealDto setmealDto);


}
