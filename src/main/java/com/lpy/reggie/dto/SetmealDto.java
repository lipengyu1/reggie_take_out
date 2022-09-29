package com.lpy.reggie.dto;

import com.lpy.reggie.entity.Setmeal;
import com.lpy.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
