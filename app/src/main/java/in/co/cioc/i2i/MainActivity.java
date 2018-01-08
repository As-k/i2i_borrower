package in.co.cioc.i2i;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText mPasswordView;
    EditText RePasswordView;
    EditText fName;
    EditText mName;
    EditText lName;
    EditText adhar;
    EditText pan;
    EditText email;
    EditText mobile;

    RadioButton male;
    RadioButton feMale;
    LinearLayout btnLayout;
    Drawable successTick;

    Registration r;
    Backend backend;

    RequestParams requestParams = new RequestParams();;
    private static AsyncHttpClient client = new AsyncHttpClient();
    String jsonResponse;

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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "DocumentsActivity"});
        mStepView.setSteps(steps);

        mStepView.selectedStep(1);

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

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() > 7){
                    showSuccess(mPasswordView);
                }else{
                    mPasswordView.setError("Password too short");
                    View focusView = mPasswordView;
                    focusView.requestFocus();
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
                }else{
                    RePasswordView.setError("Password does not match");
                    View focusView = RePasswordView;
                    focusView.requestFocus();
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

                params.put("gender", "M");
                params.put("aadhar", adhar.getText().toString());
                params.put("panNumber", adhar.getText().toString());
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
                            Bundle bundle = new Bundle();
                            bundle.putString("email" , email.getText().toString());
                            bundle.putString("mobile" , mobile.getText().toString());

                            Intent i = new Intent(getApplicationContext(), RegistrationOTP.class);
                            i.putExtras(bundle);
                            startActivity(i);
//                            register_button.setText("Resend");
                        }

                    }

                });


            }
        });


        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        btnLayout = (LinearLayout) findViewById(R.id.linearLayout);

                        if (isOpen){
                            btnLayout.setVisibility(LinearLayout.GONE);
                        }else {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            btnLayout.setVisibility(LinearLayout.VISIBLE);
                                        }
                                    },
                                    300);

                        }

                    }
                });


    }

    public boolean validateEmail(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

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

                    // Pirates are the best
                    break;
            case R.id.radio_female:
                if (checked)
                    // Ninjas rule
                    break;
        }
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
