package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author Jonydom
 * @description TODO
 * @date 2024-04-10 14:57
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
