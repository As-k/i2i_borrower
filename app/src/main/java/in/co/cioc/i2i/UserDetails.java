package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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


    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;

    Boolean married = false;
//    Boolean married = true;

    LinearLayout spouseNameForm, localAddressForm , noOfDepenLL, depen1LL, depen2LL, depen3LL;
    EditText fatherFName, fatherLName , fatherMName , spouseFName , spouseMName , spouseLName;
    EditText permAddress , permPincode , permCity , permState;
    EditText localAddress , localPincode , localCity , localState;

    Integer noOfDependents = 0;
    EditText depen1 , depen2 , depen3;
    CheckBox sameAsPermCB;
    TextView fnameErr, lnameErr, fnameSpouseErr, lnameSpouseErr, noOfDeptErr, noOfDept1Err, noOfDept2Err, noOfDept3Err,
            pAddressErr, pPincodeErr, pCityErr, pStateErr, currentCBErr, cAddressErr, cPincodeErr, cCityErr, cStateErr;


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
        noOfDepenLL = findViewById(R.id.noofDependentSpinner);
//        noOfDepenLL.setVisibility(View.GONE);

        localAddressForm = findViewById(R.id.localAddressForm);

        fatherFName = findViewById(R.id.firstName);
        fatherLName = findViewById(R.id.lastName);
        fatherMName = findViewById(R.id.middleName);
        spouseFName = findViewById(R.id.firstNameSpouse);
        spouseMName = findViewById(R.id.middleNameSpouse);
        spouseLName= findViewById(R.id.lastNameSpouse);

        findIdErrorText();


        fatherFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    fnameErr.setVisibility(View.VISIBLE);
                    fnameErr.setText("First name of father is required.");
                } else {
                    fnameErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    lnameErr.setVisibility(View.VISIBLE);
                    lnameErr.setText("Last name of father is required.");
                } else {
                    lnameErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    fnameSpouseErr.setVisibility(View.VISIBLE);
                    fnameSpouseErr.setText("First name of spouse is required.");
                } else {
                    fnameSpouseErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    lnameSpouseErr.setVisibility(View.VISIBLE);
                    lnameSpouseErr.setText("Last name of spouse is required.");
                } else {
                    lnameSpouseErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    cAddressErr.setVisibility(View.VISIBLE);
                    cAddressErr.setText("Please provide the address.");
                } else {
                    cAddressErr.setVisibility(View.GONE);
                    currentCBErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pAddressErr.setVisibility(View.VISIBLE);
                    pAddressErr.setText("Please provide the permanent address.");
                } else {
                    pAddressErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    noOfDept2Err.setVisibility(View.VISIBLE);
                    noOfDept2Err.setText("Please enter age.");
                } else {
                    noOfDept2Err.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    noOfDept3Err.setVisibility(View.VISIBLE);
                    noOfDept3Err.setText("Please enter age.");
                } else {
                    noOfDept3Err.setVisibility(View.GONE);
                }
            }
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
        depen2LL.setVisibility(LinearLayout.GONE);
        depen3LL.setVisibility(LinearLayout.GONE);

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
                noOfDeptErr.setVisibility(View.GONE);
                if (i == 0){
                    depen1LL.setVisibility(LinearLayout.GONE);
                    depen2LL.setVisibility(LinearLayout.GONE);
                    depen3LL.setVisibility(LinearLayout.GONE);
                    noOfDept1Err.setVisibility(View.GONE);
                    noOfDept2Err.setVisibility(View.GONE);
                    noOfDept3Err.setVisibility(View.GONE);
                }else if(i == 1){
                    depen1LL.setVisibility(LinearLayout.VISIBLE);
                    depen2LL.setVisibility(LinearLayout.GONE);
                    depen3LL.setVisibility(LinearLayout.GONE);
                    noOfDept1Err.setVisibility(View.GONE);
                    noOfDept2Err.setVisibility(View.GONE);
                    noOfDept3Err.setVisibility(View.GONE);
                }else if(i == 2){
                    depen1LL.setVisibility(LinearLayout.VISIBLE);
                    depen2LL.setVisibility(LinearLayout.VISIBLE);
                    depen3LL.setVisibility(LinearLayout.GONE);
                    noOfDept1Err.setVisibility(View.GONE);
                    noOfDept2Err.setVisibility(View.GONE);
                    noOfDept3Err.setVisibility(View.GONE);
                }else if(i == 3){
                    depen1LL.setVisibility(LinearLayout.VISIBLE);
                    depen2LL.setVisibility(LinearLayout.VISIBLE);
                    depen3LL.setVisibility(LinearLayout.VISIBLE);
                    noOfDept1Err.setVisibility(View.GONE);
                    noOfDept2Err.setVisibility(View.GONE);
                    noOfDept3Err.setVisibility(View.GONE);
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
                    currentCBErr.setVisibility(View.GONE);
                }else {
                    localAddressForm.setVisibility(LinearLayout.VISIBLE);
                }
            }
        });


        permPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pPincodeErr.setVisibility(View.VISIBLE);
                    pPincodeErr.setText("Please enter a valid pincode.");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    pPincodeErr.setVisibility(View.GONE);
                    showSuccess(permPincode);
                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            showSuccess(permPincode);
                            pPincodeErr.setVisibility(View.GONE);
                            try {
                                permCity.setText(response.getString("pin_city"));
                                permState.setText(response.getString("pin_state"));
                                showSuccess(permCity);
                                showSuccess(permState);
                                pCityErr.setVisibility(View.GONE);
                                pStateErr.setVisibility(View.GONE);
                            }catch (JSONException e){

                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            pPincodeErr.setVisibility(View.VISIBLE);
                            removeSuccess(localPincode);
                            pPincodeErr.setText("Invalid pincode");
                            permPincode.requestFocus();
                        }

                    });
                }else{
                    removeSuccess(localPincode);
                    pPincodeErr.setVisibility(View.VISIBLE);
                    pPincodeErr.setText("Invalid pincode");
                    permPincode.requestFocus();
                }
            }
        });

        localPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    cPincodeErr.setVisibility(View.VISIBLE);
                    cPincodeErr.setText("Please enter a valid pincode.");
                }
            }
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
                                cPincodeErr.setVisibility(View.GONE);
                                localCity.setText(response.getString("pin_city"));
                                localState.setText(response.getString("pin_state"));
                                showSuccess(localCity);
                                showSuccess(localState);
                                cCityErr.setVisibility(View.GONE);
                                cPincodeErr.setVisibility(View.GONE);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            cPincodeErr.setVisibility(View.VISIBLE);
                            cPincodeErr.setText("Invalid Pincode");
                            localPincode.requestFocus();
                        }

                    });


                }else{
                    removeSuccess(localPincode);
                    cPincodeErr.setVisibility(View.VISIBLE);
                    cPincodeErr.setText("Invalid Pincode");
                    localPincode.requestFocus();
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
                    JSONObject fatherName = c.getJSONObject("fathersName");
                    String ffname = fatherName.getString("firstName");
                    fatherFName.setText(ffname);
                    String fmname = fatherName.getString("middleName");
                    fatherMName.setText(fmname);
                    String flname = fatherName.getString("lastName");
                    fatherLName.setText(flname);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("printStackTrace",""+e);
                }


                try {
                    if (c.getString("married").equals("M")){
                        married = true;
                        spouseNameForm.setVisibility(LinearLayout.VISIBLE);
//                        noOfDepenLL.setVisibility(View.VISIBLE);
                        try {
                            JSONObject spouseName = c.getJSONObject("spouseName");
                            String sfname = spouseName.getString("firstName");
                            if (!sfname.equals("null"))
                                spouseFName.setText(sfname);
                            String smname = spouseName.getString("middleName");
                            if (!sfname.equals("null"))
                                spouseMName.setText(smname);
                            String slname = spouseName.getString("lastName");
                            if (!sfname.equals("null"))
                                spouseLName.setText(slname);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONObject noOfDependents = c.getJSONObject("noOfDependents");
                        JSONObject dependentsArr = c.getJSONObject("dependentsArr");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject permanentAddress = null;
                try {
                    permanentAddress = c.getJSONObject("permanentAddress");
                    String permanentAddressAddress = permanentAddress.getString("address");
                    String permanentAddressPincode = permanentAddress.getString("pincode");
                    String permanentAddressCity = permanentAddress.getString("city");
                    String permanentAddressState = permanentAddress.getString("state");

                    JSONObject localAddressObj = null;
                    localAddressObj = c.getJSONObject("localAddress");

                    String localAddressAddress = localAddressObj.getString("address");
                    String localAddressPincode = localAddressObj.getString("pincode");
                    String localAddressCity = localAddressObj.getString("city");
                    String localAddressState = localAddressObj.getString("state");

                    localAddress.setText(localAddressAddress);
                    if (localAddressPincode != "0") {
                        localPincode.setText(localAddressPincode);
                    }
                    localCity.setText(localAddressCity);
                    localState.setText(localAddressState);

                    permAddress.setText(permanentAddressAddress);
                    if (permanentAddressPincode.length() > 1) {
                        permPincode.setText(permanentAddressPincode);
                    }
                    permCity.setText(permanentAddressCity);
                    permState.setText(permanentAddressState);

                } catch (JSONException e) {

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

    public void findIdErrorText(){
        fnameErr = findViewById(R.id.fnameErrTxt);
        lnameErr = findViewById(R.id.lnameErrTxt);
        fnameSpouseErr = findViewById(R.id.fnameSpouseErrTxt);
        lnameSpouseErr = findViewById(R.id.lnameSpouseErrTxt);
        noOfDeptErr = findViewById(R.id.noofDependentErrTxt);
        noOfDept1Err = findViewById(R.id.dependent1ErrTxt);
        noOfDept2Err = findViewById(R.id.dependent2ErrTxt);
        noOfDept3Err = findViewById(R.id.dependent3ErrTxt);
        pAddressErr = findViewById(R.id.pAddressErrTxt);
        pPincodeErr = findViewById(R.id.pPincodeErrTxt);
        pCityErr = findViewById(R.id.pCityErrTxt);
        pStateErr = findViewById(R.id.pStateErrTxt);
        currentCBErr = findViewById(R.id.currentCBErrTxt);
        cAddressErr = findViewById(R.id.cAddressErrTxt);
        cPincodeErr = findViewById(R.id.cPincodeErrTxt);
        cCityErr = findViewById(R.id.cCityErrTxt);
        cStateErr = findViewById(R.id.cStateErrTxt);
    }

    public void save(final Boolean stay){
        String ffName = fatherFName.getText().toString().trim();
        String flName = fatherLName.getText().toString().trim();
        String sfName = spouseFName.getText().toString().trim();
        String slName = spouseLName.getText().toString().trim();
        String dependent1 = depen1.getText().toString().trim();
        String dependent2 = depen2.getText().toString().trim();
        String dependent3 = depen3.getText().toString().trim();
        String pAdd = permAddress.getText().toString().trim();
        String pPinCode = permPincode.getText().toString().trim();
        String pCity = permCity.getText().toString().trim();
        String pState = permState.getText().toString().trim();
        String cAdd = localAddress.getText().toString().trim();
        String cPinCode = localPincode.getText().toString().trim();
        String cCity = localCity.getText().toString().trim();
        String cState = localState.getText().toString().trim();

        if (ffName.isEmpty()){
            fnameErr.setVisibility(View.VISIBLE);
            fnameErr.setText("First name of father is required.");
        } else {
            fnameErr.setVisibility(View.GONE);
        }

        if (flName.isEmpty()){
            lnameErr.setVisibility(View.VISIBLE);
            lnameErr.setText("Last name of father is required.");
        } else {
            lnameErr.setVisibility(View.GONE);
        }

        if (sfName.isEmpty()){
            fnameSpouseErr.setVisibility(View.VISIBLE);
            fnameSpouseErr.setText("First name of spouse is required.");
        } else {
            fnameSpouseErr.setVisibility(View.GONE);
        }

        if (slName.isEmpty()){
            lnameSpouseErr.setVisibility(View.VISIBLE);
            lnameSpouseErr.setText("Last name of spouse is required.");
        } else {
            lnameSpouseErr.setVisibility(View.GONE);
        }

        if (!(dropdown.getSelectedItemPosition()==0)) {
            noOfDeptErr.setVisibility(View.GONE);
            if (dropdown.getSelectedItemPosition()==1) {
                if (dependent1.isEmpty()) {
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
            }else if (dropdown.getSelectedItemPosition()==2) {
                if (dependent1.isEmpty()) {
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
                if (dependent2.isEmpty()) {
                    noOfDept2Err.setVisibility(View.VISIBLE);
                    noOfDept2Err.setText("Please enter age.");
                } else {
                    noOfDept2Err.setVisibility(View.GONE);
                }
            } else if (dropdown.getSelectedItemPosition()==3) {
                if (dependent1.isEmpty()) {
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
                if (dependent2.isEmpty()) {
                    noOfDept2Err.setVisibility(View.VISIBLE);
                    noOfDept2Err.setText("Please enter age.");
                } else {
                    noOfDept2Err.setVisibility(View.GONE);
                }
                if (dependent3.isEmpty()) {
                    noOfDept3Err.setVisibility(View.VISIBLE);
                    noOfDept3Err.setText("Please enter age.");
                } else {
                    noOfDept3Err.setVisibility(View.GONE);
                }
            } else Log.e("dropdown","dependents");
//        } else {
//            noOfDeptErr.setVisibility(View.VISIBLE);
//            noOfDeptErr.setText("Please select dependents");
        }

        if (pAdd.isEmpty()){
            pAddressErr.setVisibility(View.VISIBLE);
            pAddressErr.setText("Please provide the permanent address.");
        } else {
            pAddressErr.setVisibility(View.GONE);
        }

        if (pPinCode.isEmpty()){
            pPincodeErr.setVisibility(View.VISIBLE);
            pPincodeErr.setText("Please enter a valid pincode.");
        } else {
            pPincodeErr.setVisibility(View.GONE);
            if (pCity.isEmpty()){
                pCityErr.setVisibility(View.VISIBLE);
                pCityErr.setText("Please enter city.");
            } else {
                pCityErr.setVisibility(View.GONE);
            }

            if (pState.isEmpty()){
                pStateErr.setVisibility(View.VISIBLE);
                pStateErr.setText("Please enter state.");
            } else {
                pStateErr.setVisibility(View.GONE);
            }
        }


        if (!sameAsPermCB.isChecked()) {
            currentCBErr.setVisibility(View.VISIBLE);
            currentCBErr.setText("Please enter current address");
            if (cAdd.isEmpty()) {
                cAddressErr.setVisibility(View.VISIBLE);
                cAddressErr.setText("Please provide the address.");
            } else {
                cAddressErr.setVisibility(View.GONE);
                currentCBErr.setVisibility(View.GONE);
            }
            if (cPinCode.isEmpty()) {
                cPincodeErr.setVisibility(View.VISIBLE);
                cPincodeErr.setText("Please enter a valid pincode.");
            } else {
                cPincodeErr.setVisibility(View.GONE);
                if (cCity.isEmpty()) {
                    cCityErr.setVisibility(View.VISIBLE);
                    cCityErr.setText("Please enter city.");
                } else {
                    cCityErr.setVisibility(View.GONE);
                }

                if (cState.isEmpty()) {
                    cStateErr.setVisibility(View.VISIBLE);
                    cStateErr.setText("Please enter state.");
                } else {
                    cStateErr.setVisibility(View.GONE);
                }
            }

        } else {
            currentCBErr.setVisibility(View.GONE);
        }



        if (fatherFName.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your father's first name.", Toast.LENGTH_SHORT).show();
            fatherFName.requestFocus();
            return;
        }
        if (fatherLName.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your father's last name.", Toast.LENGTH_SHORT).show();
            fatherLName.requestFocus();
            return;
        }

        if (married){
            if (spouseFName.length()==0){
                Toast.makeText(this, "Please provide your spouse's first name.", Toast.LENGTH_SHORT).show();
                spouseFName.requestFocus();
                return;
            }
            if (spouseLName.length()==0){
                Toast.makeText(this, "Please provide your spouse's last name.", Toast.LENGTH_SHORT).show();
                spouseLName.requestFocus();
                return;
            }
        }

        if (!(dropdown.getSelectedItemPosition()==0)) {
            noOfDeptErr.setVisibility(View.GONE);
            if (dropdown.getSelectedItemPosition() == 1) {
                if (dependent1.length() == 0) {
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                    depen1.requestFocus();
                    return;
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
            } else if (dropdown.getSelectedItemPosition() == 2) {
                if (dependent1.length() == 0) {
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                    depen1.requestFocus();
                    return;
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
                if (dependent2.length() == 0) {
                    noOfDept2Err.setVisibility(View.VISIBLE);
                    noOfDept2Err.setText("Please enter age.");
                    depen2.requestFocus();
                    return;
                } else {
                    noOfDept2Err.setVisibility(View.GONE);
                }
            } else if (dropdown.getSelectedItemPosition() == 3) {
                if (dependent1.length() == 0) {
                    noOfDept1Err.setVisibility(View.VISIBLE);
                    noOfDept1Err.setText("Please enter age.");
                    depen1.requestFocus();
                    return;
                } else {
                    noOfDept1Err.setVisibility(View.GONE);
                }
                if (dependent2.length() == 0) {
                    noOfDept2Err.setVisibility(View.VISIBLE);
                    noOfDept2Err.setText("Please enter age.");
                    depen2.requestFocus();
                    return;
                } else {
                    noOfDept2Err.setVisibility(View.GONE);
                }
                if (dependent3.length() == 0) {
                    noOfDept3Err.setVisibility(View.VISIBLE);
                    noOfDept3Err.setText("Please enter age.");
                    depen3.requestFocus();
                    return;
                } else {
                    noOfDept3Err.setVisibility(View.GONE);
                }
            }
        }

        if (permAddress.getText().toString().length()==0){
            Toast.makeText(this, "Please provide the permanent address.", Toast.LENGTH_SHORT).show();
            permAddress.requestFocus();
            return;
        }
        if (permPincode.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your permanent pincode.", Toast.LENGTH_SHORT).show();
            permPincode.requestFocus();
            return;
        }

        if (permCity.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your permanent city.", Toast.LENGTH_SHORT).show();
            permCity.requestFocus();
            return;
        }

        if (!sameAsPermCB.isChecked() && localAddress.getText().toString().length()==0){
            Toast.makeText(this, "Please provide your local address.", Toast.LENGTH_SHORT).show();
            localAddress.requestFocus();
            return;
        }



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
            save(true);
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
                } else {
                    Toast.makeText(UserDetails.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        fatherFName.setText(fatherFName.getText());
//        fatherLName.setText(fatherLName.getText());
//        if (married) {
//            spouseFName.setText(spouseFName.getText());
//            spouseLName.setText(spouseLName.getText());
//            if (dropdown.getSelectedItemPosition() == 1) {
//                depen1.setText(depen1.getText());
//            } else {
//                if (dropdown.getSelectedItemPosition() == 2) {
//                    depen1.setText(depen1.getText());
//                    depen2.setText(depen2.getText());
//                } else {
//                    if (dropdown.getSelectedItemPosition() == 3) {
//                        depen1.setText(depen1.getText());
//                        depen2.setText(depen2.getText());
//                        depen3.setText(depen3.getText());
//                    }
//                }
//            }
//        }
//        permAddress.setText(permAddress.getText());
//        permCity.setText(permCity.getText());
//        permPincode.setText(permPincode.getText());
//        permState.setText(permState.getText());
//        if (!sameAsPermCB.isChecked()){
//            localAddress.setText(localAddress.getText());
//            localPincode.setText(localPincode.getText());
//            localCity.setText(localCity.getText());
//            localState.setText(localState.getText());
//        }
//    }

    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
