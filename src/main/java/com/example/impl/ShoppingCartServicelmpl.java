package com.example.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.ShoppingCart;
import com.example.mapper.ShoppingCartMapper;
import com.example.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/21 21:46:32 星期四
 */

@Service
public class ShoppingCartServicelmpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
