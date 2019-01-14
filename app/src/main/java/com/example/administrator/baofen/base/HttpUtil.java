package com.example.administrator.baofen.base;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    /**
     * POST请求
     */
    public static Map<String, Object> doPost(String url, Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", null);
        result.put("code", 200);
        result.put("msg", null);

        OutputStream out = null;
        DataOutputStream dataOutputStream = null;
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            // 设置是否向httpUrlConnection输出，post请求，参数要放在http正文内，因此需要设为true,
            // 默认情况下是false;
            httpUrlConnection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // 忽略缓存
            httpUrlConnection.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.connect();

            // 建立输入流，向指向的URL传入参数

            String queryString = "";

            if (params != null) {
                for (Entry<String, Object> entry : params.entrySet()) {
                    queryString += entry.getKey()
                            + "="
                            + URLEncoder.encode(entry.getValue().toString(),
                            "UTF-8") + "&";
                }
            }

            if (queryString.length() > 0) {
                queryString = queryString
                        .substring(0, queryString.length() - 1);
                out = httpUrlConnection.getOutputStream();
                dataOutputStream = new DataOutputStream(out);
                dataOutputStream.writeBytes(queryString);

                dataOutputStream.flush();
                out.flush();
            }

            // 获得响应状态
            int responseCode = httpUrlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                in = httpUrlConnection.getInputStream();
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }

                result.put("success", true);
                result.put("data", baos.toString("UTF-8"));
                result.put("code", 200);
                result.put("msg", "请求成功");
            } else {
                result.put("success", false);
                result.put("code", responseCode);
                result.put("msg", "请求异常");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("msg",
                    "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    result.put("success", false);
                    result.put("code", 500);
                    result.put("msg",
                            "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    result.put("success", false);
                    result.put("code", 500);
                    result.put("msg",
                            "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
                }
            }
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    result.put("success", false);
                    result.put("code", 500);
                    result.put("msg",
                            "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    result.put("success", false);
                    result.put("code", 500);
                    result.put("msg",
                            "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * GET请求
     */
    public static Map<String, Object> doGet(String url, Map<String, Object> params, String ua) {

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", null);
        result.put("code", 200);
        result.put("msg", null);

        InputStream in = null;
        ByteArrayOutputStream baos = null;

        try {
            // URL传入参数
            String queryString = "";

            if (params != null) {
                for (Entry<String, Object> entry : params.entrySet()) {
                    queryString += entry.getKey()
                            + "="
                            + URLEncoder.encode(entry.getValue().toString(),
                            "UTF-8") + "&";
                }
            }

            if (queryString.length() > 0) {
                queryString = queryString
                        .substring(0, queryString.length() - 1);

                url = url + "?" + queryString;
            }

            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            httpUrlConnection.setRequestProperty("accept", "*/*");
            httpUrlConnection.setRequestProperty("connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("user-agent", ua);

            // 设置是否向httpUrlConnection输出，post请求，参数要放在http正文内，因此需要设为true,
            // 默认情况下是false;
            httpUrlConnection.setDoOutput(false);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // 忽略缓存
            httpUrlConnection.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            // 获得响应状态
            int responseCode = httpUrlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                in = httpUrlConnection.getInputStream();
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }

                result.put("success", true);
                result.put("data", baos.toString("UTF-8"));
                result.put("code", 200);
                result.put("msg", "请求成功");
            } else {
                result.put("success", false);
                result.put("code", responseCode);
                result.put("msg", "请求异常");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("msg",
                    "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    result.put("success", false);
                    result.put("code", 500);
                    result.put("msg",
                            "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    result.put("success", false);
                    result.put("code", 500);
                    result.put("msg",
                            "请求异常，异常信息：" + e.getClass() + "->" + e.getMessage());
                }
            }
        }

        return result;
    }

    public static String sendGet(String url, String param) {
        String result = "";
        String urlName = url + "?" + param;
        try {
            URL realURL = new URL(urlName);
            URLConnection conn = realURL.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
            conn.connect();
            Map<String, List<String>> map = conn.getHeaderFields();
            for (String s : map.keySet()) {
                System.out.println(s + "-->" + map.get(s));
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}