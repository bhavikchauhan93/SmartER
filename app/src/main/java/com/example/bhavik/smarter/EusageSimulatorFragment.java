package com.example.bhavik.smarter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bhavik.smarter.Helper.Helper;
import com.example.bhavik.smarter.SQLiteDB.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by bhavik on 5/4/18.
 */

public class EusageSimulatorFragment extends Fragment {

    View vEusageSimulator;
    ImageView ivTransferDB;

    private DBManager dbManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vEusageSimulator = inflater.inflate(R.layout.fragment_eusage_simulator, container, false);

        ivTransferDB = (ImageView) vEusageSimulator.findViewById(R.id.iv_transfer_icon);
        ivTransferDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject useageObject = null;

                // Storing values in SQLite DB
                dbManager = new DBManager(getActivity());
                // Opening DB
                try {
                    dbManager.open();
                }catch(SQLException e) {
                    e.printStackTrace();
                }

                // getting all data
                Cursor c = dbManager.getAllRecords();

                if (c.moveToFirst()) {
                    do {
                        //Creating a JSON object to put record in DB (resident table)
                        useageObject = new JSONObject();
                        try {
                            useageObject.put("Usage Id", c.getString(0));
                            useageObject.put("Date", c.getString(1));
                            useageObject.put("Hour", c.getString(2));
                            useageObject.put("Fridge", c.getString(3));
                            useageObject.put("AirCon", c.getString(4));
                            useageObject.put("WashingMachine", c.getString(5));
                            useageObject.put("Temperature", c.getString(6));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } while (c.moveToNext());
                }

                // closing connection
                dbManager.close();

                // getting resident Id
                String resId = Helper.getResId(getActivity().getIntent());
                try {
                    // adding resident id in object
                    useageObject.put("Resident Id", resId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // call Async to add usage Data
                AddUsageData newUserRecord = new AddUsageData(useageObject.toString());
                newUserRecord.execute(new String []{useageObject.toString()});
            }
        });
        return vEusageSimulator;
    }

    public class AddUsageData extends AsyncTask<String, Void, String> {

        private static final String BASE_URI="http://10.0.2.2:8080/SmartER/webresources";

        private final String usageRecordObject;

        public AddUsageData(String useRecordObj) {
            usageRecordObject = useRecordObj;
        }

        @Override
        protected String doInBackground(String... strings) {

            //initialise
            URL url;
            HttpURLConnection conn = null;
            final String methodPath = "/smarter.electricityusage/AddUsageData";
            String status = "";

            try {
                url = new URL(BASE_URI + methodPath);
                //opening the connection
                conn = (HttpURLConnection) url.openConnection();
                //set the timeout
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                //setting the connection method to POST
                conn.setRequestMethod("POST");
                //setting the output to true
                conn.setDoOutput(true);
                JSONObject j = new JSONObject();
                j.put("jsonObject", usageRecordObject);

                String urlParameters = j.toString();

                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                //add HTTP headers
                conn.setRequestProperty("Content-Type", "application/json");

                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(postData);
                } catch (Exception e){
                    e.printStackTrace();
                }

                StringBuilder content;

                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {

                    String line;
                    content = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        content.append(line);
                        //content.append(System.lineSeparator());
                    }
                }

                System.out.println(content.toString());
                status = content.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("Success")){

                //Context context = getApplicationContext();
                CharSequence text = "Record SUCCESSFULLY added!";
                int duration = Toast.LENGTH_SHORT;

                Context mContext = getActivity();
                Toast toast = Toast.makeText(mContext, text, duration);
                toast.show();

            }else {

                //Context context = getApplicationContext();
                CharSequence text = "FAILED to add Record";
                int duration = Toast.LENGTH_SHORT;

                Context mContext = getActivity();
                Toast toast = Toast.makeText(mContext, text, duration);
                toast.show();

            }

            super.onPostExecute(s);
        }
    }

}
