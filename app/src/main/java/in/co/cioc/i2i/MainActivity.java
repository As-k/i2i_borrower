package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class MainActivity extends AppCompatActivity {
    EditText mPasswordView;
    EditText RePasswordView;
    EditText fName;
    EditText mName;
    EditText lName;
    EditText adhar;
    EditText pan;
    EditText email;
    EditText mobile;

    private EditText mobileOTPEdit;
    private EditText emailOTPEdit;
    RadioButton male;
    SharedPreferences sharedPreferences;
    RadioButton feMale;
    LinearLayout btnLayout , otpMasterLayout;
    Drawable successTick;

    Registration r;
    Backend backend;
    private CheckBox tncCB;
    private CheckBox personalCB;
    TextView otpView;


    TextView passwordErr, password2Err;

    RequestParams requestParams = new RequestParams();;
    private static AsyncHttpClient client = new AsyncHttpClient();
    String jsonResponse , mobileOtp , emailOtp;

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backend = new Backend();
        otpView = findViewById(R.id.otpView);

        Toast.makeText(this, backend.BASE_URL, Toast.LENGTH_LONG).show();

//        StepView mStepView = (StepView) findViewById(R.id.step_view);
//        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
//        mStepView.setSteps(steps);
//
//        mStepView.selectedStep(1);
        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        otpMasterLayout = findViewById(R.id.otpMasterLayout);
        otpMasterLayout.setVisibility(LinearLayout.GONE);

        fName = findViewById(R.id.firstName);
        mName = findViewById(R.id.middleName);
        lName = findViewById(R.id.lastName);
        adhar = findViewById(R.id.aadhar);
        pan = findViewById(R.id.pan);
        pan.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);

        mPasswordView = findViewById(R.id.password);
        RePasswordView = findViewById(R.id.rePassword);

        male = findViewById(R.id.radio_male);
        feMale = findViewById(R.id.radio_female);

        View focusView = null;

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );


        pattern = Pattern.compile(EMAIL_PATTERN);

        fName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(fName);
                }else {
                    removeSuccess(fName);
                }
            }
        });
        lName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(lName);
                }else {
                    removeSuccess(lName);
                }
            }
        });

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(mName);
                }else {
                    removeSuccess(mName);
                }
            }
        });

        pan.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 10){
                    pan.setText(s.toString().substring(0,10));
                }

                if(s.length() != 0 && s.length() != 10){
                    // show error message on pan
                    pan.setError("Invalid PAN card");
                    View focusView = pan;
                    focusView.requestFocus();
                }else if(s.length() == 0){
                    pan.setError("PAN card is required");
                    View focusView = pan;
                    focusView.requestFocus();
                }else {
                    // check regex and backend validity of the pan
                    System.out.println("Checking PAN");
                    if(Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$").matcher(s.toString()).find()){


                        client.get(backend.BASE_URL + "/api/v1/checkPan/" + s.toString() +"/i2i_users||usr_pan", requestParams, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                jsonResponse = response.toString();
                                System.out.println(jsonResponse);
                                Integer count = -1;
                                try {
                                    count = response.getInt("count");
                                }catch (JSONException err){
                                }
                                if (count ==0){
                                    showSuccess(pan);
                                }else if (count>0){
                                    pan.setError("PAN card already exist");
                                    View focusView = pan;
                                    focusView.requestFocus();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                            @Override
                            public void onFinish() {
                                System.out.println("finished 001");

                            }
                        });



                    }else{
                        pan.setError("Invalid PAN card");
                        View focusView = pan;
                        focusView.requestFocus();
                    }
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String EMAIL_PATTERN =
                        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                if(validateEmail(s.toString())){


                    client.get(backend.BASE_URL + "/api/v1/checkEmail/" + s.toString() +"/i2i_users||usr_email", requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            jsonResponse = response.toString();
                            System.out.println(jsonResponse);
                            Integer count = -1;
                            try {
                                count = response.getInt("count");
                            }catch (JSONException err){
                            }
                            if (count ==0){
                                showSuccess(email);
                            }else if (count>0){
                                email.setError("Email ID already exist");
                                View focusView = email;
                                focusView.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });



                }else{
                    email.setError("Invalid Email ID");
                    View focusView = email;
                    focusView.requestFocus();
                }
            }
        });

        adhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 12){


                    client.get(backend.BASE_URL + "/api/v1/checkAadhar/" + s.toString() +"/?partner=false", requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            Integer count = -1;
                            try {
                                count = response.getInt("count");
                            }catch (JSONException err){
                            }
                            if (count ==0){
                                showSuccess(adhar);
                            }else if (count>0){
                                adhar.setError("Aadhar Number already exist");
                                View focusView = adhar;
                                focusView.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });



                }else{
                    adhar.setError("Invalid Aadhar Number");
                    View focusView = adhar;
                    focusView.requestFocus();
                }
            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 10){


                    client.get(backend.BASE_URL + "/api/v1/checkPhoneNumber/" + s.toString() +"/?partner=false", requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            jsonResponse = response.toString();
                            System.out.println(jsonResponse);
                            Integer count = -1;
                            try {
                                count = response.getInt("count");
                            }catch (JSONException err){
                            }
                            if (count ==0){
                                showSuccess(mobile);
                            }else if (count>0){
                                mobile.setError("Mobile Number already exist");
                                View focusView = mobile;
                                focusView.requestFocus();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });



                }else{
                    mobile.setError("Invalid Mobile Number");
                    View focusView = mobile;
                    focusView.requestFocus();
                }
            }
        });

        passwordErr = findViewById(R.id.passwordErrTxt);
        password2Err = findViewById(R.id.password2ErrTxt);

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                updatePasswordStrengthView(s.toString());

                if(PasswordStrength.calculateStrength(s.toString()).getText(getApplicationContext()).equals("Strong")){
                    showSuccess(mPasswordView);
                    passwordErr.setText("");

                }else{
                    passwordErr.setText("Invalid Password");
                }
            }
        });

        RePasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = mPasswordView.getText().toString();

                if(s.toString().equals(pass)){
                    showSuccess(RePasswordView);
                    password2Err.setText("");
                }else{
                    password2Err.setText("Password does not matches");
                }
            }
        });


        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();

                params.put("firstName", fName.getText().toString());
                params.put("middleName", mName.getText().toString());
                params.put("lastName", lName.getText().toString());

                if (male.isChecked()){
                    params.put("gender", "M");
                }else if (feMale.isChecked()){
                    params.put("gender", "F");
                }else {
                    return;
                }

                params.put("aadhar", adhar.getText().toString());
                params.put("panNumber", pan.getText().toString());
                params.put("emailID", email.getText().toString());
                params.put("password", mPasswordView.getText().toString());
                params.put("password2", RePasswordView.getText().toString());
                params.put("mobileNum", mobile.getText().toString());
                params.put("mobileOTP", "");
                params.put("emailOTP", "");
                params.put("type", "borrower");



                client.post(backend.BASE_URL + "/api/v1/borrowerRegistration/basic/?institution=false", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);
                        System.out.println(c.toString());
                        Boolean otpSent = false;
                        try {
                            otpSent = c.getBoolean("otpSent");
                        }catch (JSONException err){
                        }

                        if (otpSent){

                            try {
                                mobileOtp = Integer.toString(c.getInt("mobileOTP"));
                                emailOtp= Integer.toString(c.getInt("emailOTP"));

                                otpView.setText(mobileOtp + ":" + emailOtp );

                            }catch (JSONException e){

                            }

                            otpMasterLayout.setVisibility(LinearLayout.VISIBLE);

//                            Intent i = new Intent(getApplicationContext(), RegistrationOTP.class);
//                            i.putExtras(bundle);
//                            startActivity(i);
//                            register_button.setText("Resend");
                        }

                    }

                });


            }
        });

        // related to OTP

        tncCB = findViewById(R.id.tncCheckbox);
        personalCB = findViewById(R.id.personalInfoCheckBox);


        RequestParams requestParams = new RequestParams();

        mobileOTPEdit = findViewById(R.id.mobileOTP);
        emailOTPEdit = findViewById(R.id.emailOTP);

        mobileOTPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,  int before, int count) {
                if(s.length() == 6){
                    String url = backend.BASE_URL + "/api/v1/checkMobileOTP/" + mobile.getText().toString() +'/' + s.toString() + "/?partner=false";
                    client.get(url, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                            super.onSuccess(statusCode, headers, c);

                            try {
                                if (c.getBoolean("valid")){
                                    showSuccess(mobileOTPEdit);
                                }
                            }catch(JSONException e){
                                removeSuccess(mobileOTPEdit);
                                mobileOTPEdit.setError("Mobile OTP not correct");
                                View focusView = mobileOTPEdit;
                                focusView.requestFocus();
                            }

                        }

                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject obj){
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    });
                }else {
                    removeSuccess(mobileOTPEdit);
                    mobileOTPEdit.setError("Mobile OTP not correct");
                    View focusView = mobileOTPEdit;
                    focusView.requestFocus();
                }
            }
        });

        emailOTPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6){
                    String url = backend.BASE_URL + "/api/v1/checkEmailOTP/" + email.getText().toString()+"/"+ s.toString() +"/?partner=false";
                    client.get(url, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                            super.onSuccess(statusCode, headers, c);
                            try {
                                if (c.getBoolean("valid")){
                                    showSuccess(emailOTPEdit);
                                }
                            }catch(JSONException e){
                                removeSuccess(emailOTPEdit);
                                emailOTPEdit.setError("Email OTP not correct");
                                View focusView = emailOTPEdit;
                                focusView.requestFocus();
                            }

                        }

                    });
                }else {
                    removeSuccess(emailOTPEdit);
                    emailOTPEdit.setError("Email OTP not correct");
                    View focusView = emailOTPEdit;
                    focusView.requestFocus();
                }
            }
        });

        Button submit_btn = findViewById(R.id.submit_button);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mobileOTP	884214
