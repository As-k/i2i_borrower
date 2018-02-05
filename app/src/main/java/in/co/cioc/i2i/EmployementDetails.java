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
import android.widget.RadioButton;
import android.widget.Toast;

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
    EditText businessFormName , businessFormEstablishedYr, businessFormWebsite, businessFormTan, businessFormPan, businessFormEmail;
    EditText selfEmpWebsite, selfEmpExp, selfEmpEmail;



    Drawable successTick;
    private Pattern pattern;
    private Matcher matcher;

    LinearLayout selfEmpForm , businessForm , salariedForm;

    EditText address , pincode , city , state;
    EditText std, phone;

    public static String SALARIED = "salaried";
    public static String SELF_EMP = "selfEmp";
    public static String BUSINESS = "business";
    String empType, empDetailsSelfEmpType = "";
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_individual:
                if (checked)
                    empDetailsSelfEmpType = "individual";
                    break;
            case R.id.radio_firm:
                if (checked)
                    empDetailsSelfEmpType = "firm";
                    break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employement_details);

        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(3);

        selfEmpForm = findViewById(R.id.selfEmpForm);
        businessForm = findViewById(R.id.businessForm);
        salariedForm = findViewById(R.id.salariedForm);

        selfEmpForm.setVisibility(LinearLayout.GONE);
        businessForm.setVisibility(LinearLayout.GONE);
        salariedForm.setVisibility(LinearLayout.GONE);

        pattern = Pattern.compile(EMAIL_PATTERN);

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );

        salariedDesignation  = findViewById(R.id.designation);
        salariedEmail= findViewById(R.id.officialEmail);

        businessFormName = findViewById(R.id.businessFormName);
        businessFormEstablishedYr = findViewById(R.id.businessFormEstablishedYr);
        businessFormWebsite = findViewById(R.id.businessFormWebsite);
        businessFormTan = findViewById(R.id.businessFormTan);
        businessFormPan = findViewById(R.id.businessFormPan);
        businessFormEmail = findViewById(R.id.businessFormEmail);

        selfEmpWebsite = findViewById(R.id.selfEmpWebsite);
        selfEmpExp = findViewById(R.id.selfEmpExp);
        selfEmpEmail = findViewById(R.id.email);

        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);

        std = findViewById(R.id.stdCode);
        phone = findViewById(R.id.phoneNum);

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
            }
        });

        businessFormPan.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(businessFormPan);
                }else{
                    businessFormPan.setError("Invalid PAN");
                    View focusView = businessFormPan;
                    focusView.requestFocus();
                }
            }
        });

        businessFormTan.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(businessFormTan);
                }else{
                    businessFormTan.setError("Invalid TAN");
                    View focusView = businessFormTan;
                    focusView.requestFocus();
                }
            }
        });

        businessFormWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(businessFormWebsite);
                }else{
                    businessFormWebsite.setError("Invalid website address");
                    View focusView = businessFormWebsite;
                    focusView.requestFocus();
                }
            }
        });

        businessFormName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(businessFormName);
                }else{
                    businessFormName.setError("Invalid Name");
                    View focusView = businessFormName;
                    focusView.requestFocus();
                }
            }
        });

        businessFormEstablishedYr.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() == 4){
                    showSuccess(businessFormEstablishedYr);
                }else{
                    businessFormEstablishedYr.setError("Invalid Year value");
                    View focusView = businessFormEstablishedYr;
                    focusView.requestFocus();
                }
            }
        });

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

        businessFormEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(validateEmail(s.toString())){
                    showSuccess(businessFormEmail);
                }else{
                    businessFormEmail.setError("Invalid Email ID");
                    View focusView = businessFormEmail;
                    focusView.requestFocus();
                }
            }
        });

        selfEmpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(validateEmail(s.toString())){
                    showSuccess(selfEmpEmail);
                }else{
                    selfEmpEmail.setError("Invalid Email ID");
                    View focusView = selfEmpEmail;
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
                    typ =  "Self Employed";
                    if (typ.equals("Salaried Employee")){
                        empType = "Salaried";
                        selfEmpForm.setVisibility(LinearLayout.GONE);
                        businessForm.setVisibility(LinearLayout.GONE);
                        salariedForm.setVisibility(LinearLayout.VISIBLE);
                        salariedDesignation.requestFocus();

                        salariedDesignation.setText(c.getString("emp_sal_desg"));
                        salariedEmail.setText(c.getString("emp_official_email"));

                    }else if(typ.equals("Self Employed Professional")){
                        empType = "Self Employed";
                        selfEmpForm.setVisibility(LinearLayout.VISIBLE);
                        businessForm.setVisibility(LinearLayout.GONE);
                        salariedForm.setVisibility(LinearLayout.GONE);
                        selfEmpEmail.requestFocus();
                        selfEmpExp.setText(c.getString("emp_total_professional_exp"));
                        selfEmpEmail.setText(c.getString("emp_official_email"));
                    }else {
                        empType = "Business";
                        selfEmpForm.setVisibility(LinearLayout.GONE);
                        businessForm.setVisibility(LinearLayout.VISIBLE);
                        salariedForm.setVisibility(LinearLayout.GONE);
                        businessFormName.requestFocus();
                        businessFormName.setText(c.getString("emp_comp_name"));
                        businessFormEmail.setText(c.getString("emp_official_email"));
                        businessFormEstablishedYr.setText(c.getString("emp_bus_est_year"));
                        businessFormWebsite.setText(c.getString("emp_comp_website"));

                    }


                    businessFormPan.setText(c.getString("emp_comp_pan"));
                    businessFormTan.setText(c.getString("emp_comp_tan"));

                   if (c.getString("emp_self_firm_type").equals("Individual")){
                       RadioButton indivBtn = findViewById(R.id.radio_individual);
                       indivBtn.setChecked(true);
                   }else{
                       RadioButton firmBtn = findViewById(R.id.radio_firm);
                       firmBtn.setChecked(true);
                   }


//                    usr_id	25449
//                    emp_std_code	423
//                    emp_land_no	3423
//                    emp_type	Salaried Employee
//                    emp_sal_desg	fsdfsd
//                    emp_official_email	fsdgdfgfs@dfs.co
//                    emp_comp_name	fdfgbngg
//                    emp_bus_est_year	2015
//                    emp_comp_website	fsdfgdfgfd
//                    emp_comp_pan	DFGHB6543Q
//                    emp_comp_tan	fdfsdfsgfdg
//                    emp_self_firm_type	Individual
//                    emp_total_professional_exp	3
//                    officeAddress	{…}
//                    address	fdfsdhgdf
//                    pincode	201301
//                    city	Gautam Buddha Nagar
//                    state	UTTAR PRADESH

                    std.setText(c.getString("emp_std_code"));

                    phone.setText(c.getString("emp_land_no"));

                    JSONObject addrs = c.getJSONObject("officeAddress");

                    city.setText(addrs.getString("city"));
                    address.setText(addrs.getString("address"));
                    pincode.setText(addrs.getString("pincode"));
                    state.setText(addrs.getString("state"));




                }catch (JSONException e) {

                }
            }

        });


        Button next = findViewById(R.id.next_button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(false);
            }
        });

        Button previous = findViewById(R.id.previous_button);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });

    }

    public void previous(){
// /api/v1/borrowerRegistration/previous/?csrf_token=RaZYctICecN8gVbILUTvWdCmX&session_id=4kdWt2QIw2RMUEJI4hUs1sHU5

        StringEntity entity = null;

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        String url = "/api/v1/borrowerRegistration/previous/?csrf_token=" + csrf_token + "&session_id=" + session_id;

        client.post(getApplicationContext(), backend.BASE_URL + url ,entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Intent i = new Intent(getApplicationContext(), UserDetails.class);
                startActivity(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });



    }

    public void save(final Boolean stay){

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

        JSONObject empDetailsSelfEmp = new JSONObject();

        try{
            empDetailsSelfEmp.put("totalExp" , selfEmpExp.getText().toString());
            empDetailsSelfEmp.put("emailID" , selfEmpEmail.getText().toString());


            empDetailsSelfEmp.put("type" , empDetailsSelfEmpType);

            empDetailsSelfEmp.put("website" , selfEmpWebsite.getText().toString());
        }catch (JSONException e){

        }

//        empDetails	{…}
//        email	dsdadasds@sdfs.com
//        name	HCL INFOSYSTEMS LIMITED
//        pan	FGHJK7654T
//        stablishedyear	2017
//        tan	34322
//        website	fsdf

        JSONObject empDetailsBusiness = new JSONObject();

        try{
            empDetailsBusiness.put("email" , businessFormEmail.getText().toString());
            empDetailsBusiness.put("name" , businessFormName.getText().toString());
            empDetailsBusiness.put("pan" , businessFormPan.getText().toString());
            empDetailsBusiness.put("stablishedyear" , businessFormEstablishedYr.getText().toString());
            empDetailsBusiness.put("tan" , businessFormTan.getText().toString());
            empDetailsBusiness.put("website" , businessFormWebsite.getText().toString());
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

//        empDetails	{…}
//        emailID	dsdas@sdfs.com
//        totalExp	3
//        type	individual
//        website
//        empType	Self Employed

        try{
            jsonParams.put("empType" , emp);

            if (emp.equals("Self Employed")){
                if (empDetailsSelfEmpType.length() == 0){
                    Toast.makeText(this, "Please select the type", Toast.LENGTH_SHORT).show();
                    return;
                }
                jsonParams.put("empDetails" ,empDetailsSelfEmp);
            }else if(emp.equals("Business")){
                jsonParams.put("empDetails" ,empDetailsBusiness);
            }else{
                jsonParams.put("empDetails" ,empDetailsSalaried);
            }

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

        String url = "/api/v1/borrowerRegistration/employementDetails/?csrf_token=" + csrf_token + "&session_id=" + session_id;

        if (stay){
            url += "&next=0";
        }else{
            url += "&next=1";
        }

        client.post(getApplicationContext(), backend.BASE_URL + url  , entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (!stay){
                    Intent i = new Intent(getApplicationContext(), EducationalDetails.class);
                    startActivity(i);
                }else{
                    Toast.makeText(EmployementDetails.this, "Saved", Toast.LENGTH_SHORT).show();
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



    public boolean validateEmail(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

}
