package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Blockbuster
 * @date 2022/4/20 21:46:38 星期三
 */

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
