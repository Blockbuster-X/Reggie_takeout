package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Blockbuster
 * @date 2022/4/18 19:37:10 星期一
 */

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
