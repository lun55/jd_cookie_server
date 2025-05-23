package com.menghen.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author XanderYe
 * @description:
 * @date 2022/5/11 14:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component  // 记得加上这个注解
@ConfigurationProperties(prefix = "ql")  // 添加此注解
public class QlInfo {
    private String address;

    private Boolean oldVersion;

    // client_id
    private String username;
    // client_secret
    private String password;

    private String token;
}