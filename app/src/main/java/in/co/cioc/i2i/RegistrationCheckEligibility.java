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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
    private EditText dobEditTxt, pincodeEditTxt , cityEditTxt, workingSinceEditTxt , stayingSinceEditTxt;
    Drawable successTick;

    EditText amountTxt , descriptionTxt;

    TextView amountErr, tenureLoneErr, purposeErr, descriptionErr, genderErr, dobErr, pincodeErr, cityErr, empTypeErr, companyErr, monthlyIncomeErr, salaryErr, workSinceErr, workEYErr, workEMErr, ccRadioErr, ccAmountErr;
    TextView businessTypeErr, bGrossATOErr, bGrossAPErr, professionTypeErr, pGrossATOErr, pGrossAPErr, houseTypeErr, monthlyRentErr, gurantorRadiodErr, stayingSinceErr, otherMIErr, spouseErr, loanRadioErr, emiErr, sibilScoreErr;

    EditText professionTypeTxt, turnoverSelfEmpTxt , profitSelfEmpTxt;
    EditText professionBusinessTxt, turnoverBusinessTxt , profitBusinessTxt;

    EditText incomeSalariedTxt , workExperienceTxt , workExperienceMonthTxt;
    AutoCompleteTextView companyTxt;

    EditText monthlyRentTxt, monthlyIncomeSalariedTxt, otherIncomeTxt , spouseIncomeTxt;

    LinearLayout loanEmiLayout , ccOutstandingLayout , rentForm , spouseIncomeLayout;

    EditText loanEmiTxt , ccOutstandingTxt, creditScoreTxt;

    RadioButton single, married, gurantor_yes, gurantor_no, loan_yes, loan_no, cc_yes, cc_no;

    Button submit_button;
    SharedPreferences sharedPreferences;

    ImageView linkedin_connect;
    LoginButton loginButton;
    TextView tv_linkined_connect, tv_fb_connect;
    ProgressDialog progress;

    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private final String TAG = RegistrationCheckEligibility.this.getClass().getName();
    private static final String topCardUrl = "https://api.linkedin.com/v1/people/~:(first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

    public void onRadioButtonClickedMarital(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_single:
                if (checked) {
                    genderErr.setVisibility(View.GONE);
                    spouseErr.setVisibility(View.GONE);
                    spouseIncomeLayout.setVisibility(View.GONE);
                    // Pirates are the best
                    break;
                }
            case R.id.radio_married:
                if (checked) {
                    genderErr.setVisibility(View.GONE);
                    spouseIncomeLayout.setVisibility(View.VISIBLE);
                    // Ninjas rule
                    break;
                }
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

            dobEditTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2).append("/").append(arg1));
            showSuccess(dobEditTxt);
            dobErr.setVisibility(View.GONE);
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
            workSinceErr.setVisibility(View.GONE);
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
            stayingSinceErr.setVisibility(View.GONE);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_registration_check_eligibility);

        backend = new Backend();

        errorTextFindID();

        dobEditTxt = findViewById(R.id.dob);

        linkedin_connect = findViewById(R.id.linkedin_connect);