//                emailOTP	306787
//                emailID	dasdas@sdsf.com


                if (!tncCB.isChecked()){
                    Toast.makeText(MainActivity.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!personalCB.isChecked()){
                    Toast.makeText(MainActivity.this, "Please agree to the privacy policy", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject userObj = new JSONObject();

                try{
                    userObj.put("firstName", fName.getText().toString());
                    userObj.put("middleName", mName.getText().toString());
                    userObj.put("lastName", lName.getText().toString());

                    if (male.isChecked()){
                        userObj.put("gender", "M");
                    }else if (feMale.isChecked()){
                        userObj.put("gender", "F");
                    }else {
                        return;
                    }

                    userObj.put("aadhar", adhar.getText().toString());
                    userObj.put("panNumber", pan.getText().toString());
                }catch (JSONException e){

                }

                JSONObject jsonParams = new JSONObject();
                try{
                    jsonParams.put("mobileOTP", mobileOTPEdit.getText().toString());
                    jsonParams.put("emailOTP", emailOTPEdit.getText().toString());
                    jsonParams.put("emailID", email.getText().toString());
                    jsonParams.put("user", userObj);
                }catch (JSONException e){

                }

                StringEntity entity = null;

                try{
                    entity = new StringEntity(jsonParams.toString());
                }catch(Exception e){

                }


                client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/borrowerRegistration/submitBasic/", entity , "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);

                        try {
                            String session_id = c.getString("session_id");
                            String csrf_token = c.getString("csrf_token");


                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("session_id", session_id);
                            editor.putString("csrf_token", csrf_token);
                            editor.putString("email", email.getText().toString());
                            editor.putString("mobile", mobile.getText().toString());
                            editor.commit();
                        }catch(JSONException e){

                        }


                        Intent i = new Intent(getApplicationContext(), RegistrationCheckEligibility.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject c){
                        System.out.println("Console is: " + "Faild OTP verification");
                    }

                });

            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                //Log.d("Text",messageText);
                String otp = parseCode(messageText);
                mobileOTPEdit.setText(otp);

            }
        });


//        KeyboardVisibilityEvent.setEventListener(
//                this,
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        // some code depending on keyboard visiblity status
//                        btnLayout = (LinearLayout) findViewById(R.id.linearLayout);
//
//                        if (isOpen){
//                            btnLayout.setVisibility(LinearLayout.GONE);
//                        }else {
//                            new android.os.Handler().postDelayed(
//                                    new Runnable() {
//                                        public void run() {
//                                            btnLayout.setVisibility(LinearLayout.VISIBLE);
//                                        }
//                                    },
//                                    300);
//
//                        }
//
//                    }
//                });


    }


    private void updatePasswordStrengthView(String password) {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView strengthView = (TextView) findViewById(R.id.password_strength);
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("");
            progressBar.setProgress(0);
            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }

    public boolean validateEmail(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }


    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }



    public void sendOTP(){
        System.out.println("will submit the text");
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    break;
            case R.id.radio_female:
                if (checked)
                    break;
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

    public static String getMonthInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return (dateFormat.format(date));
    }

    public static String getYearInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);
    }

    public static String getdayInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

}
