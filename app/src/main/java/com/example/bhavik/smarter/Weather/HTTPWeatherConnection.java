package com.example.bhavik.smarter.Weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPWeatherConnection {

    //openweathermap api id
    final String API_ID= "d860c7290f7a08638c1c0c1dde4bd884";
    final String BASE_URI = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static String IMG_URL = "http://openweathermap.org/img/w/";


    public String getWeatherData(String currentLocation) {
        HttpURLConnection conn = null;
        InputStream is = null;
        final String fullURL = BASE_URI + currentLocation + "&APPID=" + API_ID;

        try {
            conn = (HttpURLConnection) (new URL(fullURL)).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            conn.disconnect();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getWeatherImage(String code) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            conn = (HttpURLConnection) (new URL(IMG_URL + code + ".png")).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            conn.connect();

            // Reading response
            is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(buffer) != -1)
                baos.write(buffer);

              return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
