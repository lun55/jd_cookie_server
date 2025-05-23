package com.menghen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.menghen.entity.QlEnv;
import com.menghen.config.QlInfo;
import com.menghen.entity.UserCookies;
import com.menghen.mapper.UserCookiesMapper;
import com.menghen.service.IUserCookiesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.menghen.utils.JDUtil;
import com.menghen.utils.QinglongUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lun255
 * @since 2025-05-22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserCookiesServiceImpl extends ServiceImpl<UserCookiesMapper, UserCookies> implements IUserCookiesService {

    final private QlInfo qlInfo;
    final private String KEY = "JD_COOKIE";
    /**
     * 保存用户信息
     * @param cookieDTO
     */
    @Override
    public void saveCookie(UserCookies cookieDTO) {
        // 1. 保存到数据库
        String userName = cookieDTO.getUserName();
        UserCookies userCookie = lambdaQuery().eq(UserCookies::getUserName, userName).one();
        if (userCookie != null) {
            userCookie.setCookie(cookieDTO.getCookie());
            updateById(userCookie);
        }
        else {
            save(cookieDTO);
        }
        // 2.更新到青龙面板
        try {
            getToken(); // 首选获取token
            updateToQingLong(cookieDTO.getCookie()); // 这里是你自定义的方法，调用青龙 API 等
        } catch (Exception e) {
            log.error("同步 Cookie 到青龙失败，手机号：{}，原因：{}", userName, e.getMessage(), e);
            throw new RuntimeException("同步到青龙面板失败");
        }

    }

    private void updateToQingLong(String cookie) throws IOException {
        // 格式化cookie
        Map<String, Object> map = JDUtil.formatCookies(cookie);
        String ptPin = (String) map.get("pt_pin");
        // 从青龙面板中读取"JD_COOKIE"环境变量
        List<QlEnv> qlEnvList = QinglongUtil.getEnvList(qlInfo,KEY);
        // 如果为空则新建一个环境变量列表
        if(qlEnvList==null){
            qlEnvList=new ArrayList<QlEnv>();
        }
        QlEnv targetEnv = null;
        for (QlEnv qlEnv : qlEnvList) {
            Map<String, Object> envMap = JDUtil.formatCookies(qlEnv.getValue());
            String tempPin = (String) envMap.get("pt_pin");
            if(ptPin.equals(tempPin)) {
                targetEnv = qlEnv;
                break;
            }
        }
        // 如果没有则新建一个环境变量
        if (targetEnv == null) {
            targetEnv = new QlEnv();
            targetEnv.setName(KEY);
        }
        cookie = formatCookie(cookie);
        targetEnv.setValue(cookie);

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        QlEnv finalTargetEnv = targetEnv;
        singleThreadExecutor.execute(() -> {
            try {
                // 保存cookie
                boolean success = QinglongUtil.saveEnv(qlInfo, finalTargetEnv);
                QinglongUtil.enableEnv(qlInfo, finalTargetEnv); // 启用Cookie
                if (success) {
                    log.info("更新cookie成功");
                }
            } catch (IOException e) {
                log.error("更新cookie失败：" + e.getMessage());
            }
        });
        singleThreadExecutor.shutdown();

    }

    private void getToken(){
        try {
            String tk = QinglongUtil.login(qlInfo);
            if (StringUtils.isBlank(tk)) {
                log.error("登录失败，token为空");
                return;
            }
            qlInfo.setToken(tk);
        } catch (IOException e) {
            log.error("登录失败:"+ e.getMessage());
        }
    }

    private String formatCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) return "";

        // 移除首尾空格
        cookie = cookie.trim();

        String ptKey = null;
        String ptPin = null;

        // 拆分所有键值对
        String[] parts = cookie.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("pt_key=")) {
                ptKey = part;
            } else if (part.startsWith("pt_pin=")) {
                    ptPin = part; // 解码失败就保留原始
            }
        }
        if (ptKey != null && ptPin != null) {
            return ptKey + ";" + ptPin + ";";
        } else {
            throw new IllegalArgumentException("Cookie 格式错误，必须包含 pt_key 和 pt_pin");
        }
    }


}
