package com.example.bhavik.smarter.Connections;

import android.util.Log;

import com.example.bhavik.smarter.Helper.Helper;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ElectricityUsage {

    private static final String BASE_URI="http://10.0.2.2:8080/SmartER/webresources";

    public static JSONObject getHourlyUsageData(String usageDate, String usageHour, String resId)  {

        final String methodPath = "/smarter.electricityusage/findHourlyPowerUsageForAllappliance/" + resId + "/" + usageDate + "/" + usageHour;

        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        // making HTTP request

        try{
            url = new URL(BASE_URI + methodPath);
            // opening connection
            conn = (HttpURLConnection) url.openConnection();
            // setting connection
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(1500);
            // set connection method to GET
            conn.setRequestMethod("GET");
            // adding HTTP headers to set response type to JSON
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept","application/json");
            // Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            // Read the input stream and store it as String
            Log.i("error",new Integer(conn.getResponseCode()).toString());

            while(inStream.hasNext()){
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        // convert String to JSON object from helper function
        JSONObject jsonResult = Helper.convertToJSON(textResult);
        return jsonResult;
    }
}
