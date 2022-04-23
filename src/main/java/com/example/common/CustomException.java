package com.example.common;

/**
 * 自定义业务异常
 * @author Blockbuster
 * @date 2022/4/18 23:00:36 星期一
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }

}
