package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * @author Jonydom
 * @description TODO
 * @date 2024-04-19 17:11
 */
public interface OrdersService extends IService<Orders> {

    // 用户下单
    public void submit(Orders orders);
}
