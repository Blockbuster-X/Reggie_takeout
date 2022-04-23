package com.example.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.dto.SetmealDto;
import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import com.example.mapper.SetmealMapper;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Blockbuster
 * @date 2022/4/18 22:36:46 星期一
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 添加套餐
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> list = setmealDto.getSetmealDishes();

        // 遍历列表 为每一个 SetmealDish 添加 SetmealId
        list = list.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);

    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

        // 查询套餐状态
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        long count = this.count(wrapper);

        // 有套餐起售
        if (count > 0) {
            throw new CustomException("部分套餐售卖中，不能删除");
        }

        // 删除 Setmeal 表数据
        this.removeByIds(ids);

        // 删除 SetmealDish 表数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);


    }

    /**
     * 批量停售 起售
     * @param ids
     */
    @Override
    public void statusChangeWithDish(List<Long> ids, int status) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

        wrapper.in(!ids.isEmpty(), Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, status);

        // 判断与网页命令传来的 status 不同的 Setmeal 有多少条
        long count = this.count(wrapper);

        // 说明全都不同 批量改
        if (count == 0){

            // update setmeal set `status` = 0 WHERE id IN (...)

            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Setmeal::getId, ids).set(Setmeal::getStatus, status);
            this.update(updateWrapper);

        }else throw new CustomException("部分套餐已起售（停售）");

    }

    /**
     * 根据 id 查询套餐信息和对应的菜品信息，回显
     * @param id
     * @return
     */
    @Override
    // 参考菜品的修改功能
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SetmealDish::getSetmealId, id);

        List<SetmealDish> setmealDishList = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    /**
     * 点保存修改菜品
     * @param setmealDto
     */
    @Override
    // 参考菜品的修改
    public void updateSetmealWithDish(SetmealDto setmealDto) {
        // update Setmeal set * = ? where id = ?
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());

        setmealDishService.remove(wrapper);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        setmealDishList = setmealDishList.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishList);

    }
}
