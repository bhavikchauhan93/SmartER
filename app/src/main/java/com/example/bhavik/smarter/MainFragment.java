package com.example.bhavik.smarter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bhavik.smarter.Connections.ElectricityUsage;
import com.example.bhavik.smarter.Helper.Helper;
import com.example.bhavik.smarter.Weather.HTTPWeatherConnection;
import com.example.bhavik.smarter.Weather.Weather;
import com.example.bhavik.smarter.Weather.WeatherJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bhavik on 5/4/18.
 */

public class MainFragment extends Fragment {

    View vMain;
    private TextView tvCityText;
    private TextView tvConditionDrescription;
    private TextView tvTemperature;
    private TextView tvWindSpeed;
    private TextView tvHumidity;
    private ImageView ivConditionIcon;
    private TextView tvTime;
    private TextView tvHourlyUse;
    private TextView tvPositiveMessage;
    private TextView tvNegativeMessage;
    private ImageView ivPositive;
    private ImageView ivNegative;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);

        String fullData = "";
        String userData = "";
        String firstName = "";
        String currentCity = "Melbourne,Au";

        tvCityText = vMain.findViewById(R.id.tv_city_text);
        tvConditionDrescription = vMain.findViewById(R.id.condDescr);
        tvTemperature = vMain.findViewById(R.id.tv_temperature);
        tvHumidity = vMain.findViewById(R.id.tv_humidity);
        tvWindSpeed = vMain.findViewById(R.id.tv_wind_speed);
        ivConditionIcon = vMain.findViewById(R.id.iv_condition_icon);
        tvTime = vMain.findViewById(R.id.tv_time);
        tvHourlyUse = vMain.findViewById(R.id.tv_hourly_use);
        tvPositiveMessage = vMain.findViewById(R.id.tv_positive_message);
        tvNegativeMessage = vMain.findViewById(R.id.tv_negative_message);
        ivPositive = vMain.findViewById(R.id.iv_positive_threshold);
        ivNegative = vMain.findViewById(R.id.iv_negative_threshold);

        // initially set visiblity to INVISIBLE
        tvNegativeMessage.setVisibility(View.INVISIBLE);
        ivNegative.setVisibility(View.INVISIBLE);
        tvPositiveMessage.setVisibility(View.INVISIBLE);
        ivPositive.setVisibility(View.INVISIBLE);

        // getting first name
        firstName = Helper.getFirstName(this.getActivity().getIntent());

        // TODO show welcome message with name

        // Async task to get weather info
        WeatherTask task = new WeatherTask(currentCity);
        task.execute(new String[]{currentCity});

        // Getting the current time and date using Async task
        GetTime currTime = new GetTime();
        currTime.execute();


        // todo *** Retrieving and displaying threshold value ****
        String currentDate = Helper.getCurrentDate();
        Integer currentHour = Helper.getCurrentHour();
        String resID = Helper.getResId(this.getActivity().getIntent());

        getHourlyUsage newHourlyUsageRecord = new getHourlyUsage(currentDate, currentHour.toString(), resID);
        newHourlyUsageRecord.execute(new String []{currentDate, currentHour.toString(), resID});

        return vMain;
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{

        private final String city;

        public WeatherTask(String currentCity) {
            city = currentCity;
        }

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();

            // HTTP GET connection to retrieve data related to weather
            HTTPWeatherConnection newConnection = new HTTPWeatherConnection();
            String weatherData = newConnection.getWeatherData(city);

            try {
                // parsing the data received
                weather = WeatherJSONParser.getWeather(weatherData);


                // HTTP GET connection to retrieve icon related to weather
                HTTPWeatherConnection newIconConnection = new HTTPWeatherConnection();
                weather.iconData = newIconConnection.getWeatherImage(weather.currentCondition.getIcon());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                ivConditionIcon.setImageBitmap(img);
            }

            tvCityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            tvConditionDrescription.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            tvTemperature.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) +  (char) 0x00B0 + "C");
            tvHumidity.setText("" + weather.currentCondition.getHumidity() + "%");
            tvWindSpeed.setText("" + weather.wind.getSpeed() + " mps");
            super.onPostExecute(weather);
        }
    }

    private class GetTime extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            return currentDateTimeString;
        }

        @Override
        protected void onPostExecute(String currDate) {
            tvTime.setText(currDate);
            super.onPostExecute(currDate);
        }
    }

    public class getHourlyUsage extends AsyncTask<String, Void, JSONObject>{

        private String currentDate;
        private String currentHour;
        private String resID;

        public getHourlyUsage(String currDate, String currHour, String resid) {
            currentDate = currDate;
            currentHour = currHour;
            resID = resid;

        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            JSONObject jsonResult = null;
            try {
                // Simulate network access.
                Thread.sleep(2000);

                // Authenticate connection
                jsonResult = ElectricityUsage.getHourlyUsageData(currentDate, currentHour, resID);
            } catch (InterruptedException e) {
                return null;
            }
            return jsonResult;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            String status = "";
            String totalUsage = "";

            try {
                if (jsonObject.getString("Status").equals("Success")){
                    totalUsage = jsonObject.getString("Total Hourly usage for 3 appliances:");
                }else if(jsonObject.getString("Status").equals("Failed")){
                    // default value
                    totalUsage = "1.5";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            tvHourlyUse.setText("Current Hourly Usage: " + totalUsage);
            Double val = Double.parseDouble(totalUsage);
            Integer currentHour = Helper.getCurrentHour();
            Boolean peakHour = currentHour > 9 && currentHour < 22;
            if (val > 1.5 && peakHour){
                tvNegativeMessage.setVisibility(View.VISIBLE);
                ivNegative.setVisibility(View.VISIBLE);
            }else {
                tvPositiveMessage.setVisibility(View.VISIBLE);
                ivPositive.setVisibility(View.VISIBLE);
            }

            //super.onPostExecute(jsonObject);
        }
    }
}
