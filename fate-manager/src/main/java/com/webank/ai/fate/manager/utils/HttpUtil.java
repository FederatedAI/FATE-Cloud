package com.webank.ai.fate.manager.utils;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@Data
@Service
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String doPost(String postUrl, String postData, Map<String, String> headers) {
        BufferedReader in = null;
        try {
            //发送POST请求
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setConnectTimeout(7000);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", "" + postData.length());

            if (headers != null && headers.size() > 0) {
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();
            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                logger.info("{}|{}|{}|{}|{}",postUrl,postData,conn.getResponseCode(),conn.getResponseMessage(),null);
                return "";
            }
            //获取响应内容体
            StringBuffer sb = new StringBuffer();
            String readLine = new String();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((readLine = in.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            logger.info("{}|{}|{}|{}|{}",postUrl,postData,conn.getResponseCode(),conn.getResponseMessage(),sb.toString());
            return sb.toString();
        } catch (IOException e) {
            logger.error("doPost Http failed! " + e.getMessage());
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("sendGet close Exception:" + e2.getMessage());
            }
        }
        return "";
    }

    public static String doGet(String url, String param, Map<String, String> headers) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            if (headers != null && headers.size() > 0) {
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("sendGet Exception:" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                logger.error("sendGet close Exception:" + e2.getMessage());
            }
        }
        return result;
    }
}
