package com.example.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Blockbuster
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    // 注解 @TableField 自动填充 插入时填充 MybatisPlus
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 插入和更新
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 更新时填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    // 插入和更新
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
