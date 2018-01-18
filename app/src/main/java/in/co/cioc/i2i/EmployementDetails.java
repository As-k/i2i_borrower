package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class EmployementDetails extends AppCompatActivity {
    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;

    EditText salariedDesignation, salariedEmail;
    Drawable successTick;
    private Pattern pattern;
    private Matcher matcher;
    String empType;

    LinearLayout selfEmpForm , businessForm , salariedForm;

    EditText address , pincode , city , state;
    EditText std, phone;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employement_details);

        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(3);

//        selfEmpForm = findViewById(R.id.businessForm);
//        businessForm = findViewById(R.id.businessForm);
//        salariedForm = findViewById(R.id.businessForm);


        pattern = Pattern.compile(EMAIL_PATTERN);

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );

        salariedDesignation  = findViewById(R.id.designation);
        salariedEmail= findViewById(R.id.email);


        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);

        std = findViewById(R.id.stdCode);
        phone = findViewById(R.id.phoneNum);

        salariedEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(validateEmail(s.toString())){
                    showSuccess(salariedEmail);
                }else{
                    salariedEmail.setError("Invalid Email ID");
                    View focusView = salariedEmail;
                    focusView.requestFocus();
                }
            }
        });

        salariedDesignation.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(salariedDesignation);
                }else {
                    removeSuccess(salariedDesignation);
                }
            }
        });

        std.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(std);
                }else {
                    removeSuccess(std);
                }
            }
        });


        phone.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(phone);
                }else {
                    removeSuccess(phone);
                }
            }
        });
        address.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(address);
                }else {
                    removeSuccess(address);
                }
            }
        });




        pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    showSuccess(pincode);

                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                city.setText(response.getString("pin_city"));
                                state.setText(response.getString("pin_state"));
                                showSuccess(city);
                                showSuccess(state);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });


                }else{
                    pincode.setError("Invalid Pincode");
                    View focusView = pincode;
                    focusView.requestFocus();
                }
            }
        });


        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);
        client.get(backend.BASE_URL + "/api/v1/retriveDetails/employement/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try {
                    String typ = c.getString("emp_type");
                    if (typ == "Salaried Employee"){
                        empType = "Salaried";
                    }else if(typ == "Self Employed Professional"){
                        empType = "Self Employed";
                    }else {
                        empType = "Business";
                    }

                }catch (JSONException e) {

                }
            }

        });


        Button next = findViewById(R.id.next_button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });


    }

    public void save(){

        JSONObject jsonParams = new JSONObject();
        String emp = "Salaried";
        if (empType == "Business"){
            emp = "Business";
        }else if(emp == "Self Employed Professional"){
            emp = "Self Employed";
        }

        JSONObject empDetailsSalaried = new JSONObject();

        try{
            empDetailsSalaried.put("designation" , salariedDesignation.getText().toString());
            empDetailsSalaried.put("emailID" , salariedEmail.getText().toString());
        }catch (JSONException e){

        }

        JSONObject addressObj = new JSONObject();
        try{
            addressObj.put("address" , address.getText().toString());
            addressObj.put("pincode" , pincode.getText().toString());
            addressObj.put("state" , state.getText().toString());
            addressObj.put("city" , city.getText().toString());
        }catch (JSONException e){

        }



        JSONObject commonFields = new JSONObject();

        try{
            commonFields.put("officeAddress" , addressObj);
            commonFields.put("type" , emp);
            commonFields.put("stdCode" , std.getText().toString());
            commonFields.put("phNumber" , phone.getText().toString());
        }catch (JSONException e){

        }


        try{
            jsonParams.put("empType" , emp);
            jsonParams.put("empDetails" ,empDetailsSalaried);
            jsonParams.put("commonFields" , commonFields);
        }catch (JSONException e){

        }

        StringEntity entity = null;

        try{
            entity = new StringEntity(jsonParams.toString());
        }catch(Exception e){

        }

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/borrowerRegistration/employementDetails/?next=1&csrf_token=" + csrf_token + "&session_id=" + session_id , entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Intent i = new Intent(getApplicationContext(), EducationalDetails.class);
                startActivity(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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



    public boolean validateEmail(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

}
