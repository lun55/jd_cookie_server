package com.menghen.utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.menghen.entity.QlEnv;
import com.menghen.config.QlInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XanderYe
 * @description:
 * @date 2025/5/22 14:04
 */
public class QinglongUtil {

    /**
     * 登录
     * @param qlInfo
     * @return java.lang.String
     * @description:
     * @date 2024/3/22 11:00
     */
    private static final Gson gson = new Gson();

    public static String login(QlInfo qlInfo) throws IOException {
        String url = qlInfo.getAddress();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        url += "/open/auth/token?client_id="+qlInfo.getUsername()+"&client_secret="+qlInfo.getPassword();

        HttpUtil.ResEntity resEntity = HttpUtil.doGet(url, null);
        if (resEntity.getStatusCode() != 200) {
            throw new IOException("服务器" + resEntity.getStatusCode() + "错误");
        }
        JsonObject res = JsonParser.parseString(resEntity.getResponse()).getAsJsonObject();
        if (res.get("code").getAsInt() != 200) {
            throw new IOException(res.get("message").getAsString());
        }
        return res.get("data").getAsJsonObject().get("token").getAsString();
    }


    public static List<QlEnv> getEnvList(QlInfo qlInfo) throws IOException {
        String url = qlInfo.getAddress() + "/api/envs";
        url += "?searchValue=&t=" + System.currentTimeMillis();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + qlInfo.getToken());
        HttpUtil.ResEntity resEntity = HttpUtil.doGet(url, headers, null, null);
        if (resEntity.getStatusCode() != 200) {
            throw new IOException("服务器" + resEntity.getStatusCode() + "错误");
        }
        JsonObject res = JsonParser.parseString(resEntity.getResponse()).getAsJsonObject();
        if (res.get("code").getAsInt() != 200) {
            throw new IOException(res.get("message").getAsString());
        }
        JsonArray dataArray = res.getAsJsonArray("data");
        return gson.fromJson(dataArray, new TypeToken<List<QlEnv>>() {}.getType());
    }
    /**
     * 获取环境变量

     * @date 2024/3/22 11:00
     */
    public static List<QlEnv> getEnvList(QlInfo qlInfo,String key) throws IOException {
        String url = qlInfo.getAddress() + "/open/envs?searchValue="+key;
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + qlInfo.getToken());
        HttpUtil.ResEntity resEntity = HttpUtil.doGet(url, headers, null, null);
        if (resEntity.getStatusCode() != 200) {
            throw new IOException("服务器" + resEntity.getStatusCode() + "错误");
        }
        JsonObject res = JsonParser.parseString(resEntity.getResponse()).getAsJsonObject();
        if (res.get("code").getAsInt() != 200) {
            throw new IOException(res.get("message").getAsString());
        }
        JsonArray dataArray = res.getAsJsonArray("data");
        return gson.fromJson(dataArray, new TypeToken<List<QlEnv>>() {}.getType());
    }

    /**
     * 更新环境变量
     * @param qlInfo
     * @param qlEnv
     * @return boolean
     * @author yclown
     * @description:
     * @date 2024/3/22 11:00
     */
    public static boolean saveEnv(QlInfo qlInfo, QlEnv qlEnv) throws IOException {
        String url = qlInfo.getAddress() + "/open/envs";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", qlEnv.getName());
        paramMap.put("remarks", qlEnv.getRemarks());
        paramMap.put("value", qlEnv.getValue());
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + qlInfo.getToken());
        HttpUtil.ResEntity resEntity;

        if (qlEnv.getId() != null) {
            // 更新
            paramMap.put("id", qlEnv.getId());
            String json = gson.toJson(paramMap);
            resEntity = HttpUtil.doPutJSON(url, headers, null, json);
        } else {
            // 新增
            List<Map<String, Object>> list = Collections.singletonList(paramMap);
            String json = gson.toJson(list);
            resEntity = HttpUtil.doPostJSON(url, headers, null, json);
        }

        if (resEntity.getStatusCode() != 200) {
            throw new IOException("发送失败，服务器状态码：" + resEntity.getStatusCode());
        }

        JsonObject res = JsonParser.parseString(resEntity.getResponse()).getAsJsonObject();
        if (res.get("code").getAsInt() != 200) {
            throw new IOException(res.get("message").getAsString());
        }

        return true;
    }

    public static void enableEnv(QlInfo qlInfo, QlEnv qlEnv) throws IOException {
        if (qlEnv.getId() == null) return;

        String url = qlInfo.getAddress() + "/open/envs/enable";
        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + qlInfo.getToken());
        List<String> idList = Collections.singletonList(qlEnv.getId());
        String json = gson.toJson(idList);

        HttpUtil.ResEntity resEntity = HttpUtil.doPutJSON(url, headers, null, json);

        if (resEntity.getStatusCode() != 200) {
            throw new IOException("启用失败，服务器状态码：" + resEntity.getStatusCode());
        }

        JsonObject res = JsonParser.parseString(resEntity.getResponse()).getAsJsonObject();
        if (res.get("code").getAsInt() != 200) {
            throw new IOException(res.get("message").getAsString());
        }
    }
}