package com.lpy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lpy.reggie.entity.OrderDetail;
import com.lpy.reggie.mapper.OrderDetailMapper;
import com.lpy.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public List<OrderDetail> getByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetailList = this.list(orderDetailLambdaQueryWrapper);
        return orderDetailList;
    }
}