package com.example.bhavik.smarter.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherJSONParser {

    public static Weather getWeather(String weatherData){
        Weather weather = new Weather();

        // Retrieving relevant information
        Location loc = new Location();

        // initialising
        JSONObject coordObj = null;
        JSONObject sysObj = null;
        JSONArray weatherArray = null;
        JSONObject weatherObj = null;
        JSONObject mainObj = null;
        JSONObject windObj = null;

        try {
            // creating JSON Object from the data retrieved from Open Weather map
            JSONObject jsonObj = new JSONObject(weatherData);


            // *** getting coord object from jsonObj ***
            coordObj = jsonObj.getJSONObject("coord");
            // getting latitide and longitude from coord object
            float lat = (float) coordObj.getDouble("lat");
            loc.setLatitude(lat);
            float lon = (float) coordObj.getDouble("lon");
            loc.setLongitude(lon);

            // *** getting weather information from an array in jsonObj ***
            weatherArray = jsonObj.getJSONArray("weather");
            // using the first object of the array
            weatherObj = weatherArray.getJSONObject(0);
            // getting elevant items from the weatherObj
            weather.currentCondition.setWeatherId(weatherObj.getInt("id"));
            weather.currentCondition.setDescr(weatherObj.getString("description"));
            weather.currentCondition.setCondition(weatherObj.getString("main"));
            weather.currentCondition.setIcon(weatherObj.getString("icon"));

            // *** getting temperature and humidity from main object in jsonObj ***
            mainObj = jsonObj.getJSONObject("main");
            weather.currentCondition.setHumidity(mainObj.getInt("humidity"));
            float maxTemp = (float) mainObj.getDouble("temp_max");
            weather.temperature.setMaxTemp(maxTemp);
            float minTemp = (float) mainObj.getDouble("temp_min");
            weather.temperature.setMaxTemp(minTemp);
            float temp = (float) mainObj.getDouble("temp");
            weather.temperature.setTemp(temp);

            // *** getting wind information from wind object in jsonObj ***
            windObj = jsonObj.getJSONObject("wind");
            // getting relevant items from wind object
            float windVal = (float) windObj.getDouble("speed");
            weather.wind.setDeg(windVal);
            float windDeg = (float) windObj.getDouble("deg");
            weather.wind.setDeg(windDeg);

            // *** getting sys object from jsonObj ***
            sysObj = jsonObj.getJSONObject("sys");
            // getting relevant items from sys object
            loc.setCountry(sysObj.getString("country"));

            // *** getting city from jsonObj ***
            loc.setCity(jsonObj.getString("name"));



            weather.location = loc;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weather;
    }
}
