package com.lpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lpy.reggie.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    public List<OrderDetail> getByOrderId(Long orderId);
}
