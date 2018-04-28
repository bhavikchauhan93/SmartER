package com.example.bhavik.smarter.Helper;

import android.content.Intent;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by bhavik on 17/4/18.
 */

public class Helper {

    // getting Resident Id
    public static String getResId(Intent intent){

        String userData = "";
        String resId = "";
        // getting resident Id
        String fullData = intent.getStringExtra("fullData");

        // convert full data to JSON object
        JSONObject fullObj = new JSONObject();
        fullObj = Helper.convertToJSON(fullData);

        // retrieve user data from full data and convert to JSON
        try {
            userData = fullObj.getString("Cred Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // extract Credential data from full object
        JSONObject userObj = new JSONObject();
        userObj = Helper.convertToJSON(userData);

        // retrieve resident ID from cred data
        try {
            resId = userObj.getString("Resident Id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resId;
    }

    // getting first name
    public static String getFirstName(Intent intent){

        String fullData;
        String userData = "";
        String firstName = "";

        fullData = intent.getStringExtra("fullData");

        // convert full data to JSON object
        JSONObject fullObj = new JSONObject();
        fullObj = Helper.convertToJSON(fullData);

        // retrieve user data from full data and convert to JSON
        try {
            userData = fullObj.getString("User Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // extract user data from full object
        JSONObject userObj = new JSONObject();
        userObj = Helper.convertToJSON(userData);

        // extract First name
        try {
            firstName = userObj.getString("First Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstName;
    }

    // getting current Date
    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format1.format(cal.getTime());
        return currentDate;
    }

    // getting current hour
    public static Integer getCurrentHour(){

        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DATE, 1);
        SimpleDateFormat format2 = new SimpleDateFormat("HH");
        String hour = format2.format(cal2.getTime());
        Integer currentHour = Integer.parseInt(hour);
        return currentHour;
    }

    //converting String to JSON object
    public static JSONObject convertToJSON(String stringData) {
        JSONObject jsonResult = null;
        try {
            jsonResult = new JSONObject(stringData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    // Hashing password
    public static String hashPassword(String password) {
        String hashedPassword = "";
        String finalHashedPassword = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            hashedPassword = Base64.encodeToString(hash, Base64.DEFAULT);
            //System.out.println("Hash value: " + encoded);
            // resolving the issue of forward slash created by SHA for some passwords
            // replacing '/' with 'b'
            finalHashedPassword = hashedPassword.replaceAll("/", "b");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return finalHashedPassword;
    }

    // password validation check
    public static String validatePassword(String password) {

        int min = 8;
        int max = 16;
        int digit = 0;
        int special = 0;
        int upCount = 0;
        int loCount = 0;

        if (password.length() < min) {
            return "short";
        } else if (password.length() > max) {
            return "long";
        } else if (password.length() >= min && password.length() <= max) {
            for (int i = 0; i < password.length(); i++) {
                char c = password.charAt(i);
                if (Character.isUpperCase(c)) {
                    upCount++;
                }
                if (Character.isLowerCase(c)) {
                    loCount++;
                }
                if (Character.isDigit(c)) {
                    digit++;
                }
                if (c >= 33 && c <= 46 || c == 64) {
                    special++;
                }
            }
            if (special < 1 || loCount < 1 || upCount < 1 || digit < 1) {
                return "invalid";
            }
        }
        return "valid";
    }
}

