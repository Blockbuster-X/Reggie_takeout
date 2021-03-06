package com.example.common;

/**
 * @author Blockbuster
 * @date 2022/4/18 16:32:44 ζζδΈ
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
