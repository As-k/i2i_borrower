package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserDetails extends AppCompatActivity {


    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;

    Boolean married = false;

    LinearLayout spouseNameForm, localAddressForm , depen1LL, depen2LL, depen3LL;
    EditText fatherFName, fatherLName , fatherMName , spouseFName , spouseMName , spouseLName;
    EditText permAddress , permPincode , permCity , permState;
    EditText localAddress , localPincode , localCity , localState;

    Integer noOfDependents = 0;
    EditText depen1 , depen2 , depen3;
    CheckBox sameAsPermCB;

    Spinner dropdown;
    Drawable successTick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(2);

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );

        spouseNameForm = findViewById(R.id.spouseNameForm);
        spouseNameForm.setVisibility(LinearLayout.GONE);

        localAddressForm = findViewById(R.id.localAddressForm);

        fatherFName = findViewById(R.id.firstName);
        fatherLName = findViewById(R.id.lastName);
        fatherMName = findViewById(R.id.middleName);
        spouseFName = findViewById(R.id.firstNameSpouse);
        spouseMName = findViewById(R.id.middleNameSpouse);
        spouseLName= findViewById(R.id.lastNameSpouse);


        fatherFName.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(fatherFName);
                }else {
                    removeSuccess(fatherFName);
                }
            }
        });

        fatherLName.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(fatherLName);
                }else {
                    removeSuccess(fatherLName);
                }
            }
        });

        fatherMName.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(fatherMName);
                }else {
                    removeSuccess(fatherMName);
                }
            }
        });

        spouseFName.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(spouseFName);
                }else {
                    removeSuccess(spouseFName);
                }
            }
        });

        spouseMName.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(spouseMName);
                }else {
                    removeSuccess(spouseMName);
                }
            }
        });

        spouseLName.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(spouseLName);
                }else {
                    removeSuccess(spouseLName);
                }
            }
        });


        permAddress = findViewById(R.id.permanentAddress);
        permPincode = findViewById(R.id.permanentPincode);
        permCity = findViewById(R.id.permanentCity);
        permState= findViewById(R.id.permanentState);

        localAddress= findViewById(R.id.currentAddress);
        localPincode = findViewById(R.id.currentPincode);
        localCity = findViewById(R.id.currentCity);
        localState= findViewById(R.id.currentState);

        depen1 = findViewById(R.id.dependent1);
        depen2 = findViewById(R.id.dependent2);
        depen3= findViewById(R.id.dependent3);



        localAddress.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(localAddress);
                }else {
                    removeSuccess(localAddress);
                }
            }
        });

        permAddress.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(permAddress);
                }else {
                    removeSuccess(permAddress);
                }
            }
        });

        depen1.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(depen1);
                }else {
                    removeSuccess(depen1);
                }
            }
        });
        depen2.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(depen2);
                }else {
                    removeSuccess(depen2);
                }
            }
        });
        depen3.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(depen3);
                }else {
                    removeSuccess(depen3);
                }
            }
        });


        depen1LL = findViewById(R.id.dependent1Layout);
        depen2LL = findViewById(R.id.dependent2Layout);
        depen3LL = findViewById(R.id.dependent3Layout);

        depen1LL.setVisibility(LinearLayout.GONE);
        depen1LL.setVisibility(LinearLayout.GONE);
        depen1LL.setVisibility(LinearLayout.GONE);

        sameAsPermCB = findViewById(R.id.sameAsPerm);

        dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"0" , "1" , "2" , "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);


        Button next = findViewById(R.id.next_button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(false);
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    depen1LL.setVisibility(LinearLayout.GONE);
                    depen2LL.setVisibility(LinearLayout.GONE);
                    depen3LL.setVisibility(LinearLayout.GONE);
                }else if(i == 1){
                    depen1LL.setVisibility(LinearLayout.VISIBLE);
                    depen2LL.setVisibility(LinearLayout.GONE);
                    depen3LL.setVisibility(LinearLayout.GONE);
                }else if(i == 2){
                    depen1LL.setVisibility(LinearLayout.VISIBLE);
                    depen2LL.setVisibility(LinearLayout.VISIBLE);
                    depen3LL.setVisibility(LinearLayout.GONE);
                }else if(i == 3){
                    depen1LL.setVisibility(LinearLayout.VISIBLE);
                    depen2LL.setVisibility(LinearLayout.VISIBLE);
                    depen3LL.setVisibility(LinearLayout.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        sameAsPermCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    localAddressForm.setVisibility(LinearLayout.GONE);
                }else {
                    localAddressForm.setVisibility(LinearLayout.VISIBLE);
                }
            }
        });


        permPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    showSuccess(permPincode);

                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                permCity.setText(response.getString("pin_city"));
                                permState.setText(response.getString("pin_state"));
                                showSuccess(permCity);
                                showSuccess(permState);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });


                }else{
                    permPincode.setError("Invalid Pincode");
                    View focusView = permPincode;
                    focusView.requestFocus();
                }
            }
        });

        localPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    showSuccess(localPincode);

                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                localCity.setText(response.getString("pin_city"));
                                localState.setText(response.getString("pin_state"));
                                showSuccess(localCity);
                                showSuccess(localState);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });


                }else{
                    localPincode.setError("Invalid Pincode");
                    View focusView = localPincode;
                    focusView.requestFocus();
                }
            }
        });



        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        client.get(backend.BASE_URL + "/api/v1/retriveDetails/personal/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try {
                    if (c.getString("married") == "M"){
                        married = true;
                        spouseNameForm.setVisibility(LinearLayout.VISIBLE);
                    }

                    JSONObject permanentAddress = c.getJSONObject("permanentAddress");

                    String permanentAddressAddress = permanentAddress.getString("address");
                    String permanentAddressPincode = permanentAddress.getString("pincode");
                    String permanentAddressCity = permanentAddress.getString("city");
                    String permanentAddressState = permanentAddress.getString("state");

                    JSONObject localAddressObj = c.getJSONObject("localAddress");

                    String localAddressAddress = localAddressObj.getString("address");
                    String localAddressPincode = localAddressObj.getString("pincode");
                    String localAddressCity = localAddressObj.getString("city");
                    String localAddressState = localAddressObj.getString("state");

                    localAddress.setText(localAddressAddress);
                    if (localAddressPincode != "0" ){
                        localPincode.setText(localAddressPincode);
                    }
                    localCity.setText(localAddressCity);
                    localState.setText(localAddressState);

                    permAddress.setText(permanentAddressAddress);
                    if (permanentAddressPincode.length() >1){
                        permPincode.setText(permanentAddressPincode);
                    }
                    permCity.setText(permanentAddressCity);
                    permState.setText(permanentAddressState);

                }catch (JSONException e) {

                }
            }

        });

        Button save_btn = findViewById(R.id.save_button);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
            }
        });

        ///api/v1/retriveDetails/personal/?csrf_token=XzaphWgrzwWRKtBjLkRneKYaq&session_id=gs7Ix9YWiMtmdNsbeFBYYhJIi