//        fb_connect = findViewById(R.id.fb_connect);
        loginButton = findViewById(R.id.login_button);
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
        initParameters();
        initViews();
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    Log.d(TAG, "User logged out successfully");
                    tv_fb_connect.setVisibility(View.GONE);
                }
            }
        };

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


        single = findViewById(R.id.radio_single);
        married = findViewById(R.id.radio_married);
        gurantor_yes = findViewById(R.id.canProvideGurantee_yes);
        gurantor_no = findViewById(R.id.canProvideGurantee_no);
        loan_yes = findViewById(R.id.radio_yes_loan);
        loan_no = findViewById(R.id.radio_no_loan);
        cc_yes = findViewById(R.id.radio_yes_creditCard);
        cc_no = findViewById(R.id.radio_no_creditCard);




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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    professionTypeErr.setVisibility(View.VISIBLE);
                    professionTypeErr.setText("Please select profession type");
                } else {
                    professionTypeErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pGrossATOErr.setVisibility(View.VISIBLE);
                    pGrossATOErr.setText("Please enter your turnover last year");
                } else {
                    pGrossATOErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pGrossAPErr.setVisibility(View.VISIBLE);
                    pGrossAPErr.setText("Please enter the profit you made last year ");
                } else {
                    pGrossAPErr.setVisibility(View.GONE);
                }}
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    businessTypeErr.setVisibility(View.VISIBLE);
                    businessTypeErr.setText("Please select business type");
                } else {
                    businessTypeErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    bGrossATOErr.setVisibility(View.VISIBLE);
                    bGrossATOErr.setText("Please enter the turnover you had last year");
                } else {
                    bGrossATOErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    bGrossAPErr.setVisibility(View.VISIBLE);
                    bGrossAPErr.setText("Please enter the turnover you had last year");
                } else {
                    bGrossAPErr.setVisibility(View.GONE);
                }
            }
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

        amountTxt = findViewById(R.id.amount);
        amountTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    amountErr.setVisibility(View.VISIBLE);
                    amountErr.setText("Please enter amount");
                } else {
                    amountErr.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 0){
                    amountTxt.setError("Amount can not be Empty");
                    amountTxt.requestFocus();
                    return;
                }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    descriptionErr.setVisibility(View.VISIBLE);
                    descriptionErr.setText("Please enter description");
                } else {
                    descriptionErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    companyErr.setVisibility(View.VISIBLE);
                    companyErr.setText("Please select company name");
                } else {
                    companyErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    workEMErr.setVisibility(View.VISIBLE);
                    workEMErr.setText("Please enter experience in month");
                } else {
                    workEMErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    workEYErr.setVisibility(View.VISIBLE);
                    workEYErr.setText("Please enter experience in year");
                } else {
                    workEYErr.setVisibility(View.GONE);
                }
            }
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

        workingSinceEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    workSinceErr.setVisibility(View.VISIBLE);
                    workSinceErr.setText("Please enter when work started");
                } else {
                    workSinceErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    monthlyRentErr.setVisibility(View.VISIBLE);
                    monthlyRentErr.setText("Please enter your monthly rent amount");
                } else {
                    monthlyRentErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    sibilScoreErr.setVisibility(View.VISIBLE);
                    sibilScoreErr.setText("Please enter credit score amount");
                } else {
                    sibilScoreErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    monthlyIncomeErr.setVisibility(View.VISIBLE);
                    monthlyIncomeErr.setText("Please enter your monthly take home salary");
                } else {
                    monthlyIncomeErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    otherMIErr.setVisibility(View.VISIBLE);
                    otherMIErr.setText("Please enter other income");
                } else {
                    otherMIErr.setVisibility(View.GONE);
                }
            }
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

        loanEmiTxt = findViewById(R.id.loanEmi);
        ccOutstandingTxt = findViewById(R.id.ccOutstanding);
        spouseIncomeTxt = findViewById(R.id.spouseIncome);
        spouseIncomeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    showSuccess(spouseIncomeTxt);
                }else {
                    removeSuccess(spouseIncomeTxt);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().equals("")){
                    spouseErr.setVisibility(View.VISIBLE);
                    spouseErr.setText("Please enter spouse income");
                } else {
                    spouseErr.setVisibility(View.GONE);
                }
            }
        });

        loanEmiTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    emiErr.setVisibility(View.VISIBLE);
                    emiErr.setText("Please enter your monthly EMIs on loan");
                } else {
                    emiErr.setVisibility(View.GONE);
                }
            }
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    ccAmountErr.setVisibility(View.VISIBLE);
                    ccAmountErr.setText("Please enter your existing Credit Card outstanding balance");
                } else {
                    ccAmountErr.setVisibility(View.GONE);
                }
            }
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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tenureLoneErr.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dropdownEmpType = findViewById(R.id.spinnerEmpType);
        String[] itemsEmpType = new String[]{"Please select", "Salaried", "Self Employed", "Business" };
        ArrayAdapter<String> adapterEmpType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsEmpType);
        dropdownEmpType.setAdapter(adapterEmpType);

        spinnerPaymentMode = findViewById(R.id.spinnerPaymentMode);
        String[] itemsPaymentMode = new String[]{"Please select", "Cash", "Cheque", "Credit to Bank Account" };
        ArrayAdapter<String> adapterPaymentMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsPaymentMode);
        spinnerPaymentMode.setAdapter(adapterPaymentMode);

        spinnerPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                salaryErr.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerHouseType = findViewById(R.id.spinnerHouseType);
        String[] itemsHouseType = new String[]{"Please select", "Rented", "Own", "Parental" };
        ArrayAdapter<String> adapterHouseType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsHouseType);
        spinnerHouseType.setAdapter(adapterHouseType);

        loading = true;

        spinnerHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                houseTypeErr.setVisibility(View.GONE);
                if (i == 1){
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

                empTypeErr.setVisibility(View.GONE);
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

                    dropdownPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                purposeErr.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pincodeErr.setVisibility(View.VISIBLE);
                    pincodeErr.setText("Please enter pincode");
                } else {
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
                    showSuccess(pincodeEditTxt);

                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                cityEditTxt.setText(response.getString("pin_city"));
                                addressState = response.getString("pin_state");
                                showSuccess(cityEditTxt);
                                cityErr.setVisibility(View.GONE);
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

                String amount = amountTxt.getText().toString().trim();
                String description = descriptionTxt.getText().toString().trim();
                String dobStr = dobEditTxt.getText().toString().trim();
                String pincode = pincodeEditTxt.getText().toString().trim();
                String city = cityEditTxt.getText().toString().trim();
                String company = companyTxt.getText().toString().trim();
                String miSalary = monthlyIncomeSalariedTxt.getText().toString().trim();
                String workingSince_et = workingSinceEditTxt.getText().toString().trim();
                String workingExperience = workExperienceTxt.getText().toString().trim();
                String workEM = workExperienceMonthTxt.getText().toString().trim();
                String businessType_et = professionBusinessTxt.getText().toString().trim();
                String bTurnover = turnoverBusinessTxt.getText().toString().trim();
                String bProfit = profitBusinessTxt.getText().toString().trim();
                String professionType = professionTypeTxt.getText().toString().trim();
                String sTurnover = turnoverSelfEmpTxt.getText().toString().trim();
                String sProfit = profitSelfEmpTxt.getText().toString().trim();
                String monthlyRent_et = monthlyRentTxt.getText().toString().trim();
                String stayingSince_et = stayingSinceEditTxt.getText().toString().trim();
                String otherIncome = otherIncomeTxt.getText().toString().trim();
                String spouseIncome = spouseIncomeTxt.getText().toString().trim();
                String loanEmi = loanEmiTxt.getText().toString().trim();
                String ccOutstanding = ccOutstandingTxt.getText().toString().trim();
                String creditScore = creditScoreTxt.getText().toString().trim();

                if (amount.isEmpty()){
                    amountErr.setVisibility(View.VISIBLE);
                    amountErr.setText("Please enter amount.");
                } else {
                    amountErr.setVisibility(View.GONE);
                }
                if (description.isEmpty()){
                    descriptionErr.setVisibility(View.VISIBLE);
                    descriptionErr.setText("Please enter description.");
                } else {
                    descriptionErr.setVisibility(View.GONE);
                }
                if (dobStr.isEmpty()){
                    dobErr.setVisibility(View.VISIBLE);
                    dobErr.setText("Please enter dob.");
                } else {
                    dobErr.setVisibility(View.GONE);
                }
                if (pincode.isEmpty()){
                    pincodeErr.setVisibility(View.VISIBLE);
                    pincodeErr.setText("Please enter pincode.");
                } else {
                    pincodeErr.setVisibility(View.GONE);
                }

                if (city.isEmpty()){
                    cityErr.setVisibility(View.VISIBLE);
                    cityErr.setText("Please enter city.");
                } else {
                    cityErr.setVisibility(View.GONE);
                }

                if (otherIncome.isEmpty()) {
                    otherMIErr.setVisibility(View.VISIBLE);
                    otherMIErr.setText("Please enter other income amount.");
                } else {
                    otherIncomeTxt.setVisibility(View.GONE);
                }
                if (creditScore.isEmpty()){
                    sibilScoreErr.setVisibility(View.VISIBLE);
                    sibilScoreErr.setText("Please enter credit score.");
                } else {
                    sibilScoreErr.setVisibility(View.GONE);
                }

                if (single.isChecked() || married.isChecked()){
                    genderErr.setVisibility(View.GONE);
                    if (married.isChecked()){
                        if (spouseIncome.isEmpty()){
                            spouseErr.setVisibility(View.VISIBLE);
                            spouseErr.setText("Please enter spouse income.");
                        } else {
                            spouseErr.setVisibility(View.GONE);
                        }
                    }
                }else {
                    genderErr.setVisibility(View.VISIBLE);
                    genderErr.setText("Please choose marital.");
                }


                if (loan_yes.isChecked() || loan_no.isChecked()){
                    loanRadioErr.setVisibility(View.GONE);
                    if (loan_yes.isChecked()) {
                        if (loanEmi.isEmpty()) {
                            emiErr.setVisibility(View.VISIBLE);
                            emiErr.setText("Please enter monthly EMI's on loan.");
                        } else {
                            emiErr.setVisibility(View.GONE);
                        }
                    }
                }else {
                    loanRadioErr.setVisibility(View.VISIBLE);
                    loanRadioErr.setText("Please choose any loan running.");
                }

                if (cc_yes.isChecked() || cc_no.isChecked()){
                    ccRadioErr.setVisibility(View.GONE);
                    if (cc_yes.isChecked())
                        if (ccOutstanding.isEmpty()){
                            ccAmountErr.setVisibility(View.VISIBLE);
                            ccAmountErr.setText("Please enter amount");
                        } else {
                            ccAmountErr.setVisibility(View.GONE);
                        }
                }else {
                    ccRadioErr.setVisibility(View.VISIBLE);
                    ccRadioErr.setText("Please choose credit card.");
                }


                if (dropdownPurpose.getSelectedItemPosition() != 0){
                    purposeErr.setVisibility(View.GONE);
                }else {
                    purposeErr.setVisibility(View.VISIBLE);
                    purposeErr.setText("Please select one.");
                }

                if (spinnerHouseType.getSelectedItemPosition() != 0){
                    houseTypeErr.setVisibility(View.GONE);
                    if (spinnerHouseType.getSelectedItemPosition() == 1) {
                        if (monthlyRent_et.isEmpty()) {
                            monthlyRentErr.setVisibility(View.VISIBLE);
                            monthlyRentErr.setText("Please enter company");
                        } else {
                            monthlyRentErr.setVisibility(View.GONE);
                        }
                        if (gurantor_yes.isChecked() || gurantor_no.isChecked()) {
                            gurantorRadiodErr.setVisibility(View.GONE);
                        } else {
                            gurantorRadiodErr.setVisibility(View.VISIBLE);
                            gurantorRadiodErr.setText("Please choose gurantor.");
                        }
                        if (stayingSince_et.isEmpty()){
                            stayingSinceErr.setVisibility(View.VISIBLE);
                            stayingSinceErr.setText("Please enter date.");
                        } else {
                            sibilScoreErr.setVisibility(View.GONE);
                        }

                    }
                }else {
                    houseTypeErr.setVisibility(View.VISIBLE);
                    houseTypeErr.setText("Please select house type.");
                }


                if (dropdownEmpType.getSelectedItemPosition() != 0){
                    empTypeErr.setVisibility(View.GONE);
                    if (dropdownEmpType.getSelectedItemPosition() == 1) {
                        if (company.isEmpty()) {
                            companyErr.setVisibility(View.VISIBLE);
                            companyErr.setText("Please enter company name.");
                        } else {
                            companyErr.setVisibility(View.GONE);
                        }

                        if (miSalary.isEmpty()) {
                            monthlyIncomeErr.setVisibility(View.VISIBLE);
                            monthlyIncomeErr.setText("Please enter monthly income.");
                        } else {
                            monthlyIncomeErr.setVisibility(View.GONE);
                        }

                        if (spinnerPaymentMode.getSelectedItemPosition() != 0){
                            salaryErr.setVisibility(View.GONE);
                        }else {
                            salaryErr.setVisibility(View.VISIBLE);
                            salaryErr.setText("Please select one salary.");
                        }

                        if (workingSince_et.isEmpty()){
                            workSinceErr.setVisibility(View.VISIBLE);
                            workSinceErr.setText("Please enter work since");
                        } else {
                            workSinceErr.setVisibility(View.GONE);
                        }

                        if (workingExperience.isEmpty()){
                            workEYErr.setVisibility(View.VISIBLE);
                            workEYErr.setText("Please enter work experience in year");
                        } else {
                            workEYErr.setVisibility(View.GONE);
                        }

                        if (workEM.isEmpty()){
                            workEMErr.setVisibility(View.VISIBLE);
                            workEMErr.setText("Please enter work experince in month");
                        } else {
                            workEMErr.setVisibility(View.GONE);
                        }

                    } else if (dropdownEmpType.getSelectedItemPosition() == 2){
                        if (professionType.isEmpty()){
                            professionTypeErr.setVisibility(View.VISIBLE);
                            professionTypeErr.setText("Please enter profession type");
                        } else {
                            professionTypeErr.setVisibility(View.GONE);
                        }
                        if (sTurnover.isEmpty()){
                            pGrossATOErr.setVisibility(View.VISIBLE);
                            pGrossATOErr.setText("Please enter turnover amount");
                        } else {
                            pGrossATOErr.setVisibility(View.GONE);
                        }
                        if (sProfit.isEmpty()){
                            pGrossAPErr.setVisibility(View.VISIBLE);
                            pGrossAPErr.setText("Please enter profit amount");
                        } else {
                            pGrossAPErr.setVisibility(View.GONE);
                        }
                    }else if (dropdownEmpType.getSelectedItemPosition() == 3){
                        if (businessType_et.isEmpty()){
                            businessTypeErr.setVisibility(View.VISIBLE);
                            businessTypeErr.setText("Please enter business type");
                        } else {
                            businessTypeErr.setVisibility(View.GONE);
                        }
                        if (bTurnover.isEmpty()){
                            bGrossATOErr.setVisibility(View.VISIBLE);
                            bGrossATOErr.setText("Please enter turnover amount");
                        } else {
                            bGrossATOErr.setVisibility(View.GONE);
                        }
                        if (bProfit.isEmpty()){
                            bGrossAPErr.setVisibility(View.VISIBLE);
                            bGrossAPErr.setText("Please enter profit amount");
                        } else {
                            bGrossAPErr.setVisibility(View.GONE);
                        }
                    } else Log.d("Emptype","=== null");
                }else {
                    empTypeErr.setVisibility(View.VISIBLE);
                    empTypeErr.setText("Please select one employment type.");
                }

                if (dropdown.getSelectedItemPosition() != 0){
                    tenureLoneErr.setVisibility(View.GONE);
                }else {
                    tenureLoneErr.setVisibility(View.VISIBLE);
                    tenureLoneErr.setText("Please select one tenure of loan.");
                }



                Integer desiredAmount = 0;
                try{
                    desiredAmount = Integer.parseInt(amountTxt.getText().toString());
                }catch(Exception e){

                }

                if(desiredAmount<25000 || desiredAmount > 300000){
                    Toast.makeText(RegistrationCheckEligibility.this, "Amount can not be less then 25000 or greater than 300000", Toast.LENGTH_SHORT).show();
                    return;
                }else if(desiredAmount%5000 != 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please enter amount in the multiples of 5000", Toast.LENGTH_SHORT).show();
                    return;
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
//                String description = descriptionTxt.getText().toString();

                if (description.length()==0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please provide description for the loan requirement", Toast.LENGTH_SHORT).show();
                    return;
                }

                Boolean married = null;
                RadioButton radioMarried = findViewById(R.id.radio_married);
                RadioButton radioSingle = findViewById(R.id.radio_single);



                if (radioMarried.isChecked()){
                    married = true;
                }else if (radioSingle.isChecked()){
                    married = false;
                }else{
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your marital status", Toast.LENGTH_SHORT).show();
                    return;
                }

//                String dobStr = dobEditTxt.getText().toString();
                if (dobStr.length() == 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date dob = strToDate(dobStr);

//                String pincode = pincodeEditTxt.getText().toString();

                if (pincode.length() == 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please provide your pincode", Toast.LENGTH_SHORT).show();
                    return;
                }

//                String city = cityEditTxt.getText().toString();
                String state = addressState;

                String empType = dropdownEmpType.getSelectedItem().toString();

                if (empType.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your employment type", Toast.LENGTH_SHORT).show();
                    return;
                }

                String businessType  = "";
                Integer grossTurnover = 0;
                Integer grossAnnualProfit = 0;
                if (empType == "Business"){
                    businessType = professionBusinessTxt.getText().toString();

                    if (businessType.length()==0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business type", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try{
                        grossTurnover = Integer.parseInt(turnoverBusinessTxt.getText().toString());
                        grossAnnualProfit = Integer.parseInt(turnoverBusinessTxt.getText().toString());
                    }catch ( Exception e){

                    }

                    if (grossTurnover == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (grossAnnualProfit == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        return;
                    }


                }

                if (empType == "Self Employed"){
                    businessType = professionTypeTxt.getText().toString();
                    if (businessType.length()==0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business type", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try{
                        grossTurnover = Integer.parseInt(turnoverSelfEmpTxt.getText().toString());
                        grossAnnualProfit = Integer.parseInt(profitSelfEmpTxt.getText().toString());
                    }catch ( Exception e){

                    }

                    if (grossTurnover == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (grossAnnualProfit == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                String expYear="0";
                String expMonth="0";
                Date workingSince = null;
                Integer monthlyIncome = null;
                String compName = "";
                if (empType == "Salaried"){
                    compName = companyTxt.getText().toString();

                    if (compName.length()== 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your company name", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (compName.length()== 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your company name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String incomeSalriedTxt = incomeSalariedTxt.getText().toString();
                    if (incomeSalriedTxt.length()== 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide monthly income", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try{
                        monthlyIncome = Integer.parseInt(incomeSalriedTxt);
                    }catch (Exception e){

                    }

                    String empPaymentType = spinnerPaymentMode.getSelectedItem().toString();
                    if (empPaymentType.equals("Please select")){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please select your payment mode for salary", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String wrkingSinceTxt = workingSinceEditTxt.getText().toString();

                    if (wrkingSinceTxt.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please select a date since you are working in your company", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    workingSince = strToDate(wrkingSinceTxt);
                    expYear = workExperienceTxt.getText().toString();
                    expMonth = workExperienceMonthTxt.getText().toString();

                    if (expYear.length() == 0 && expMonth.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your experience", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                String houseType = spinnerHouseType.getSelectedItem().toString();
                if (houseType.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your residence type", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                }else if (yesLoan.isChecked()){
                    loanRunning = true;
                    try{
                        runningLoanEmi = Integer.parseInt(loanEmiTxt.getText().toString());
                    }catch (Exception e){

                    }

                    if (runningLoanEmi.equals(0)){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your current EMI", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }else{
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select if you have any loan running", Toast.LENGTH_SHORT).show();
                    return;
                }


                RadioButton yesCC = findViewById(R.id.radio_yes_creditCard);
                RadioButton noCC = findViewById(R.id.radio_no_creditCard);

                Boolean creditCard = false;
                Integer creditCardOutstanding = 0;
                if (yesCC.isChecked()){
                    creditCard = true;
                    try{
                        creditCardOutstanding = Integer.parseInt(ccOutstandingTxt.getText().toString());
                    }catch (Exception e){

                    }

                    if (creditCardOutstanding.equals(0)){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your current credit card outstanding", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (noCC.isChecked()){
                    creditCard = false;


                }else{
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select if you have any credit card", Toast.LENGTH_SHORT).show();
                    return;
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
                    }else if (haveGurantee.isChecked()){
                        canProvideGurantor = true;
                    }else{
                        Toast.makeText(RegistrationCheckEligibility.this, "Please select if you can provide a gurantor who has own house", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    monthlyRent = monthlyRentTxt.getText().toString();
                    if (monthlyRent.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your monthly rent", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String stayingSinceEdtTxt = stayingSinceEditTxt.getText().toString();
                    if (stayingSinceEdtTxt.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please select a date since you are living at current residence", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    stayingSince = strToDate(stayingSinceEdtTxt);
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
                        employementDetails.put("compName" , compName);
                        employementDetails.put("monthlyIncome" , monthlyIncome);
                        employementDetails.put("empType" , empType);
                        employementDetails.put("workingSince" , workingSince);
                        employementDetails.put("expYear" , expYear);
                        employementDetails.put("expMonth" , expMonth);

                    }catch (JSONException e){

                    }
                }else if (empType == "Self Employed" || empType == "Business"){
                    try{
                        employementDetails.put("grossTurnover" , grossTurnover);
                        employementDetails.put("grossAnnualProfit" , grossAnnualProfit);
                        employementDetails.put("type" , businessType);
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

    public void errorTextFindID(){
        amountErr = findViewById(R.id.amountErrTxt);
        tenureLoneErr = findViewById(R.id.tenureLoanErrTxt);
        purposeErr = findViewById(R.id.purposeErrTxt);
        descriptionErr = findViewById(R.id.descriptionErrTxt);
        genderErr = findViewById(R.id.genderErrTxt);
        dobErr = findViewById(R.id.dobErrTxt);
        pincodeErr = findViewById(R.id.pincodeErrTxt);
        cityErr = findViewById(R.id.cityErrTxt);
        empTypeErr = findViewById(R.id.empTypeErrTxt);
        companyErr = findViewById(R.id.companyErrTxt);
        monthlyIncomeErr = findViewById(R.id.incomeSalariedErrTxt);
        salaryErr = findViewById(R.id.paymentModeErrTxt);
        workSinceErr = findViewById(R.id.workingSinceErrTxt);
        workEYErr = findViewById(R.id.workExperienceErrTxt);
        workEMErr = findViewById(R.id.workExperienceMonthErrTxt);
        businessTypeErr = findViewById(R.id.professionBusinessErrTxt);
        bGrossATOErr = findViewById(R.id.turnoverBusinessErrTxt);
        bGrossAPErr = findViewById(R.id.profitBusinessErrTxt);
        professionTypeErr= findViewById(R.id.professionSelfEmpErrTxt);
        pGrossATOErr = findViewById(R.id.turnoverSelfEmpErrTxt);
        pGrossAPErr = findViewById(R.id.profitSelfEmpErrTxt);
        houseTypeErr = findViewById(R.id.houseTypeErrTxt);
        monthlyRentErr = findViewById(R.id.monthlyRentErrTxt);
        gurantorRadiodErr = findViewById(R.id.canProvideGuranteeErrTxt);
        stayingSinceErr = findViewById(R.id.stayingSinceErrTxt);
        otherMIErr = findViewById(R.id.otherIncomeErrTxt);
        spouseErr = findViewById(R.id.spouseIncomeErrTxt);
        loanRadioErr = findViewById(R.id.radioLoanErrTxt);
        emiErr = findViewById(R.id.loanEmiErrTxt);
        ccRadioErr = findViewById(R.id.radioCCErrTxt);
        ccAmountErr = findViewById(R.id.ccOutstandingErrTxt);
        sibilScoreErr = findViewById(R.id.creditScoreErrTxt);
    }

    public void initParameters() {
        accessToken = AccessToken.getCurrentAccessToken();
        callbackManager = CallbackManager.Factory.create();
    }

    public void initViews() {
        loginButton.setReadPermissions(Arrays.asList(new String[]{"email", "user_birthday", "user_hometown"}));

        if (accessToken != null) {
            getProfileData();
        } else {
            tv_fb_connect.setVisibility(View.GONE);
        }

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "User login successfully");
                getProfileData();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "User cancel login");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "Problem for login");
            }
        });
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getProfileData() {
        try {
            accessToken = AccessToken.getCurrentAccessToken();
            tv_fb_connect.setVisibility(View.VISIBLE);
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.d(TAG, "Graph Object :" + object);
                            try {
                                String name = object.getString("name");
                                tv_fb_connect.setText("Welcome,  "+ name);

                                Log.d(TAG, "Name : "+ name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday,gender,email");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes_creditCard:
                if (checked) {
                    ccOutstandingLayout.setVisibility(LinearLayout.VISIBLE);
                    ccRadioErr.setVisibility(View.GONE);
                ccOutstandingTxt.requestFocus();
                break;
            }
            case R.id.radio_no_creditCard:
                if (checked) {
                    creditScoreTxt.requestFocus();
                    ccOutstandingLayout.setVisibility(LinearLayout.GONE);
                    ccRadioErr.setVisibility(View.GONE);
                    break;
                }
            case R.id.radio_yes_loan:
                if (checked) {
                    loanRadioErr.setVisibility(View.GONE);
                    loanEmiLayout.setVisibility(LinearLayout.VISIBLE);
                    loanEmiTxt.requestFocus();
                    break;
                }
            case R.id.radio_no_loan:
                if (checked) {
                    loanRadioErr.setVisibility(View.GONE);
                    creditScoreTxt.requestFocus();
                    loanEmiLayout.setVisibility(LinearLayout.GONE);
                    break;
                }
            case R.id.canProvideGurantee_yes:
                if (checked) {
                    gurantorRadiodErr.setVisibility(View.GONE);
                    break;
                }
            case R.id.canProvideGurantee_no:
                if (checked) {
                    gurantorRadiodErr.setVisibility(View.GONE);
                    break;
                }
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
