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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
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
    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;

    EditText salariedDesignation, salariedEmail;
    EditText businessFormName , businessFormWebsite, businessFormTan, businessFormPan, businessFormEmail;
    EditText selfEmpWebsite, selfEmpEmail;

    TextView designationErr, selfEmailErr, businessNameErr, businessEmailErr, radioIFErr, addressErr, pincodeErr, cityErr, stateErr;
    RadioButton individual, firm;

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
                if (checked) {
                    empDetailsSelfEmpType = "individual";
                    radioIFErr.setVisibility(View.GONE);
                    break;
                }
            case R.id.radio_firm:
                if (checked) {
                    empDetailsSelfEmpType = "firm";
                    radioIFErr.setVisibility(View.GONE);
                    break;
                }
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
//        businessFormEstablishedYr = findViewById(R.id.businessFormEstablishedYr);
        businessFormWebsite = findViewById(R.id.businessFormWebsite);
        businessFormTan = findViewById(R.id.businessFormTan);
        businessFormPan = findViewById(R.id.businessFormPan);
        businessFormEmail = findViewById(R.id.businessFormEmail);

        selfEmpWebsite = findViewById(R.id.selfEmpWebsite);
//        selfEmpExp = findViewById(R.id.selfEmpExp);
        selfEmpEmail = findViewById(R.id.email);

        individual = findViewById(R.id.radio_individual);
        firm = findViewById(R.id.radio_firm);

        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);

        std = findViewById(R.id.stdCode);
        phone = findViewById(R.id.phoneNum);

        findIdErrorText();

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
            }
        });

        businessFormPan.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(businessFormPan);
                }else{
                    businessFormPan.setError("Invalid PAN number.");
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
                    businessFormTan.setError("Invalid TAN number");
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
                    removeSuccess(businessFormWebsite);
                    businessFormWebsite.setError("Invalid website address");
                    View focusView = businessFormWebsite;
                    focusView.requestFocus();
                }
            }
        });

        businessFormName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {if(s.toString().trim().equals("")){
                businessNameErr.setVisibility(View.VISIBLE);
                businessNameErr.setText("Please provide name of your business.");
            } else{
                businessNameErr.setVisibility(View.GONE);
            }}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(businessFormName);
                    businessNameErr.setVisibility(View.GONE);
                }else{
                    removeSuccess(businessFormName);
                    businessNameErr.setVisibility(View.VISIBLE);
                    businessNameErr.setText("Invalid business name");
                    businessFormName.requestFocus();
                }
            }
        });

//        businessFormEstablishedYr.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString().trim().equals("")){
//                    businessEYErr.setVisibility(View.VISIBLE);
//                    businessEYErr.setText("Please enter established year.");
//                } else{
//                    businessEYErr.setVisibility(View.GONE);
//                }
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if(s.toString().length() == 4){
//                    showSuccess(businessFormEstablishedYr);
//                }else{
//                    businessFormEstablishedYr.setError("Invalid Year value");
//                    View focusView = businessFormEstablishedYr;
//                    focusView.requestFocus();
//                }
//            }
//        });

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
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    businessEmailErr.setVisibility(View.VISIBLE);
                    businessEmailErr.setText("Please enter your Email-Id.");
                } else{
                    businessEmailErr.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(validateEmail(s.toString())){
                    showSuccess(businessFormEmail);
                    businessEmailErr.setVisibility(View.GONE);
                }else{
                    businessEmailErr.setVisibility(View.VISIBLE);
                    businessEmailErr.setText("Invalid Email ID");
                    View focusView = businessFormEmail;
                    focusView.requestFocus();
                }
            }
        });

        selfEmpWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0){
                    showSuccess(selfEmpWebsite);
                }else{
                    selfEmpWebsite.setError("Invalid website address");
                    View focusView = selfEmpWebsite;
                    focusView.requestFocus();
                }
            }
        });

