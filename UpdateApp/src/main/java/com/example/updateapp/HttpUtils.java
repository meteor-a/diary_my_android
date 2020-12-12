package com.example.updateapp;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.updateapp.BuildConfig;


/**
 * @author feicien (ithcheng@gmail.com)
 * @since 2016-07-05 19:25
 */
public class HttpUtils {


    public static String get(String urlStr) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        try {
            URL url = new URL(urlStr);
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("ver_code", 2);
            params.put("api", String.valueOf(Build.VERSION.SDK_INT));
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("POST");
            uRLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            uRLConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            uRLConnection.setDoOutput(true);
            uRLConnection.getOutputStream().write(postDataBytes);

            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            result = strBuilder.toString();
            String result_tmp = "";
            for (int ii = 0; ii < result.length(); ++ii) {
                if (result.charAt(ii) == '#') {
                    result_tmp += '/';
                } else {
                    result_tmp += result.charAt(ii);
                }
            }
            result = result_tmp;
        } catch (Exception e) {
            Log.e(Constants.TAG, "https  error");
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {

                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
        return result;
    }
}
