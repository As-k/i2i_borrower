package in.co.cioc.i2i;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RegistrationCheckEligibility extends AppCompatActivity {

    String addressState = "";
    Boolean loading;
    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend;
    Spinner dropdownPurpose , spinnerPaymentMode , spinnerHouseType , dropdownEmpType , dropdown;
    private int year, month, day;
    private Calendar calendar;
    private EditText bodEditTxt, pincodeEditTxt , cityEditTxt, workingSinceEditTxt , stayingSinceEditTxt;
    Drawable successTick;

    EditText amountTxt , descriptionTxt;

    EditText professionTypeTxt, turnoverSelfEmpTxt , profitSelfEmpTxt;
    EditText professionBusinessTxt, turnoverBusinessTxt , profitBusinessTxt;

    EditText incomeSalariedTxt , workExperienceTxt , workExperienceMonthTxt;
    AutoCompleteTextView companyTxt;

    EditText monthlyRentTxt, monthlyIncomeSalariedTxt, otherIncomeTxt , spouseIncomeTxt;

    LinearLayout loanEmiLayout , ccOutstandingLayout , rentForm , spouseIncomeLayout;

    EditText loanEmiTxt , ccOutstandingTxt, creditScoreTxt;

    Button submit_button;
    SharedPreferences sharedPreferences;

    ImageView linkedin_connect, fb_connect;
    TextView tv_linkined_connect, tv_fb_connect;
    ProgressDialog progress;

    private CallbackManager callbackManager;

    public void onRadioButtonClickedMarital(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_single:
                if (checked)

                    // Pirates are the best
                    break;
            case R.id.radio_married:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setWorkingSince(View view) {
        showDialog(1000);
    }

    public void setStayingSince(View view) {
        showDialog(1001);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }else if (id == 1000) {
            return new DatePickerDialog(this,
                    WorkingSinceListner, year, month, day);
        }else if (id == 1001) {
            return new DatePickerDialog(this,
                    StayingSinceListner, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            arg2 +=1;

            bodEditTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2).append("/").append(arg1));
            showSuccess(bodEditTxt);
        }
    };


    private DatePickerDialog.OnDateSetListener WorkingSinceListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            arg2 +=1;

            workingSinceEditTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2).append("/").append(arg1));
            showSuccess(workingSinceEditTxt);

            workExperienceTxt.requestFocus();
        }
    };

    private DatePickerDialog.OnDateSetListener StayingSinceListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            arg2 +=1;

            stayingSinceEditTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2).append("/").append(arg1));
            showSuccess(stayingSinceEditTxt);

            otherIncomeTxt.requestFocus();
        }
    };


    private Date strToDate(String dateStr){
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

;

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_registration_check_eligibility);

        backend = new Backend();

        bodEditTxt = findViewById(R.id.dob);

        linkedin_connect = findViewById(R.id.linkedin_connect);
//        fb_connect = findViewById(R.id.fb_connect);
        tv_linkined_connect = findViewById(R.id.tv_linkedin_connect);
        tv_fb_connect = findViewById(R.id.tv_fb_connect);

        progress = new ProgressDialog(this);

        linkedin_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress.setTitle("Please wait");
                progress.setMessage("Connecting to LinkedIn");
//                progress.setCancelable(false);
                progress.show();

            }
        });

