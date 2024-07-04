package com.itheima.reggie;

import org.junit.jupiter.api.Test;

/**
 * @author Jonydom
 * @description 测试上传下载文件
 * @date 2024-04-11 14:36
 */
public class UploadFileTest {
    @Test
    public void test01() {
        String fileName = "test.jpg";
        String substring = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("substring = " + substring);
    }
}
