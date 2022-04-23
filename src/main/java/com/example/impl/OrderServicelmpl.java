package com.example.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.BaseContext;
import com.example.common.CustomException;
import com.example.entity.*;
import com.example.mapper.OrderMapper;
import com.example.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Blockbuster
 * @date 2022/4/22 14:30:57 星期五
 */

@Service
public class OrderServicelmpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private UserService userService;

    @Resource
    private OrderDetailService orderDetailService;

    /**
     * 支付功能
     * @param orders
     */
    @Override
    @Transactional
    public void orderSubmit(Orders orders) {


        // 获取当前登录用户id
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ShoppingCart::getUserId, userId);

        // 获取购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，无法下单");
        }

        // 得到当前登录用户信息
        User user = userService.getById(userId);

        // 获得填写的送餐地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        if (addressBook == null) {
            throw new CustomException("找不到地址");
        }

        // 订单总金额
        AtomicInteger amount = new AtomicInteger(0);

        // 订单号 随意获取
        long orderId = IdWorker.getId();

        // 新建 OrderDetail 集合，给里面属性一个一个赋值
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setImage(item.getImage());
            // 单个菜品的价格
            orderDetail.setAmount(item.getAmount());
            // 每个菜品价格乘以数量，再累加获得总金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 给 Order 的每个属性赋值
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(IdWorker.getId()));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                           + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                           + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                           + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 保存
        this.save(orders);

        // 保存
        orderDetailService.saveBatch(orderDetailList);
        // 删除购物车
        shoppingCartService.removeBatchByIds(shoppingCartList);
    }

}
