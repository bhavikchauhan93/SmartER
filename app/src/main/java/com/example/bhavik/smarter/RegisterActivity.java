package com.example.bhavik.smarter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//import com.example.bhavik.smarter.Connections.AddNewUser;
import com.example.bhavik.smarter.Helper.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFirstName, etLastName, etDob, etAddress, etPostcode, etMobile, etEmail, etUsername, etPassword;
    private Spinner sNumRes, sEnergyProvider;
    private Button btSubmit, btCancel;

    // Date picker variables
    Context context = this;
    Calendar myCalendar = Calendar.getInstance();
    String dateFormat = "yyyy-MM-dd";
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etDob = findViewById(R.id.et_dob);
        etDob.setOnClickListener(this);
        etAddress = findViewById(R.id.et_address);
        etPostcode = findViewById(R.id.et_postcode);
        etMobile = findViewById(R.id.et_mobile);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);
        sNumRes = findViewById(R.id.sp_num_of_res);
        sEnergyProvider = findViewById(R.id.sp_energy_provider);
        btSubmit = findViewById(R.id.bt_submit);
        btSubmit.setOnClickListener(this);
        btCancel = findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(this);

        // set the username value to EditText entered by user at login
        etUsername.setText(getIntent().getStringExtra("Username").toString());

        // init - set date to current date
        long currentdate = System.currentTimeMillis();
        String dateString = sdf.format(currentdate);
        etDob.setText(dateString);

        // set calendar date and update editDate
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        // onclick - popup datepicker
        etDob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateDate() {
        etDob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v){

        switch (v.getId()) {

            case R.id.bt_submit:
                // when submit button is clicked

                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String dob = etDob.getText().toString();
                String address = etAddress.getText().toString();
                String postcode = etPostcode.getText().toString();
                String mobile = etMobile.getText().toString();
                String email = etEmail.getText().toString();
                String numOfRes = sNumRes.getSelectedItem().toString();
                String energyProvider = sEnergyProvider.getSelectedItem().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();


                // validate input data
                if (firstName.isEmpty()) {
                    etFirstName.setError("First Name is required !!!");
                    return;
                }

                if (dob.isEmpty()) {
                    etDob.setError("Date of Birth is required !!!");
                    return;
                }

                if (address.isEmpty()) {
                    etAddress.setError("Address is required !!!");
                    return;
                }

                if (postcode.isEmpty()) {
                    etPostcode.setError("Postcode is required !!!");
                    return;
                }

                if (mobile.isEmpty()) {
                    etMobile.setError("Mobile number is required !!!");
                    return;
                }

                if(email.isEmpty()) {
                    etEmail.setError("Email address is required !!!");
                    return;
                }

                if (username.isEmpty()) {
                    etUsername.setError("Username is required !!!");
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password is required !!!");
                    return;
                }


                // Creating a JSON object to put record in DB (resident credentials table)
                // Hash password before sending to server side
                String hashedPassword = Helper.hashPassword(password);

                // retrieve current date to insert in the data base as registration date
                //Date currentDate = Calendar.getInstance().getTime();

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = format1.format(cal.getTime());


                //Creating a JSON object to put record in DB (resident table)
                JSONObject userRecordObject = new JSONObject();
                try {
                    userRecordObject.put("First name", firstName);
                    userRecordObject.put("Last name", lastName);
                    userRecordObject.put("DoB", dob);
                    userRecordObject.put("Address", address);
                    userRecordObject.put("Postcode", postcode);
                    userRecordObject.put("Mobile", mobile);
                    userRecordObject.put("Email", email);
                    userRecordObject.put("Number of residents", numOfRes);
                    userRecordObject.put("Energy Provider", energyProvider);
                    userRecordObject.put("Username", username);
                    userRecordObject.put("Password", hashedPassword);
                    userRecordObject.put("Registration date", currentDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // call Async to add new user
                AddNewUser newUserRecord = new AddNewUser(userRecordObject.toString());
                newUserRecord.execute(new String []{userRecordObject.toString()});


                // end of Submit button case
                break;

            case R.id.bt_cancel:
                // when cancel button is clicked go to login page again
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
                break;

            case R.id.et_dob:
                // when date of birth text filed is clicked, pop up date picker


            default:
                break;
        }
    }

    public class AddNewUser extends AsyncTask<String, Void, String> {

        private static final String BASE_URI="http://10.0.2.2:8080/SmartER/webresources";

        private final String userRecordObject;

        public AddNewUser(String usrRecordObj) {
            userRecordObject = usrRecordObj;
        }

        @Override
        protected String doInBackground(String... param) {

            //initialise
            URL url = null;
            HttpURLConnection conn = null;
            final String methodPath = "/smarter.resident/addNewUser";
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
                j.put("jsonObject",userRecordObject);

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
        protected void onPostExecute(String status) {
            if (status.equals("Fail") || status.equals("")){

                //Context context = getApplicationContext();
                CharSequence text = "User Registration FAILED!";
                int duration = Toast.LENGTH_SHORT;

                Context mContext = getApplicationContext();
                Toast toast = Toast.makeText(mContext, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                Intent intent = getIntent();
                finish();
                startActivity(intent);

//            RegisterActivity registerActivity = getActivity();
//            // make activity falling into restart phase:
//            getInstrumentation().callActivityOnRestart(myActivity);
            } else if(status.equals("Success")){
                //Context context = getApplicationContext();
                CharSequence text = "User Registration SUCCESSFUL!";
                int duration = Toast.LENGTH_SHORT;

                Context mContext = getApplicationContext();
                Toast toast = Toast.makeText(mContext, text, duration);
                toast.show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        }
    }


}
