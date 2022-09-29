package com.lpy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lpy.reggie.common.CustomException;
import com.lpy.reggie.entity.Category;
import com.lpy.reggie.entity.Dish;
import com.lpy.reggie.entity.Setmeal;
import com.lpy.reggie.mapper.CategoryMapper;
import com.lpy.reggie.service.Categoryservice;
import com.lpy.reggie.service.DishService;
import com.lpy.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements Categoryservice {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        lambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //查询当前分类是否关联了菜品，如果已经关联，抛出业务异常
        int count = dishService.count(lambdaQueryWrapper);
        if (count>0){
            //已关联菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,id);
        //查询当前分类是否关联了套餐，如果已经关联，抛出业务异常
        int count1 = setmealService.count(lambdaQueryWrapper1);
        if (count1>0){
            //已关联套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }
}
