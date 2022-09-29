package com.lpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lpy.reggie.dto.DishDto;
import com.lpy.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish>  {
    //新增菜品，同时插入口味数据,操作两张表，dish,dishflavor
     void saveWithFalvor(DishDto  dishDto);

    //根据id查询菜品信息与口味信息
     DishDto getByIdWithFlacor(Long id);

    //更新菜品信息，更新口味信息
    void updateWithFalvor(DishDto dishDto);

    //删除菜品与口味信息
    void removeWithFlavor(List<Long> ids);
}
