package com.example.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.OrderDetail;
import com.example.mapper.OrderDetailMapper;
import com.example.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/22 14:31:48 星期五
 */

@Service
public class OrderDetailServicelmpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
