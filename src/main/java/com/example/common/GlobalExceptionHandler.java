package com.example.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Blockbuster
 * @date 2022/4/17 16:09:36 星期日
 * 全局异常处理
 */

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 添加用户名重复异常处理
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException sq){
        if (sq.getMessage().contains("Duplicate entry")){
            String[] split = sq.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }


    /**
     * 添加用户名重复异常处理
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException sq){

            return R.error(sq.getMessage());
    }

}
