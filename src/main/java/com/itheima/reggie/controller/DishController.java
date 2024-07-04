package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jonydom
 * @description 菜品管理
 * @date 2024-04-11 15:47
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }


    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝：拷贝一些Page中的其他属性
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // 取出records属性的值
        List<Dish> records = pageInfo.getRecords();
        // 使用新对象来承接，并进行修改
        List<DishDto> list = records.stream().map((item) -> {
            // 创建一个待填数据的DishDto对象
            DishDto dishDto = new DishDto();
            // 将通用属性都拷贝过去
            BeanUtils.copyProperties(item, dishDto);
            // 获取categoryId
            Long categoryId = item.getCategoryId();
            // 调用categoryService对象，来根据categoryId查询数据
            Category category = categoryService.getById(categoryId);
            // 将category对象里面name复制给dishDto
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        // 复制值到要返回的对象中
        dishDtoPage.setRecords(list);


        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 修改菜品状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> statusChange(Long[] ids, @PathVariable Integer status) {
        for(Long id : ids) {
            // 根据id查询对应的dish记录
            Dish dish = dishService.getById(id);
            if (status == 0) {
                dish.setStatus(0);
            } else {
                dish.setStatus(1);
            }
            // 更新对应的dish记录
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }


    /**
     * 根据ids批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids) {
        dishService.deleteDishByIdsWithFlavor(ids);
        return R.success("删除成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish  使用dish对象来接收categoryId，相比只接收categoryId更加通用一点
     * @return
     */
    // @GetMapping("/list")
    // public R<List<Dish>> list(Dish dish) {
    //     // 构造查询条件
    //     LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //     queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
    //     // 添加条件，查询状态为1（起售）的菜品
    //     queryWrapper.eq(Dish::getStatus, 1);
    //     queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    //
    //     List<Dish> list = dishService.list(queryWrapper);
    //
    //     return R.success(list);
    // }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 添加条件，查询状态为1（起售）的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 根据category_id查找category_name
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 根据dish_id查找dish_flavor表
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
