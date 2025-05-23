package com.menghen.service;

import com.menghen.entity.UserCookies;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2025-05-22
 */
public interface IUserCookiesService extends IService<UserCookies> {

    void saveCookie(UserCookies cookieDTO);
}
