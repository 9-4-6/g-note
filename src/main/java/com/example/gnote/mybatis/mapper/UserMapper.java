package com.example.gnote.mybatis.mapper;

import com.example.gnote.mybatis.entity.SysUser;

/**
 * @author guozhong
 * @date 2026/1/29
 * @description TODO
 */
public interface UserMapper {
        SysUser getUserById(Long id);
}