//        fb_connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                progress.setTitle("Please wait");
//                progress.setMessage("Connecting to Facebook");
////                progress.setCancelable(false);
//                progress.show();
//
//            }
//        });
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                tv_fb_connect.setVisibility(View.VISIBLE);
                tv_fb_connect.setText("Connected");
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        workingSinceEditTxt = findViewById(R.id.workingSince);
        stayingSinceEditTxt = findViewById(R.id.stayingSince);
        incomeSalariedTxt = findViewById(R.id.incomeSalaried);
        workExperienceTxt = findViewById(R.id.workExperience);
        workExperienceMonthTxt = findViewById(R.id.workExperienceMonth);
        companyTxt = findViewById(R.id.company);
        companyTxt.setAdapter(new AutoCompleteAdapter(this,companyTxt.getText().toString()  , "companySearch" ));




        final LinearLayout selfEmpForm = findViewById(R.id.emp_form_selfEmp);
        final LinearLayout businessForm = findViewById(R.id.emp_form_business);
        final LinearLayout salariedForm = findViewById(R.id.emp_form_salaried);

        spouseIncomeLayout = findViewById(R.id.spouseIncomeLayout);
        spouseIncomeLayout.setVisibility(LinearLayout.GONE);

        selfEmpForm.setVisibility(LinearLayout.GONE);
        businessForm.setVisibility(LinearLayout.GONE);
        salariedForm.setVisibility(LinearLayout.GONE);

        professionTypeTxt = findViewById(R.id.professionSelfEmp);
        turnoverSelfEmpTxt = findViewById(R.id.turnoverSelfEmp);
        profitSelfEmpTxt = findViewById(R.id.profitSelfEmp);

        professionTypeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(professionTypeTxt);
                }else {
                    removeSuccess(professionTypeTxt);
                    professionTypeTxt.setError("Please tell us about the profession type you are in");
                    professionTypeTxt.requestFocus();
                }
            }
        });

        turnoverSelfEmpTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(turnoverSelfEmpTxt);
                }else {
                    removeSuccess(turnoverSelfEmpTxt);
                    turnoverSelfEmpTxt.setError("What was your turnover last year");
                    turnoverSelfEmpTxt.requestFocus();
                }
            }
        });

        profitSelfEmpTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(profitSelfEmpTxt);
                }else {
                    removeSuccess(profitSelfEmpTxt);
                    profitSelfEmpTxt.setError("What was the profit you made last year");
                    profitSelfEmpTxt.requestFocus();
                }
            }
        });



        professionBusinessTxt = findViewById(R.id.professionBusiness);
        turnoverBusinessTxt = findViewById(R.id.turnoverBusiness);
        profitBusinessTxt = findViewById(R.id.profitBusiness);


        professionBusinessTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(professionBusinessTxt);
                }else {
                    removeSuccess(professionBusinessTxt);
                    professionBusinessTxt.setError("What is your business type");
                    professionBusinessTxt.requestFocus();
                }
            }
        });

        turnoverBusinessTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(turnoverBusinessTxt);
                }else {
                    removeSuccess(turnoverBusinessTxt);
                    turnoverBusinessTxt.setError("What was the turnover you had last year");
                    turnoverBusinessTxt.requestFocus();
                }
            }
        });

        profitBusinessTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(profitBusinessTxt);
                }else {
                    removeSuccess(profitBusinessTxt);
                    profitBusinessTxt.setError("What was the profit you made last year");
                    profitBusinessTxt.requestFocus();
                }
            }
        });


        loanEmiTxt = findViewById(R.id.loanEmi);
        ccOutstandingTxt = findViewById(R.id.ccOutstanding);
        creditScoreTxt = findViewById(R.id.creditScore);
        spouseIncomeTxt = findViewById(R.id.spouseIncome);

        amountTxt = findViewById(R.id.amount);
        amountTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Integer i = Integer.parseInt(s.toString());

                if(i<25000 || i > 300000){
                    removeSuccess(amountTxt);
                    amountTxt.setError("Amount can not be less then 25000 or greater than 300000");
                    amountTxt.requestFocus();
                }else if(i%5000 != 0){
                    amountTxt.setError("Please enter amount in the multiples of 5000");
                    amountTxt.requestFocus();
                } else {
                    showSuccess(amountTxt);
                }
            }
        });

        descriptionTxt = findViewById(R.id.description);
        descriptionTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(descriptionTxt);
                }else {
                    removeSuccess(descriptionTxt);
                    descriptionTxt.setError("Please explain why you need this loan");
                    descriptionTxt.requestFocus();
                }
            }
        });

        companyTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(companyTxt);
                }else {
                    removeSuccess(companyTxt);
                    companyTxt.setError("Please search and select a company");
                    companyTxt.requestFocus();
                }
            }
        });

        workExperienceMonthTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(workExperienceMonthTxt);
                }else {
                    removeSuccess(workExperienceMonthTxt);
                    workExperienceMonthTxt.setError("Please tell us your work experience");
                    workExperienceMonthTxt.requestFocus();
                }
            }
        });

        workExperienceTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(workExperienceTxt);
                }else {
                    removeSuccess(workExperienceTxt);
                    workExperienceTxt.setError("Please tell us your work experience");
                    workExperienceTxt.requestFocus();
                }
            }
        });

        workExperienceTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(workExperienceTxt);
                }else {
                    removeSuccess(workExperienceTxt);
                    workExperienceTxt.setError("Please tell us your work experience");
                    workExperienceTxt.requestFocus();
                }
            }
        });



        monthlyRentTxt = findViewById(R.id.monthlyRent);

        monthlyRentTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(monthlyRentTxt);
                }else {
                    removeSuccess(monthlyRentTxt);
                    monthlyRentTxt.setError("What is your monthly rent amount");
                    monthlyRentTxt.requestFocus();
                }
            }
        });

        creditScoreTxt = findViewById(R.id.creditScore);

        creditScoreTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(creditScoreTxt);
                }else {
                    removeSuccess(creditScoreTxt);
                }
            }
        });

        monthlyIncomeSalariedTxt = findViewById(R.id.incomeSalaried);
        monthlyIncomeSalariedTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(monthlyIncomeSalariedTxt);
                }else {
                    removeSuccess(monthlyIncomeSalariedTxt);
                    monthlyIncomeSalariedTxt.setError("What is your monthly take home salary");
                    monthlyIncomeSalariedTxt.requestFocus();
                }
            }
        });

        otherIncomeTxt = findViewById(R.id.otherIncome);
        otherIncomeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(otherIncomeTxt);
                }else {
                    removeSuccess(otherIncomeTxt);
                }
            }
        });

        loanEmiTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(loanEmiTxt);
                }else {
                    removeSuccess(loanEmiTxt);
                    loanEmiTxt.setError("What is your monthly EMIs on loan");
                    loanEmiTxt.requestFocus();
                }
            }
        });

        ccOutstandingTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(ccOutstandingTxt);
                }else {
                    removeSuccess(ccOutstandingTxt);
                    ccOutstandingTxt.setError("What is your existing Credit Card outstanding balance");
                    ccOutstandingTxt.requestFocus();
                }
            }
        });


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{ "Please select" ,"3", "6", "9" , "12", "15","18" ,"21","24"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdownEmpType = findViewById(R.id.spinnerEmpType);
        String[] itemsEmpType = new String[]{"Please select", "Salaried", "Self Employed", "Business" };
        ArrayAdapter<String> adapterEmpType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsEmpType);
        dropdownEmpType.setAdapter(adapterEmpType);

        spinnerPaymentMode = findViewById(R.id.spinnerPaymentMode);
        String[] itemsPaymentMode = new String[]{"Please select", "Cash", "Cheque", "Credit to Bank Account" };
        ArrayAdapter<String> adapterPaymentMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsPaymentMode);
        spinnerPaymentMode.setAdapter(adapterPaymentMode);

        spinnerHouseType = findViewById(R.id.spinnerHouseType);
        String[] itemsHouseType = new String[]{"Please select", "Rented", "Own", "Parental" };
        ArrayAdapter<String> adapterHouseType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsHouseType);
        spinnerHouseType.setAdapter(adapterHouseType);



        loading = true;

        spinnerHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    rentForm.setVisibility(LinearLayout.VISIBLE);
                }else{
                    rentForm.setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dropdownEmpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                if (position == 0){
                    salariedForm.setVisibility(LinearLayout.GONE);
                    selfEmpForm.setVisibility(LinearLayout.GONE);
                    businessForm.setVisibility(LinearLayout.GONE);

                }else if (position == 1){
                    salariedForm.setVisibility(LinearLayout.VISIBLE);
                    selfEmpForm.setVisibility(LinearLayout.GONE);
                    businessForm.setVisibility(LinearLayout.GONE);
                    if (!loading){
                        companyTxt.requestFocus();
                    }

                }else if (position == 2){
                    salariedForm.setVisibility(LinearLayout.GONE);
                    selfEmpForm.setVisibility(LinearLayout.VISIBLE);
                    businessForm.setVisibility(LinearLayout.GONE);
                    professionTypeTxt.requestFocus();

                }else if (position == 3){
                    salariedForm.setVisibility(LinearLayout.GONE);
                    selfEmpForm.setVisibility(LinearLayout.GONE);
                    businessForm.setVisibility(LinearLayout.VISIBLE);
                    professionBusinessTxt.requestFocus();
                }
                loading = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        dropdownPurpose = findViewById(R.id.spinnerPurpose);





        client.get(backend.BASE_URL + "/api/v1/loanPurpose" , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                    ArrayList<String> purps = new ArrayList<String>();

                    purps.add("Please select");

                    for(int i = 0; i < response.length(); i++)
                    {
                        try{
                            JSONObject object = response.getJSONObject(i);
                            try {
                                purps.add(object.getString("mst_prps_title"));
                            }catch (JSONException e){

                            }
                        }catch (JSONException e){

                        }
                    }


                    ArrayAdapter<String> adapterPurpose = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, purps);
                    dropdownPurpose.setAdapter(adapterPurpose);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );

        ccOutstandingLayout = findViewById(R.id.ccOutstandingLayout);
        loanEmiLayout = findViewById(R.id.loanEmiLayout);
        ccOutstandingLayout.setVisibility(LinearLayout.GONE);
        loanEmiLayout.setVisibility(LinearLayout.GONE);

        rentForm = findViewById(R.id.rentForm);
        rentForm.setVisibility(LinearLayout.GONE);

        pincodeEditTxt = findViewById(R.id.pincode);
        cityEditTxt = findViewById(R.id.city);

        pincodeEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    showSuccess(pincodeEditTxt);

                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                cityEditTxt.setText(response.getString("pin_city"));
                                addressState = response.getString("pin_state");
                                showSuccess(cityEditTxt);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });


                }else{
                    pincodeEditTxt.setError("Invalid Pincode");
                    View focusView = pincodeEditTxt;
                    focusView.requestFocus();
                }
            }
        });


        submit_button = findViewById(R.id.submit_button);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer desiredAmount = 0;
                try{
                    desiredAmount = Integer.parseInt(amountTxt.getText().toString());
                }catch(Exception e){

                }

                String tenure = dropdown.getSelectedItem().toString();
                if (tenure.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select a tenure", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer purpose = dropdownPurpose.getSelectedItemPosition();
                if (purpose.equals(0)){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select a purpose", Toast.LENGTH_SHORT).show();
                    return;
                }
                String description = descriptionTxt.getText().toString();

                Boolean married = false;
                RadioButton radioMarried = findViewById(R.id.radio_married);
                RadioButton radioSingle = findViewById(R.id.radio_single);



                if (radioMarried.isChecked()){
                    married = true;
                }else if (radioSingle.isChecked()){
                    married = false;
                }

                Date dob = strToDate(bodEditTxt.getText().toString());

                String pincode = pincodeEditTxt.getText().toString();
                String city = cityEditTxt.getText().toString();
                String state = addressState;

                String empType = dropdownEmpType.getSelectedItem().toString();

                if (empType.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your employment type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (empType == "Business"){
                    String type = professionBusinessTxt.getText().toString();

                    try{
                        Integer grossTurnover = Integer.parseInt(turnoverBusinessTxt.getText().toString());
                        Integer grossAnnualProfit = Integer.parseInt(turnoverBusinessTxt.getText().toString());
                    }catch ( Exception e){

                    }


                }

                if (empType == "Self Employed"){
                    String type = professionTypeTxt.getText().toString();
                    try{
                        Integer grossTurnover = Integer.parseInt(turnoverSelfEmpTxt.getText().toString());
                        Integer grossAnnualProfit = Integer.parseInt(profitSelfEmpTxt.getText().toString());
                    }catch ( Exception e){

                    }

                }

                if (empType == "Salaried"){
                    String compName = companyTxt.getText().toString();
                    try{
                        Integer monthlyIncome = Integer.parseInt(incomeSalariedTxt.getText().toString());
                    }catch (Exception e){

                    }

                    String empPaymentType = spinnerPaymentMode.getSelectedItem().toString();
                    Date workingSince = strToDate(workingSinceEditTxt.getText().toString());
                    String expYear = workExperienceTxt.getText().toString();
                    String expMonth = workExperienceMonthTxt.getText().toString();
                }

                String houseType = spinnerHouseType.getSelectedItem().toString();
                Integer otherMonthlyIncome = 0;
                try{
                    otherMonthlyIncome = Integer.parseInt(otherIncomeTxt.getText().toString());
                }catch (Exception e){

                }


                RadioButton yesLoan = findViewById(R.id.radio_yes_loan);
                RadioButton noLoan = findViewById(R.id.radio_no_loan);

                Boolean loanRunning = false;

                Integer runningLoanEmi = 0;
                if (noLoan.isChecked()){
                    loanRunning = false;
                }
                if (yesLoan.isChecked()){
                    loanRunning = true;
                    try{
                        runningLoanEmi = Integer.parseInt(loanEmiTxt.getText().toString());
                    }catch (Exception e){

                    }

                }


                RadioButton yesCC = findViewById(R.id.radio_yes_creditCard);
                RadioButton noCC = findViewById(R.id.radio_no_creditCard);

                Boolean creditCard = false;
                Integer creditCardOutstanding = 0;
                if (yesCC.isChecked()){
                    creditCard = true;
                }
                if (noCC.isChecked()){
                    creditCard = false;
                    try{
                        creditCardOutstanding = Integer.parseInt(ccOutstandingTxt.getText().toString());
                    }catch (Exception e){

                    }

                }



                Integer cibilScore = 0;

                try{
                    cibilScore =Integer.parseInt(creditScoreTxt.getText().toString());
                }catch (Exception e){

                }

                Integer spouseMonthlyIncome = 0;
                if (married){
                    try{
                        spouseMonthlyIncome = Integer.parseInt(spouseIncomeTxt.getText().toString());
                    }catch (Exception e){

                    }

                }

                String monthlyRent = "0";
                Date stayingSince = null;
                Boolean canProvideGurantor = false;
                if (houseType == "Rented"){

                    RadioButton haveGurantee = findViewById(R.id.canProvideGurantee_yes);
                    RadioButton noGurantee = findViewById(R.id.canProvideGurantee_no);

                    if (noGurantee.isChecked()){
                        canProvideGurantor = false;
                    }
                    if (haveGurantee.isChecked()){
                        canProvideGurantor = true;
                    }
                    monthlyRent = monthlyRentTxt.getText().toString();
                    stayingSince = strToDate(stayingSinceEditTxt.getText().toString());
                }


/*http://localhost:8080/api/v1/checkEligibility/?csrf_token=XzaphWgrzwWRKtBjLkRneKYaq&session_id=gs7Ix9YWiMtmdNsbeFBYYhJIi
{
    "loanDetails": {
        "desiredAmount": 45000,
        "tenure": "6",
        "purpose": "4",
        "description": "fdfsdfsd"
    },
    "personalDetails": {
        "married": false,
        "dateOfBirth": "2018-01-11T04:54:00.000Z",
        "pincode": "201301",
        "city": "Gautam Buddha Nagar",
        "state": "UTTAR PRADESH\r"
    },
    "empType": "Self Employed",
    "employementDetails": {
        "type": "34234",
        "grossTurnover": 42342,
        "grossAnnualProfit": 423
    },
    "financialDetails": {
        "houseType": "Own",
        "otherMonthlyIncome": 323,
        "loanRunning": false,
        "runningLoanEmi": 0,
        "creditCard": false,
        "creditCardOutstanding": 0,
        "cibilScore": 23,
        "spouseMonthlyIncome": 0,
        "canProvideGurantor": null,
        "monthlyRent": 0,
        "stayingSince": null
        }
    }

*/

                JSONObject jsonParams = new JSONObject();

                JSONObject loanDetails = new JSONObject();
                JSONObject personalDetails = new JSONObject();
                JSONObject employementDetails = new JSONObject();
                JSONObject financialDetails = new JSONObject();
                try{
                    loanDetails.put("desiredAmount" , desiredAmount);
                    loanDetails.put("tenure" , tenure);
                    loanDetails.put("purpose" , purpose);
                    loanDetails.put("description" , description);
                }catch (JSONException e){

                }

                try{
                    personalDetails.put("married" , married);
                    personalDetails.put("dateOfBirth" , dob);
                    personalDetails.put("pincode" , pincode);
                    personalDetails.put("city" , city);
                    personalDetails.put("state" , state);

                }catch (JSONException e){

                }

                if (empType == "Salaried"){
                    try{
                        employementDetails.put("compName" , married);
                        employementDetails.put("monthlyIncome" , dob);
                        employementDetails.put("empType" , pincode);
                        employementDetails.put("workingSince" , city);
                        employementDetails.put("expYear" , state);
                        employementDetails.put("expMonth" , state);

                    }catch (JSONException e){

                    }
                }else if (empType == "Self Employed" || empType == "Business"){
                    try{
                        employementDetails.put("grossTurnover" , married);
                        employementDetails.put("grossAnnualProfit" , dob);
                        employementDetails.put("type" , pincode);
                    }catch (JSONException e){

                    }
                }


                try{
                    financialDetails.put("houseType" , houseType);
                    financialDetails.put("otherMonthlyIncome" , otherMonthlyIncome);
                    financialDetails.put("loanRunning" , loanRunning);
                    financialDetails.put("runningLoanEmi" , runningLoanEmi);
                    financialDetails.put("creditCard" , creditCard);
                    financialDetails.put("creditCardOutstanding" , creditCardOutstanding);
                    financialDetails.put("cibilScore" , cibilScore);
                    financialDetails.put("spouseMonthlyIncome" , spouseMonthlyIncome);
                    financialDetails.put("canProvideGurantor" , canProvideGurantor);
                    financialDetails.put("monthlyRent" , monthlyRent);
                    financialDetails.put("stayingSince" , stayingSince);
                }catch (JSONException e){

                }

                try{
                    jsonParams.put("loanDetails" , loanDetails);
                    jsonParams.put("personalDetails" , personalDetails);
                    jsonParams.put("empType" , empType);
                    jsonParams.put("employementDetails" , employementDetails);
                    jsonParams.put("financialDetails" , financialDetails);
                }catch (JSONException e){

                }

                StringEntity entity = null;

                try{
                    entity = new StringEntity(jsonParams.toString());
                }catch(Exception e){

                }

                sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

                String session_id = sharedPreferences.getString("session_id" , null);
                String csrf_token = sharedPreferences.getString("csrf_token" , null);

                client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/checkEligibility/?csrf_token=" + csrf_token + "&session_id=" + session_id , entity , "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Boolean eligible = false;
                        try {
                            eligible = response.getBoolean("eligible");
                        }catch (JSONException e){

                        }
                        String msg = "Sorry, You are not eligible";
                        Integer icon = R.drawable.thumbsdown_icon;
                        if (eligible){
                            icon = R.drawable.thumsup_icon;
                            msg = "Congratulations, You are eligible";
                        }

                        LovelyStandardDialog dialog = new LovelyStandardDialog(RegistrationCheckEligibility.this);

                        dialog.setIcon(icon)
                                .setTopColorRes(R.color.white)
                                .setMessage(msg)
                                .setPositiveButton("Proceed", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getApplicationContext(), "positive clicked", Toast.LENGTH_SHORT).show();

                                        Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
                                        startActivity(i);

                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                });

            }
        });


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes_creditCard:
                if (checked)
                    ccOutstandingLayout.setVisibility(LinearLayout.VISIBLE);
                    ccOutstandingTxt.requestFocus();
                    break;
            case R.id.radio_no_creditCard:
                if (checked)
                    creditScoreTxt.requestFocus();
                    ccOutstandingLayout.setVisibility(LinearLayout.GONE);
                    break;
            case R.id.radio_yes_loan:
                if (checked)
                    loanEmiLayout.setVisibility(LinearLayout.VISIBLE);
                    loanEmiTxt.requestFocus();
                break;
            case R.id.radio_no_loan:
                if (checked)
                    creditScoreTxt.requestFocus();
                    loanEmiLayout.setVisibility(LinearLayout.GONE);
                break;
            case R.id.radio_single:
                if (checked)
                    spouseIncomeLayout.setVisibility(LinearLayout.GONE);
                break;
            case R.id.radio_married:
                if (checked)
                    spouseIncomeLayout.setVisibility(LinearLayout.VISIBLE);
                break;
        }
    }

    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }


}
