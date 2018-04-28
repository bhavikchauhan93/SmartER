package com.example.bhavik.smarter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bhavik.smarter.Connections.AuthConnection;
import com.example.bhavik.smarter.Connections.ElectricityUsage;
import com.example.bhavik.smarter.Helper.Helper;
import com.example.bhavik.smarter.SQLiteDB.DBManager;
import com.example.bhavik.smarter.Weather.HTTPWeatherConnection;
import com.example.bhavik.smarter.Weather.Weather;
import com.example.bhavik.smarter.Weather.WeatherJSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HandshakeCompletedListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // floating mail icon
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // To activate fragments
        getSupportActionBar().setTitle("SmartER");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();

        // *** repeating task to simulate usage data ***
        SharedPreferences.Editor editor = getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor.putInt("IdCount", 0);
        editor.putString("Fridge", "0.0");
        editor.putString("AirCon", "0.0");
        editor.putString("AirConCount", "0");
        editor.putString("WashingMachine", "0.0");
        editor.putString("WashingMachineCount", "0");
        editor.apply();


        // deleting DB records
        dbManager = new DBManager(this);
        // Opening DB
        try {
            dbManager.open();
            dbManager.deleteAllRecords();
            dbManager.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }

        // calling timer
        Intent intent = this.getIntent();
        Timer timer = new Timer();
        timer.schedule(new SimulatorTask(intent, this), 1000, 3600000);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;

        if (id == R.id.nav_eusage_simulator) {
           nextFragment = new EusageSimulatorFragment();

        } else if (id == R.id.nav_home) {
            nextFragment = new MainFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, nextFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

class SimulatorTask extends TimerTask {

    private Intent mainIntent;
    private Context activityContext;

    protected DBManager dbManager;


    public SimulatorTask(Intent intent, Context context){
        mainIntent = intent;
        activityContext = context;

    }

    public void run(){

        String userData = "";
        String resId = "";
        String currentCity = "Melbourne,Au";


        // getting current date
        String currentDate = Helper.getCurrentDate();

        // getting current hour
        Integer currentHour = Helper.getCurrentHour();

        // getting resident Id
        resId = Helper.getResId(mainIntent);

        // getting current temperature
        Integer currentTemp = getTemp(currentCity);

        //todo  **** delete SQLite DB records and send data to REST DB at 12:00 am ****
        if (currentHour == 00){

        }


        // get value from shared preferences
        SharedPreferences prefs = activityContext.getSharedPreferences("Simulator",Context.MODE_PRIVATE);
        Integer IdCount = prefs.getInt("IdCount", 0);
        String f = prefs.getString("Fridge", "0.0");
        String ac = prefs.getString("AirCon", "0.0");
        String acc = prefs.getString("AirConCount", "0");
        String wm = prefs.getString("WashingMachine", "0.0");
        String wmc = prefs.getString("WashingMachineCount", "0");

        // generating random appliance values
        Double washingMachine = getWashingMachineVal(currentHour, wm, wmc);
        Double airCon = getAirConVal(currentHour, currentTemp, ac, acc);
        Double fridge = getFridgeVal(f);


        // Storing values in SQLite DB
        dbManager = new DBManager(activityContext);
        // Opening DB
        try {
            dbManager.open();
        }catch(SQLException e) {
            e.printStackTrace();
        }

        //Inserting record
        dbManager.insertRecord(IdCount.toString(), currentDate.toString(), currentHour.toString(), fridge.toString(), airCon.toString(), washingMachine.toString(), currentTemp.toString());
        // Closing DB
        dbManager.close();

        // updating ID count values in shared preferences
        SharedPreferences.Editor editor = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor.remove("IdCount");
        editor.apply();

        SharedPreferences.Editor editor2 = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor2.putInt("IdCount", IdCount + 1);
        editor2.apply();

    }

    // getting current tempearture value from Open Weather map
    private Integer getTemp(String currentCity){

        Weather weather = new Weather();

        // HTTP GET connection to retrieve data related to weather
        HTTPWeatherConnection newConnection = new HTTPWeatherConnection();
        String weatherData = newConnection.getWeatherData(currentCity);

        try {
            // parsing the data received
            weather = WeatherJSONParser.getWeather(weatherData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) (weather.temperature.getTemp() - 273.15);
    }

    // generating random usage value for washing machine
    private Double getWashingMachineVal(Integer currentHour, String wm, String wmc){
        Double value;
        Integer count = Integer.parseInt(wmc);
        Boolean b = false;

        // first time
        if (count == 0 && (currentHour > 6 && currentHour < 21)) {
            Random ran = new Random();
            b = ran.nextBoolean();
            // decide if to start generating
            if (b) {
                Random r = new Random();
                value = 0.9 * r.nextDouble() + 0.4 ;
                value = Math.round(value*100.0)/100.0;
                count += 1;
            } else {
                value = 0.0;
            }
        }else if ((currentHour > 6 && currentHour < 21) && count != 3) {
                value = Double.parseDouble(wm);
                count += 1;
        } else {
            value = 0.0;
        }


        SharedPreferences.Editor editor = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor.remove("WashingMachine");
        editor.remove("WashingMachineCount");
        editor.apply();

        SharedPreferences.Editor editor2 = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor2.putString("WashingMachine", value.toString());
        editor2.putString("WashingMachineCount", count.toString());
        editor2.apply();

        return value;
    }

    // generating random usage value for Air conditioner
    private Double getAirConVal(Integer currentHour, Integer currentTemp, String ac, String acc) {
        Double value;
        Integer count = Integer.parseInt(acc);

        // only generate max 10 values between 9 am and 11 pm and when temperature greater than 20
        if ((currentHour > 9 && currentHour < 23) && count != 11 && currentTemp > 20){
            Random ran = new Random();
            Boolean b = ran.nextBoolean();
            // deciding randomly if a value should be generated
            if (b){
                if(ac.equals("0.0")){
                    Random r = new Random();
                    value = 4 * r.nextDouble() + 1 ;
                    value = Math.round(value*100.0)/100.0;
                    count += 1;
                } else {
                    value = Double.parseDouble(ac);
                    count += 1;
                }
            } else {
                value = 0.0;
            }
        }else {
            value = 0.0;
        }


        SharedPreferences.Editor editor = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor.remove("AirCon");
        editor.remove("AirConCount");
        editor.apply();

        SharedPreferences.Editor editor2 = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor2.putString("AirCon", value.toString());
        editor2.putString("AirConCount", count.toString());
        editor2.apply();

        return value;
    }

    // generating random usage value for Fridge
    private Double getFridgeVal(String f) {
        Double value;

        if(f.equals("0.0")){
            Random r = new Random();
            value = 0.5 * r.nextDouble() + 0.3 ;
            value = Math.round(value*100.0)/100.0;
        } else {
            value = Double.parseDouble(f);
        }

        SharedPreferences.Editor editor = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor.remove("Fridge");
        editor.apply();

        SharedPreferences.Editor editor2 = activityContext.getSharedPreferences("Simulator", Context.MODE_PRIVATE).edit();
        editor2.putString("Fridge", value.toString());
        editor2.apply();
        return value;
    }
}
