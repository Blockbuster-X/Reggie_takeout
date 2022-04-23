package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Blockbuster
 * @date 2022/4/21 11:36:46 星期四
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
