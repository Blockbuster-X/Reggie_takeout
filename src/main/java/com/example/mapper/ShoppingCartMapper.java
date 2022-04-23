package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Blockbuster
 * @date 2022/4/21 21:45:24 星期四
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
