package com.itheima.reggie.common;

/**
 * @author Jonydom
 * @description 自定义业务异常类
 * @date 2024-04-10 19:39
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
