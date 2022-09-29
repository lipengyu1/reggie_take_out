package com.lpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lpy.reggie.dto.SetmealDto;
import com.lpy.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    SetmealDto getByIdWithSetmealDish(Long id);

    void removeWithSetmealDish(List<Long> ids);

    void updateWithSetmealDish(SetmealDto setmealDto);
}
