package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.BaseContext;
import com.example.common.R;
import com.example.entity.ShoppingCart;
import com.example.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Blockbuster
 * @date 2022/4/21 21:47:33 星期四
 */

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        // 设置用户 Id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 查询当前菜品或套餐是否在购物车中
        if (shoppingCart.getDishId() != null){
            // 当前添加的是菜品
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 当前添加的是套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one != null) {
            // 如果已经存在，就在原来数量基础上加一
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        }else {
            // 如果不存在，则添加在购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 删除购物车菜品，套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(shoppingCart != null, ShoppingCart::getUserId, BaseContext.getCurrentId());

        if (shoppingCart.getDishId() == null){
            // 删除的是套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }else {
            // 删除的是菜品
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }

        shoppingCartService.remove(wrapper);
        return R.success("删除成功");

    }

    /**
     * 展示购物车列表
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> list(){
        // 根据用户id查找
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        wrapper.orderByAsc(ShoppingCart::getCreateTime);

        return R.success(shoppingCartService.list(wrapper));
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success("清空成功");
    }
}
