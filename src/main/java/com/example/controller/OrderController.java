package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.BaseContext;
import com.example.common.R;
import com.example.dto.OrdersDto;
import com.example.entity.OrderDetail;
import com.example.entity.Orders;
import com.example.entity.ShoppingCart;
import com.example.entity.User;
import com.example.service.OrderDetailService;
import com.example.service.OrderService;
import com.example.service.ShoppingCartService;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Blockbuster
 * @date 2022/4/22 14:32:44 星期五
 */

@RestController
@Slf4j
@RequestMapping("/order")

public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private OrderDetailService orderDetailService;

    @Resource
    private UserService userService;

    @Resource
    private ShoppingCartService shoppingCartService;


    /**
     * 支付功能
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.orderSubmit(orders);

        return R.success("支付成功");
    }

    /**
     * 展示订单列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    // 参考 Dish 的列表展示
    public R<Page> list(@RequestParam int page, int pageSize){
            Page<Orders> ordersPage = new Page<>(page, pageSize);
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
            wrapper.orderByDesc(Orders::getOrderTime);
            orderService.page(ordersPage, wrapper);
            Page<OrdersDto> ordersDtoPage = new Page<>();

            BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

            List<Orders> ordersPageRecords = ordersPage.getRecords();


            List<OrdersDto> ordersDtoList = ordersPageRecords.stream().map((item) -> {
                OrdersDto ordersDto = new OrdersDto();
                BeanUtils.copyProperties(item, ordersDto);
                LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
                orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
                List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
                ordersDto.setOrderDetails(list);
                return ordersDto;
            }).collect(Collectors.toList());

            ordersDtoPage.setRecords(ordersDtoList);

            return R.success(ordersDtoPage);

    }

    /**
     * 后台的订单明细展示，及订单查询功能
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageR(@RequestParam int page, int pageSize, Long number, Long beginTime, Long endTime){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.between((beginTime != null && endTime != null), Orders::getOrderTime, beginTime, endTime);
        ordersLambdaQueryWrapper.eq(number != null, Orders::getNumber, number);
        orderService.page(ordersPage, ordersLambdaQueryWrapper);
        // 提取出 Records (每个Orders) 把 username 属性加上去
        ordersPage.setRecords(ordersPage.getRecords().stream().map((item) -> {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, item.getUserId());
            // 通过 User 表找到当前登录用户名字
            item.setUserName(userService.getOne(userLambdaQueryWrapper).getName());
            return item;
        }).collect(Collectors.toList()));
        return R.success(ordersPage);
    }

    /**
     * 订单派送
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> change(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(orders != null, Orders::getId, orders.getId()).set(Orders::getStatus, orders.getStatus());
        orderService.update(wrapper);
        return R.success("派送成功");
    }

    /**
     * 再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    // 根据订单id找到详细订单表，把详细订单表赋值给购物车，再自己设定一些没有的值
    public R<String> again(@RequestBody Map map){
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!map.isEmpty(), OrderDetail::getOrderId, map.get("id"));
        shoppingCartService.saveBatch(orderDetailService.list(wrapper).stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(item, shoppingCart, "id");
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setId(IdWorker.getId());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            return shoppingCart;
        }).collect(Collectors.toList()));
        return R.success("再来一单成功");
    }
}