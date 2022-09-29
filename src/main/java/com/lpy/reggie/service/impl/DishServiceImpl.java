package com.lpy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lpy.reggie.common.CustomException;
import com.lpy.reggie.dto.DishDto;
import com.lpy.reggie.entity.Dish;
import com.lpy.reggie.entity.DishFlavor;
import com.lpy.reggie.mapper.DishMapper;
import com.lpy.reggie.service.DishFlavorService;
import com.lpy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.callback.LanguageCallback;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，并保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFalvor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //添加菜品id
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
               item.setDishId(dishId);
               return item;
        }).collect(Collectors.toList());
        //保存菜品口味到菜品口味表dishflavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息与口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlacor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息与口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFalvor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味信息 delete
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //添加当前菜品对应口味信息 insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品与口味信息
     * @param ids
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //select count(*) form dish where id in (1,2,3) and status = 1;
        lambdaQueryWrapper.in(Dish::getId,ids);
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        int count = this.count(lambdaQueryWrapper);
        if (count>0){
            //不能删除，抛出业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //删除菜品表中的数据
        this.removeByIds(ids);
        //删除口味表的数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper1);
    }
}
