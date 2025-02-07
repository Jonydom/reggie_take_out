package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * @author Jonydom
 * @description TODO
 * @date 2024-04-10 17:12
 */
public interface DishService extends IService<Dish> {

    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    // 修改菜品信息，同时修改口味信息
    public void updateWithFlavor(DishDto dishDto);

    // 根据ids批量删除对应的dis和dish_flavor
    public void deleteDishByIdsWithFlavor(Long[] ids);
}
