package com.menghen.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QlApiLogin {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String loginWithClientCredentials(String qlUrl, String clientId, String clientSecret) throws Exception {
        String loginUrl = qlUrl + "/open/auth/token";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        log.info("使用 client_id 和 client_secret 登录 QL 面板: {}", loginUrl);

        ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode json = objectMapper.readTree(response.getBody());
            if (json.get("code").asInt() == 200) {
                String token = json.get("data").get("token").asText();
                log.info("QL 登录成功，token={}", token);
                return token;
            } else {
                throw new Exception("QL 登录失败：" + json.toString());
            }
        } else {
            throw new Exception("QL 请求失败，状态码: " + response.getStatusCode());
        }
    }
}
