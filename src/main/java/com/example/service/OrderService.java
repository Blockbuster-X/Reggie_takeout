package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Orders;

/**
 * @author Blockbuster
 * @date 2022/4/22 14:30:09 星期五
 */
public interface OrderService extends IService<Orders> {

    // 支付功能
    void orderSubmit(Orders orders);

}
