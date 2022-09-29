package com.lpy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lpy.reggie.entity.Category;

public interface Categoryservice extends IService<Category> {
    public void remove(Long id);
}
