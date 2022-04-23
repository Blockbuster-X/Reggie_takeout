package com.example.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.DishFlavor;
import com.example.mapper.DishFlavorMapper;
import com.example.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/18 22:30:23 星期一
 */

@Service
public class DishFlavorServicelmpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
