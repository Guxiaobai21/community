package com.yingteng.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 工具类
 */
public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");//生成的UUID中包含"-"符合，替换成空的。
    }

    //MD5加密
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * Json处理
     * @param code  编号
     * @param msg   提示
     * @param map   业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null){
            for (String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    //重载方法，处理map为空的情况
    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    //重载方法，处理map和msg都为空的情况
    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    //main方法测试数据
    public static void main(String[] args){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }

}
