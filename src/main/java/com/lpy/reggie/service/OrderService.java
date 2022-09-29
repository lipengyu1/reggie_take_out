package com.lpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lpy.reggie.common.R;
import com.lpy.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
