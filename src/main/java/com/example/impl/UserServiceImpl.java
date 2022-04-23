package com.example.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/21 11:38:34 星期四
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
