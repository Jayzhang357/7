package com.zhd.shj.qx;


import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;


import com.zhd.AppHelper;
import com.zhd.shj.BigConfigxml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignatureDemo {
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public static void Abc(String deviceId, String deviceType, Context context) throws Exception {
        String sik = "G48c2lp2197d";// 请填写您申请的sik
        String sis = "3ccc867fc5173cf2";// 请填写您申请的sis
        BigConfigxml abc=new BigConfigxml(context);
        sik= abc.sik;
        sis= abc.sis;
        String apiName = "qxwz.DeviceGroup.AllocateAccount";// 访问的API接口名称
        String apiPath = "/rest/" + apiName + "/" + "gk" + "/" + sik;// API路径，注意没有问号

        // 参数列表(加签的时候需要进行字典序升序排序)
        final Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("deviceId", deviceId);
        paramMap.put("deviceType",deviceType);
        // 毫秒级时间戳，一定是毫秒级！重要！！！
        final String timestamp = String.valueOf(System.currentTimeMillis());
        // paramMap.put("wz-acs-timestamp", timestamp);
        String signatureStr = SignatureDemo.doHmacSHA2(apiPath, paramMap, sis,
                timestamp);
        System.out.println("加签值：" + signatureStr);// 打印sign值

        String qxwzUrl = "http://openapi.qxwz.com";// 千寻的服务器地址
        final String queryUrl = qxwzUrl + apiPath + "?_sign=" + signatureStr;
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 发送http请求，获取返回值
                try {
                    doHttpPost(queryUrl, paramMap, timestamp);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();

        // 发送http请求，获取返回值
        // sendPostMessage(queryUrl, paramMap, timestamp);
    }

    public static void doHttpPost(String url, Map<String, String> paramMap,
                                  String timestamp) throws Exception {

        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (entry.getValue() != null) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        RequestBody requestBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("wz-acs-timestamp", timestamp)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                String[] abc = responseBody.split("\":\"");
                if (abc.length == 3) {
                    AppHelper.AS = abc[1].split("\",\"")[0];
                    AppHelper.AK = abc[2].split("\"")[0];
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
        }
    }
    /*
     * 将字节数组转换成16进制字符串
     */
    public static String encodeHexStr(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] digital = "0123456789ABCDEF".toCharArray();
        char[] result = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            result[i * 2] = digital[(bytes[i] & 0xf0) >> 4];
            result[i * 2 + 1] = digital[bytes[i] & 0x0f];
        }
        return new String(result);
    }

    /*
     * 加签算法
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public static <T> String doHmacSHA2(String path, Map<String, T> params,
                                        String key, String timestamp) {

        List<Entry<String, T>> parameters = new ArrayList<Entry<String, T>>(
                params.entrySet());
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
                "HmacSHA256");
        Charset CHARSET_UTF8 = Charset.forName("UTF-8");
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        if (path != null && path.length() > 0) {
            mac.update(path.getBytes(CHARSET_UTF8));
        }
        if (parameters != null) {
            Collections.sort(parameters, new MapEntryComparator<String, T>());
            for (Entry<String, T> parameter : parameters) {
                byte[] name = parameter.getKey().getBytes(CHARSET_UTF8);
                Object value = parameter.getValue();
                if (value instanceof Collection) {
                    for (Object o : (Collection) value) {
                        mac.update(name);
                        if (o != null) {
                            mac.update(o.toString().getBytes(CHARSET_UTF8));
                        }
                    }
                } else {
                    mac.update(name);
                    if (value != null) {
                        mac.update(value.toString().getBytes(CHARSET_UTF8));
                    }
                }
            }
        }
        if (timestamp != null && timestamp.length() > 0) {
            mac.update(timestamp.toString().getBytes(CHARSET_UTF8));
        }
        return encodeHexStr(mac.doFinal());
    }
}

/*
 * Map参数排序类
 */

class MapEntryComparator<K, V> implements Comparator<Entry<K, V>> {

    @Override
    public int compare(Entry<K, V> o1, Entry<K, V> o2) {
        if (o1 == o2) {
            return 0;
        }
        final String k1 = o1.getKey().toString();
        final String k2 = o2.getKey().toString();
        int l1 = k1.length();
        int l2a = k2.length();
        for (int i = 0; i < l1; i++) {
            char c1 = k1.charAt(i);
            char c2;
            if (i < l2a) {
                c2 = k2.charAt(i);
            } else {
                return 1;
            }
            if (c1 > c2) {
                return 1;
            } else if (c1 < c2) {
                return -1;
            }
        }
        if (l1 < l2a) {
            return -1;
        } else if (l1 == l2a) {
            return 0;
        } else {
            return -1;
        }
    }
}
