package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.R;
import com.example.dto.DishDto;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.service.CategoryService;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Blockbuster
 * @date 2022/4/19 13:56:36 星期二
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);
        // 修改菜品清理对应分类缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("添加成功");
    }

    /**
     * 批量删除菜品，同时删除口味
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> dishdelete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);

        return R.success("删除成功");
    }

    /**
     * 停售起售及批量停售起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> change(@RequestParam List<Long> ids, @PathVariable int status){

        dishService.statusChangeWithDish(ids,status);

        // select * from dish where id in ()
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId, ids);
        List<Dish> list = dishService.list(wrapper);
        System.out.println("1111111111111111111111111111111111111111111111111"+list.toString());
        list.stream().map((item) -> {
            String key = "dish_" + item.getCategoryId() + "_1";
            redisTemplate.delete(key);
            return item;
        }).collect(Collectors.toList());


        return R.success("修改成功");
    }

    /**
     * 分页查询，根据id查询菜品
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @Transactional
    public R<Page> page(int page, int pageSize, String name){


        Page<Dish> dishPage = new Page<>(page, pageSize);

        // dish 没有属性 categoryName 无法给前端显示，所以需要新建一个 Page<DishDto>
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(name != null, Dish::getName, name);

        wrapper.orderByDesc(Dish::getUpdateTime);

        // 根据条件获取到分页显示在前端 但没有 categoryName 属性
        dishService.page(dishPage, wrapper);

        // 除了 records 属性，其他都复制，因为需要把 categoryName 放到 records 里
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        // 拿出 records (records 里存放的是多个 Dish 对象)
        List<Dish> records = dishPage.getRecords();

        // 把 dishPage 的 records 都加上 categoryName 属性 复制为 dishDtoList
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 把 records 里的 item（即每个 Dish 对象）复制给 dishDto
            BeanUtils.copyProperties(item, dishDto);
            // 得到每个 Dish 对象的 categoryId
            Long categoryId = item.getCategoryId();
            // 根据 categoryId 查处 Category 对象
            Category category = categoryService.getById(categoryId);
            // 获得 Category 对象名字，即菜品类型名字：川菜之类的
            String categoryName = category.getName();
            // 把 categoryName 设定给 dishDto
            dishDto.setCategoryName(categoryName);
            // 返回 dishDto
            return dishDto;
            // 重新变为 list
        }).collect(Collectors.toList());

        // 再把 dishDtoList（Records） 放回 dishDtoPage
        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);

    }

    /**
     * 根据Id查询菜品信息和口味信息，回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        // 封装业务逻辑到 DishService
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 点提交修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        // 修改菜品清理对应分类缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    /**
     * 单纯后台端
     * 根据分类id CategoryId 查询菜品
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> dishlist(Dish dish){

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        // 根据 分类id CategoryId 查询
        wrapper.eq(dish.getCategoryId() != 0, Dish::getCategoryId, dish.getCategoryId());

        // 根据 起售状态 Status 查询
        wrapper.eq(Dish::getStatus, 1);

        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(wrapper);

        return R.success(list);


    }*/

    /**
     * 客户端和后台端共用
     * 根据分类id CategoryId 查询菜品和口味
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> dishlist(Dish dish){

        List<DishDto> dishDtoList = null;

        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // 查询 redis 是否有该分类下菜品信息
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        // 根据 分类id CategoryId 查询
        wrapper.eq(dish.getCategoryId() != 0, Dish::getCategoryId, dish.getCategoryId());

        // 根据 起售状态 Status 查询
        wrapper.eq(Dish::getStatus, 1);

        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(wrapper);

        // ------ 上面的代码是后台端回显菜品的分类 ------ //
        // ------ 下面的代码是客户端回显菜品的口味 ------ //

        // 给 List<Dish> list 里的每个 Dish 加上口味信息
        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 把 list 里的 item（即每个 Dish 对象）复制给 dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();

            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            // 加入口味信息
            dishDto.setFlavors(dishFlavorList);

            // 返回 dishDto
            return dishDto;
            // 重新变为 list
        }).collect(Collectors.toList());

        // 加入缓存
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
