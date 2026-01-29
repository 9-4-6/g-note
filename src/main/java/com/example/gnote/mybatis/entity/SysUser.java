package com.example.gnote.mybatis.entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author guozhong
 * @since 2026-01-06
 */
@Getter
@Setter
public class SysUser implements Serializable {

	/**
     * 用户id
     */
    private Long id;

    /**
     * 租户id
     */
    private String tenantId;
	/**
	 * 姓名
	 */
	private String realName;

	/**
	 * 性别;女-0；男-1；
	 */
	private Integer gender;

	/**
	 * 头像
	 */
	private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String mobileNo;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 最近登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 是否删除;默认0；0-未删除；1-已删除
     */
	private Integer deleted;
}
