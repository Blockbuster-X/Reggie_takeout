package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Category;

/**
 * @author Blockbuster
 * @date 2022/4/18 20:48:20 ζζδΈ
 */


public interface CategoryService extends IService<Category> {

    void remove(Long ids);
}