//        {"married":"S","localAddress":{"address":"","pincode":201301,"city":"Gautam Buddha Nagar","state":"UTTAR PRADESH\r"},"ud_curnt_addrs_same_as_prmanent":null,"permanentAddress":{"address":"","pincode":0,"city":"","state":""},"fathersName":{"firstName":"","middleName":"","lastName":""},"spouseName":{"firstName":null,"middleName":null,"lastName":null},"dependentsArr":"{}"}


    }

    /*

    {
        "noOfDependents": 0,
            "dependentsArr": [],
        "permAddress": {
        "address": "tret",
                "pincode": "201301",
                "city": "Gautam Buddha Nagar",
                "state": "UTTAR PRADESH\r"
    },
        "localAddress": {
        "address": "tret",
                "pincode": "201301",
                "city": "Gautam Buddha Nagar",
                "state": "UTTAR PRADESH\r"
    },
        "permAddressSameAsLocal": true,
            "fathersName": {
        "firstName": "rter",
                "middleName": "ter",
                "lastName": "tre"
    },
        "spouseName": {
        "firstName": "",
                "middleName": "",
                "lastName": ""
    },
        "email": "",
            "key": "",
            "married": "S"
    }
    */
    public void save(final Boolean stay){
        String dependentsArr = "[]";

        JSONObject permAdd = new JSONObject();

        try{
            permAdd.put("address" ,permAddress.getText().toString());
            permAdd.put("pincode" ,permPincode.getText().toString());
            permAdd.put("city" ,permCity.getText().toString());
            permAdd.put("state" ,permState.getText().toString());
        }catch (JSONException e){

        }

        JSONObject localAdd = new JSONObject();

        try{
            localAdd.put("address" ,localAddress.getText().toString());
            localAdd.put("pincode" ,localPincode.getText().toString());
            localAdd.put("city" ,localCity.getText().toString());
            localAdd.put("state" ,localState.getText().toString());
        }catch (JSONException e){

        }


        JSONObject fatherName = new JSONObject();
        try{
            fatherName.put("firstName" ,fatherFName.getText().toString());
            fatherName.put("middleName" ,fatherMName.getText().toString());
            fatherName.put("lastName" ,fatherLName.getText().toString());
        }catch (JSONException e){

        }

        if (fatherFName.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your father's name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fatherLName.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your father's last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (permAddress.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your permanent address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (permPincode.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your permanent pincode", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!sameAsPermCB.isChecked() && localAddress.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your local address", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject spouseName = new JSONObject();
        try{
            spouseName.put("firstName" ,spouseFName.getText().toString());
            spouseName.put("middleName" ,spouseMName.getText().toString());
            spouseName.put("lastName" ,spouseLName.getText().toString());
        }catch (JSONException e){

        }

        JSONObject jsonParams = new JSONObject();

        try{
            jsonParams.put("noOfDependents" ,dropdown.getSelectedItemPosition());
            jsonParams.put("dependentsArr" ,"[]");
            jsonParams.put("permAddress" , permAdd);
            jsonParams.put("localAddress" ,localAdd);
            jsonParams.put("permAddressSameAsLocal" ,sameAsPermCB.isChecked());
            jsonParams.put("fathersName" ,fatherName);
            jsonParams.put("spouseName" ,spouseName);
        }catch (JSONException e){

        }

        StringEntity entity = null;

        try{
            entity = new StringEntity(jsonParams.toString());
        }catch(Exception e){

        }

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        String url = "/api/v1/borrowerRegistration/userDetails/?csrf_token=" + csrf_token + "&session_id=" + session_id;

        if (!stay){
            url += "&next=1";
        }else{
            url += "&next=0";
        }

        client.post(getApplicationContext(), backend.BASE_URL + url, entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (!stay){
                    Intent i = new Intent(getApplicationContext(), EmployementDetails.class);
                    startActivity(i);
                }
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


}
