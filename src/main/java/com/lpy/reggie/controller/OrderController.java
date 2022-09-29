package com.lpy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lpy.reggie.common.BaseContext;
import com.lpy.reggie.common.R;
import com.lpy.reggie.dto.OrdersDto;
import com.lpy.reggie.entity.OrderDetail;
import com.lpy.reggie.entity.Orders;
import com.lpy.reggie.entity.ShoppingCart;
import com.lpy.reggie.service.OrderDetailService;
import com.lpy.reggie.service.OrderService;
import com.lpy.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody  Orders orders){
        log.info("订单数据{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime,String endTime){
        Page<Orders> pageInfo = new Page(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (number != null){
            ordersLambdaQueryWrapper.eq(Orders::getNumber,number);
        }
        if (beginTime != null&& endTime != null){
            //String->LocalDateTime
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime begin = LocalDateTime.parse(beginTime, fmt);
            LocalDateTime end = LocalDateTime.parse(endTime, fmt);

            ordersLambdaQueryWrapper.between(Orders::getOrderTime,begin,end);
        }
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,ordersLambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list = records.stream().map((item) ->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            String userName = item.getConsignee();
            ordersDto.setUserName(userName);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);

        return R.success(ordersDtoPage);
    }

    /**
     * 修改订单状态
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("修改订单信息：{}",orders);
        orderService.updateById(orders);
        return R.success("订单状态修改成功");
    }

    /**
     * 前台分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){

        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list = records.stream().map((item) ->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);

            Long id = item.getId();
            List<OrderDetail> orderDetail = orderDetailService.getByOrderId(id);
            if(orderDetail != null){
                ordersDto.setOrderDetails(orderDetail);
            }
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);
    }

    /**
     * 再来一单功能
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        //清空该用户当前购物车
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        //根据该订单号获取该订单细节
        Long ordersId = orders.getId();
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,ordersId);
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
        //更具订单细节中信息重新添加购物车
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item)-> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(item,shoppingCart);
            shoppingCart.setUserId(currentId);
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            String dishFlavor = item.getDishFlavor();
            if(dishId != null){
                shoppingCart.setDishId(dishId);
                shoppingCart.setDishFlavor(dishFlavor);
            }else {
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success("成功");
    }
}
