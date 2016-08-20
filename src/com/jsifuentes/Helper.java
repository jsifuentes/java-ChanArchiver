package com.jsifuentes;

import com.jsifuentes.core.Configuration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by Jacob on 11/17/2014.
 */
public class Helper {
    public static String readFromURL(String givenURL) throws Exception {
        String output = "";
        URL url = new URL(givenURL);
        // read text returned by server
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while ((line = in.readLine()) != null) {
            output += line;
        }
        in.close();

        return output;
    }

    public static long currentMillis() {
        return System.currentTimeMillis();
    }

    public static String sendPostRequest(String url, String data, HashMap<String, String> headers) throws IOException {
        return sendPostRequest(url, data, headers, true);
    }

    public static String sendPostRequest(String url, String data, HashMap<String, String> headers, boolean throwExceptions) throws IOException {
        try {
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            if (headers != null) {
                for (String headerKey : headers.keySet()) {
                    conn.setRequestProperty(headerKey, headers.get(headerKey));
                }
            }
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            String line;

            InputStream is;
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            int statusCode = httpConn.getResponseCode();
            if (statusCode != 200) {
                is = httpConn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String output = "";
            while ((line = reader.readLine()) != null) {
                output += line;
            }
            writer.close();
            reader.close();

            return output;
        }
        catch(Exception e) {
            if(throwExceptions) {
                throw e;
            } else {
                return "";
            }
        }
    }

    public static String randomString(int length) {
        String AB = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);

        for( int i = 0; i < length; i++ ) {
            sb.append(AB.charAt(new Random().nextInt(AB.length())));
        }

        return sb.toString();
    }
}