//        selfEmpExp.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString().trim().equals("")){
//                    professionalExpErr.setVisibility(View.VISIBLE);
//                    professionalExpErr.setText("Please enter professional exp.");
//                } else{
//                    professionalExpErr.setVisibility(View.GONE);
//                }
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if(s.length() != 0){
//                    showSuccess(selfEmpExp);
//                }else {
//                    selfEmpExp.setError("Invalid professional exp.");
//                    selfEmpExp.requestFocus();
//                }
//            }
//        });

        selfEmpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(validateEmail(s.toString())){
                    showSuccess(selfEmpEmail);
                    selfEmailErr.setVisibility(View.GONE);
                }else{
                    selfEmailErr.setVisibility(View.VISIBLE);
                    selfEmailErr.setText("Invalid Email ID");
                    selfEmpEmail.requestFocus();
                }
            }
        });

        salariedDesignation.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    designationErr.setVisibility(View.VISIBLE);
                    designationErr.setText("Please provide your designation.");
                } else{
                    designationErr.setVisibility(View.GONE);
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
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    addressErr.setVisibility(View.VISIBLE);
                    addressErr.setText("Please provide the address.");
                } else{
                    addressErr.setVisibility(View.GONE);
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
                    showSuccess(address);
                }else {
                    removeSuccess(address);
                }
            }
        });


        pincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    pincodeErr.setVisibility(View.VISIBLE);
                    pincodeErr.setText("Please enter a valid pincode.");
                } else{
                    pincodeErr.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    showSuccess(pincode);
                    pincodeErr.setVisibility(View.GONE);
                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                city.setText(response.getString("pin_city"));
                                state.setText(response.getString("pin_state"));
                                showSuccess(city);
                                showSuccess(state);
                                cityErr.setVisibility(View.GONE);
                                stateErr.setVisibility(View.GONE);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            pincodeErr.setVisibility(View.VISIBLE);
                            pincodeErr.setText("Invalid Pincode");
                            pincode.requestFocus();
                        }

                    });
                }else{
                    pincodeErr.setVisibility(View.VISIBLE);
                    pincodeErr.setText("Invalid Pincode");
                    pincode.requestFocus();
                }
            }
        });


        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

