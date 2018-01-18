package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class RegistrationOTP extends AppCompatActivity {

    private String email = "";
    private String mobile = "";

    private CheckBox tncCB;
    private CheckBox personalCB;

    private EditText mobileOTPEdit;
    private EditText emailOTPEdit;
    private Drawable successTick;
    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_otp);

        Intent intentExtra = getIntent();

        backend = new Backend();

        Bundle extrasBundle = intentExtra.getExtras();
        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );

        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        try{
            email = extrasBundle.getString("email");
            mobile = extrasBundle.getString("mobile");
        }catch (RuntimeException e){
            email = "fdsfds@dfsd.com";
            mobile = "3432412645";
        }


        tncCB = findViewById(R.id.tncCheckbox);
        personalCB = findViewById(R.id.personalInfoCheckBox);

        mobileOTPEdit = findViewById(R.id.mobileOTP);
        emailOTPEdit = findViewById(R.id.emailOTP);

        RequestParams requestParams = new RequestParams();


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
                    String url = backend.BASE_URL + "/api/v1/checkMobileOTP/" + mobile +'/' + s.toString() + "/?partner=false";
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
                    String url = backend.BASE_URL + "/api/v1/checkEmailOTP/" + email+"/"+ s.toString() +"/?partner=false";
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

                RequestParams params = new RequestParams();
                params.put("mobileOTP", mobileOTPEdit.getText().toString());
                params.put("emailOTP", emailOTPEdit.getText().toString());
                params.put("emailID", email);

                client.post(backend.BASE_URL + "/api/v1/borrowerRegistration/submitBasic/", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);

                        try {
                            String session_id = c.getString("session_id");
                            String csrf_token = c.getString("csrf_token");


                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("session_id", session_id);
                            editor.putString("csrf_token", csrf_token);
                            editor.commit();
                        }catch(JSONException e){

                        }


                        Intent i = new Intent(getApplicationContext(), RegistrationCheckEligibility.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject c){

                    }

                });

            }
        });


    }

    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }

}