//        selfEmpForm.setVisibility(LinearLayout.VISIBLE);
//        businessForm.setVisibility(LinearLayout.VISIBLE);
//        salariedForm.setVisibility(LinearLayout.VISIBLE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);
        client.get(backend.BASE_URL + "/api/v1/retriveDetails/employement/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try {
                    String typ = c.getString("emp_type");
//                    typ =  "Salaried";
//                    typ =  "Business";
                    if (typ.equals("Salaried Employee")){
                        empType = "Salaried";
                        selfEmpForm.setVisibility(LinearLayout.GONE);
                        businessForm.setVisibility(LinearLayout.GONE);
                        salariedForm.setVisibility(LinearLayout.VISIBLE);
                        salariedDesignation.requestFocus();

                        salariedDesignation.setText(c.getString("emp_sal_desg"));
                        String eml = c.getString("emp_official_email");
                        if (eml.length() >0){
                            salariedEmail.setText(eml);
                        }

                    }else if(typ.equals("Self Employed Professional")){
                        empType = "Self Employed";
                        selfEmpForm.setVisibility(LinearLayout.VISIBLE);
                        businessForm.setVisibility(LinearLayout.GONE);
                        salariedForm.setVisibility(LinearLayout.GONE);
//                        selfEmpExp.setText(c.getString("emp_total_professional_exp"));
                        selfEmpEmail.setText(c.getString("emp_official_email"));
                        selfEmpWebsite.requestFocus();
                    }else {
                        empType = "Business";
                        selfEmpForm.setVisibility(LinearLayout.GONE);
                        businessForm.setVisibility(LinearLayout.VISIBLE);
                        salariedForm.setVisibility(LinearLayout.GONE);
                        businessFormName.requestFocus();
                        businessFormName.setText(c.getString("emp_comp_name"));
                        businessFormEmail.setText(c.getString("emp_official_email"));
//                        businessFormEstablishedYr.setText(c.getString("emp_bus_est_year"));
                        businessFormWebsite.setText(c.getString("emp_comp_website"));
                    }

                    String bPan = c.getString("emp_comp_pan");
                    if (bPan.length()>0){
                        businessFormPan.setText(bPan);
                    }
                    String bTan = c.getString("emp_comp_tan");
                    if (bTan.length()>0){
                        businessFormTan.setText(bTan);
                    }

                   if (c.getString("emp_self_firm_type").equals("Individual")){
                       RadioButton indivBtn = findViewById(R.id.radio_individual);
                       indivBtn.setChecked(true);
                   }else if (c.getString("emp_self_firm_type").equals("Firm")) {
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
                    String pin = addrs.getString("pincode");
                    if (pin.length()>0){
                        pincode.setText(pin);
                    }
                    state.setText(addrs.getString("state"));

                }catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("onFailure","Failure");
            }

        });


        Button next = findViewById(R.id.next_button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(false);
            }
        });

        Button saveBtn = findViewById(R.id.save_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
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
        String designation = salariedDesignation.getText().toString().trim();
        String businessName = businessFormName.getText().toString().trim();
//        String businessEY = businessFormEstablishedYr.getText().toString().trim();
        String businessEmail = businessFormEmail.getText().toString().trim();
//        String professionalExp = selfEmpExp.getText().toString().trim();
        String address1 = address.getText().toString().trim();
        String city1 = city.getText().toString().trim();
        String pinCode = pincode.getText().toString().trim();
        String state1 = state.getText().toString().trim();

        if (designation.isEmpty()){
            designationErr.setVisibility(View.VISIBLE);
            designationErr.setText("Please provide your designation.");
        } else {
            designationErr.setVisibility(View.GONE);
        }

        if (businessName.isEmpty()){
            businessNameErr.setVisibility(View.VISIBLE);
            businessNameErr.setText("Please provide name of your business.");
        } else {
            businessNameErr.setVisibility(View.GONE);
        }

//        if (businessEY.isEmpty()){
//            businessEYErr.setVisibility(View.VISIBLE);
//            businessEYErr.setText("Please enter established year.");
//        } else {
//            businessEYErr.setVisibility(View.GONE);
//        }

        if (businessEmail.isEmpty()){
            businessEmailErr.setVisibility(View.VISIBLE);
            businessEmailErr.setText("Please enter your Email-Id.");
        } else {
            businessEmailErr.setVisibility(View.GONE);
        }

//        if (professionalExp.isEmpty()){
//            professionalExpErr.setVisibility(View.VISIBLE);
//            professionalExpErr.setText("Please enter professional exp.");
//        } else {
//            professionalExpErr.setVisibility(View.GONE);
//        }

        if (address1.isEmpty()){
            addressErr.setVisibility(View.VISIBLE);
            addressErr.setText("Please provide the address.");
        } else {
            addressErr.setVisibility(View.GONE);
        }

        if (pinCode.isEmpty()){
            pincodeErr.setVisibility(View.VISIBLE);
            pincodeErr.setText("Please enter a valid pincode.");
        } else {
            pincodeErr.setVisibility(View.GONE);
            if (city1.isEmpty()){
                cityErr.setVisibility(View.VISIBLE);
                cityErr.setText("Please enter city.");
            } else {
                cityErr.setVisibility(View.GONE);
            }

            if (state1.isEmpty()){
                stateErr.setVisibility(View.VISIBLE);
                stateErr.setText("Please enter State.");
            } else {
                stateErr.setVisibility(View.GONE);
            }
        }

        if (individual.isChecked() || firm.isChecked()){
            radioIFErr.setVisibility(View.GONE);
        }else {
            radioIFErr.setVisibility(View.VISIBLE);
            radioIFErr.setText("Please provide communication address of your firm.");
        }

        if (address1.length() == 0){
            Toast.makeText(this, "Please provide the address.", Toast.LENGTH_SHORT).show();
            address.requestFocus();
            return;
        }

        if (pinCode.length() == 0){
            Toast.makeText(this, "Please enter a valid pincode.", Toast.LENGTH_SHORT).show();
            pincode.requestFocus();
            return;
        }
        if (city1.length() == 0){
            Toast.makeText(this, "Please enter city.", Toast.LENGTH_SHORT).show();
            city.requestFocus();
            return;
        }
        if (state1.length() == 0){
            Toast.makeText(this, "Please enter state.", Toast.LENGTH_SHORT).show();
            state.requestFocus();
            return;
        }


        JSONObject jsonParams = new JSONObject();
        String emp = "";
        if (empType.equals("Salaried")){
            emp = "Salaried";
        }
        if (empType.equals("Business")){
            emp = "Business";
        }else
//            if(emp == "Self Employed Professional"){
//            emp = "Self Employed";
//        }
        if(empType.equals("Self Employed")){
            emp = "Self Employed";
        }

        if (emp.equals("Salaried")){
            if (designation.isEmpty()){
                designationErr.setVisibility(View.VISIBLE);
                designationErr.setText("Please provide your designation.");
            } else {
                designationErr.setVisibility(View.GONE);
            }

            if (designation.length() == 0){
                Toast.makeText(this, "Please provide your designation.", Toast.LENGTH_SHORT).show();
                salariedDesignation.requestFocus();
                return;
            }
        } else if (emp.equals("Business")){
            if (businessName.length() == 0){
                Toast.makeText(this, "Please provide name of your business.", Toast.LENGTH_SHORT).show();
                businessFormName.requestFocus();
                return;
            }
//            if (businessEY.length() == 0){
//                Toast.makeText(this, "Please enter established year", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (businessEmail.length() == 0){
                Toast.makeText(this, "Please enter your Email-Id.", Toast.LENGTH_SHORT).show();
                businessFormEmail.requestFocus();
                return;
            }
        } else if (emp.equals("Self Employed")){
//            if (professionalExp.length() == 0){
//                Toast.makeText(this, "Please enter professional experience", Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (individual.isChecked() ){
                empDetailsSelfEmpType = "individual";
                radioIFErr.setVisibility(View.GONE);
            }
            else if (firm.isChecked()){
                empDetailsSelfEmpType = "firm";
                radioIFErr.setVisibility(View.GONE);
            }else {
                radioIFErr.setVisibility(View.VISIBLE);
                radioIFErr.setText("Please provide communication address of your firm.");
                Toast.makeText(this, "Please choose one.", Toast.LENGTH_SHORT).show();
                individual.requestFocus();
                return;
            }
        } else {
            Log.d("","");
        }

        JSONObject empDetailsSalaried = new JSONObject();

        try{

            empDetailsSalaried.put("designation" , salariedDesignation.getText().toString());
            empDetailsSalaried.put("emailID" , salariedEmail.getText().toString());
        }catch (JSONException e){

        }

        JSONObject empDetailsSelfEmp = new JSONObject();

        try{


//            empDetailsSelfEmp.put("totalExp" , selfEmpExp.getText().toString());
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
//            empDetailsBusiness.put("stablishedyear" , businessFormEstablishedYr.getText().toString());
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
//                if (empDetailsSelfEmpType.length() == 0){
//                    Toast.makeText(this, "Please select the type", Toast.LENGTH_SHORT).show();
//                    return;
//                }
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
            save(true);
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

    public void findIdErrorText() {
        designationErr = findViewById(R.id.designationErrTxt);
        selfEmailErr = findViewById(R.id.selfEmailErrTxt);
        businessNameErr = findViewById(R.id.bnameErrTxt);
//        businessEYErr = findViewById(R.id.bEYErrTxt);
        businessEmailErr = findViewById(R.id.bEmailErrTxt);
//        professionalExpErr = findViewById(R.id.tpeYearErrTxt);
        radioIFErr = findViewById(R.id.radio_I_F_ErrTxt);
        addressErr = findViewById(R.id.addressErrTxt);
        pincodeErr = findViewById(R.id.pinCodeErrTxt);
        cityErr = findViewById(R.id.cityErrTxt);
        stateErr = findViewById(R.id.stateErrTxt);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
