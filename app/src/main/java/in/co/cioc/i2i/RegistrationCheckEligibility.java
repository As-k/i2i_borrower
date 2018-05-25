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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
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
    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    Backend backend;
    Spinner dropdownPurpose , spinnerPaymentMode , spinnerHouseType , dropdownEmpType , dropdown;
    ArrayAdapter<String> adapterPurpose, adapterPaymentMode, adapterHouseType;

    private int year, month, day;
    private Calendar calendar;
    DatePickerDialog dpd;
    private EditText dobEditTxt, pincodeEditTxt , cityEditTxt, workingSinceEditTxt , stayingSinceEditTxt;
    Drawable successTick;

    EditText amountTxt , descriptionTxt;
    TextView clickHere;

    TextView amountErr, tenureLoneErr, purposeErr, descriptionErr, genderErr, dobErr, pincodeErr, cityErr, empTypeErr, companyErr, monthlyIncomeErr, salaryErr, workSinceErr, workEYErr, workEMErr, ccRadioErr, ccAmountErr;
    TextView businessTypeErr, businessEYrErr, bGrossATOErr, bGrossAPErr, professionTypeErr, totalProfessionEYrErr, pGrossATOErr, pGrossAPErr, houseTypeErr, monthlyRentErr, gurantorRadiodErr, stayingSinceErr, otherMIErr, spouseErr, loanRadioErr, emiErr, sibilScoreErr;

    EditText professionTypeTxt, selfEmpExp, turnoverSelfEmpTxt , profitSelfEmpTxt;
    EditText incomeSalariedTxt , workExperienceTxt , workExperienceMonthTxt;

    AutoCompleteTextView companyTxt;
    EditText professionBusinessTxt, businessFormEstablishedYr, turnoverBusinessTxt , profitBusinessTxt;

    EditText monthlyRentTxt, monthlyIncomeSalariedTxt, otherIncomeTxt , spouseIncomeTxt;

    LinearLayout loanEmiLayout , ccOutstandingLayout , rentForm , spouseIncomeLayout;

    EditText loanEmiTxt , ccOutstandingTxt, creditScoreTxt;

    RadioButton single, married, gurantor_yes, gurantor_no, loan_yes, loan_no, cc_yes, cc_no;

    Button submit_button;
    SharedPreferences sharedPreferences;

    ImageView linkedin_connect;
    LoginButton loginButton;
    TextView tv_linkined_connect, tv_fb_connect;
//    ProgressDialog progress;

    private CallbackManager callbackManager;
    private AccessToken accessToken;
    public static final String PACKAGE = "in.co.cioc.i2i";
    private final String TAG = RegistrationCheckEligibility.this.getClass().getName();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

//        if (id == 999) {
//            return new DatePickerDialog(this,
//                    myDateListener, year, month, day);
//        } else
            if (id == 1000) {
            return new DatePickerDialog(this,
                    WorkingSinceListner, year, month, day);
        }else if (id == 1001) {
            return new DatePickerDialog(this,
                    StayingSinceListner, year, month, day);
        }
        return null;
    }

//    public DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker arg0,
//                              int arg1, int arg2, int arg3) {
//            // TODO Auto-generated method stub
//            // arg1 = year
//            // arg2 = month
//            // arg3 = day
//
//            arg2 +=1;
//            dobEditTxt.setText(new StringBuilder().append(arg3).append("/")
//                    .append(arg2).append("/").append(arg1));
//            showSuccess(dobEditTxt);
//            dobErr.setVisibility(View.GONE);
//        }
//
//    };

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
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); //yyyy-MM-dd'T'HH:mm:ss.SSSZ
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


//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.tile_background);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
//        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//        LinearLayout layout = new LinearLayout(this);
//        layout.setBackgroundDrawable(bitmapDrawable);

        backend = new Backend();
        errorTextFindID();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int old_yr, old_mon, old_day;

        dobEditTxt = findViewById(R.id.dob);
        dobEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 dpd = new DatePickerDialog(RegistrationCheckEligibility.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        dobEditTxt.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        calendar.set(year, month, dayOfMonth);
                        view.updateDate(year, month, dayOfMonth);
                        showSuccess(dobEditTxt);
                        dobErr.setVisibility(View.GONE);
                    }
                },year,month,day);
                DatePicker dp = dpd.getDatePicker();
//                dp.setMinDate(System.currentTimeMillis()-10*24*60*60*1000);
                dp.setMaxDate(System.currentTimeMillis());
//                dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH));
//                dpd.convertToComoletDate(da)
                dpd.show();
            }

        });
//        String dobE = dobEditTxt.getText().toString();
//        for (int i=0; i<dobE.length(); i++){
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//        }

//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            String mday = "02";
//            String mmonth="07";
//            String myear="2013";
//            //convert them to int
//            int mDay=Integer.valueOf(mday);
//            int mMonth=Integer.valueOf(mmonth);
//            int mYear=Integer.valueOf(myear);
//
//            return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                    String d = convertToCompletDate(i2,i1,i);
//                    mListener.onDatePicked(d);
//
//                }
//            },mYear,mMonth,mDay);
//        }

        linkedin_connect = findViewById(R.id.linkedin_connect);
        loginButton = findViewById(R.id.login_button);
        tv_linkined_connect = findViewById(R.id.tv_linkedin_connect);
        tv_linkined_connect.setVisibility(View.GONE);
        tv_fb_connect = findViewById(R.id.tv_fb_connect);
        tv_fb_connect.setVisibility(View.GONE);

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

        clickHere = findViewById(R.id.click_here);

        SpannableStringBuilder sb = new SpannableStringBuilder();
        String regularText = "Please describe about you loan requirment which can help you in attracting prospective investors. ";
        String clickableText = "Click here to see example";
        sb.append(regularText);
        sb.append(clickableText);
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                String title = "Need Money for Sister's Marriage";
                String msg = "My sister is getting married and I need money for her marriage. I have done MBA from a reputed college and have a stable job with fixed monthly salary. I have never defaulted in any kind of repayments so far. I have 2 credit cards and I have always paid the dues ontime. I would request you to help me in getting my sister's marriage completed without any issues. Thanks in advance.";
                final LovelyStandardDialog dialog = new LovelyStandardDialog(RegistrationCheckEligibility.this);

                dialog.setTitle(title)
                        .setTopColorRes(R.color.white)
                        .setMessage(msg).setCancelable(false)
                        .setPositiveButton("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.orange));
            }

        }, sb.length()-clickableText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        clickHere.setText(sb);
        clickHere.setMovementMethod(LinkMovementMethod.getInstance());

        final LinearLayout selfEmpForm = findViewById(R.id.emp_form_selfEmp);
        final LinearLayout businessForm = findViewById(R.id.emp_form_business);
        final LinearLayout salariedForm = findViewById(R.id.emp_form_salaried);

        spouseIncomeLayout = findViewById(R.id.spouseIncomeLayout);
        spouseIncomeLayout.setVisibility(LinearLayout.GONE);
        spouseIncomeTxt = findViewById(R.id.spouseIncome);

        selfEmpForm.setVisibility(LinearLayout.GONE);
        businessForm.setVisibility(LinearLayout.GONE);
        salariedForm.setVisibility(LinearLayout.GONE);

        professionTypeTxt = findViewById(R.id.professionSelfEmp);
        selfEmpExp = findViewById(R.id.selfEmpExp);
        turnoverSelfEmpTxt = findViewById(R.id.turnoverSelfEmp);
        profitSelfEmpTxt = findViewById(R.id.profitSelfEmp);

        professionTypeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    professionTypeErr.setVisibility(View.VISIBLE);
                    professionTypeErr.setText("What is your profession type ?");
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
                    professionTypeErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(professionTypeTxt);
                    professionTypeErr.setVisibility(View.VISIBLE);
                    professionTypeErr.setText("Please tell us about the profession type you are in");
                    professionTypeTxt.requestFocus();
                }
            }
        });

        selfEmpExp.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    totalProfessionEYrErr.setVisibility(View.VISIBLE);
                    totalProfessionEYrErr.setText("Please provide your total professional experience.");
                } else{
                    totalProfessionEYrErr.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    showSuccess(selfEmpExp);
                    totalProfessionEYrErr.setVisibility(View.GONE);
                }else {
                    totalProfessionEYrErr.setVisibility(View.VISIBLE);
                    totalProfessionEYrErr.setText("Please provide your total professional experience.");
                    selfEmpExp.requestFocus();
                }
            }
        });

        turnoverSelfEmpTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pGrossATOErr.setVisibility(View.VISIBLE);
                    pGrossATOErr.setText("Please enter your annual turnover (in Rs.)");
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
                    pGrossATOErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(turnoverSelfEmpTxt);
                    pGrossATOErr.setVisibility(View.GONE);
                    pGrossATOErr.setText("What was your turnover last year");
                    turnoverSelfEmpTxt.requestFocus();
                }
            }
        });

        profitSelfEmpTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    pGrossAPErr.setVisibility(View.VISIBLE);
                    pGrossAPErr.setText("Please enter gross annual profit (in Rs.)");
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
                    pGrossAPErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(profitSelfEmpTxt);
                    pGrossAPErr.setVisibility(View.VISIBLE);
                    pGrossAPErr.setText("What was the profit you made last year");
                    profitSelfEmpTxt.requestFocus();
                }
            }
        });



        professionBusinessTxt = findViewById(R.id.professionBusiness);
        businessFormEstablishedYr = findViewById(R.id.businessFormEstablishedYr);
        turnoverBusinessTxt = findViewById(R.id.turnoverBusiness);
        profitBusinessTxt = findViewById(R.id.profitBusiness);


        professionBusinessTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    businessTypeErr.setVisibility(View.VISIBLE);
                    businessTypeErr.setText("Please provide type of your business ?");
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
                    businessTypeErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(professionBusinessTxt);
                    businessTypeErr.setVisibility(View.VISIBLE);
                    businessTypeErr.setError("Please provide type of your business ?");
                    professionBusinessTxt.requestFocus();
                }
            }
        });

        businessFormEstablishedYr.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().equals("")){
                    businessEYrErr.setVisibility(View.VISIBLE);
                    businessEYrErr.setText("Please provide establishment year of your business.");
                } else if(s.toString().trim().length() == 4){
                    int years = Integer.parseInt(s.toString().trim());
                    if (1800 <= years && years <= year) {
                        businessEYrErr.setVisibility(View.GONE);
                        showSuccess(businessFormEstablishedYr);
                    } else {
                        businessEYrErr.setVisibility(View.VISIBLE);
                        businessEYrErr.setText("Please provide valid establishment year of your business.");
                        removeSuccess(businessFormEstablishedYr);
                    }

                }else{
                    businessEYrErr.setVisibility(View.VISIBLE);
                    businessEYrErr.setText("Invalid Year value.");
                    removeSuccess(businessFormEstablishedYr);
                }
            }
        });

        turnoverBusinessTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    bGrossATOErr.setVisibility(View.VISIBLE);
                    bGrossATOErr.setText("Please enter your annual turnover (in Rs.)");
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
                    bGrossATOErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(turnoverBusinessTxt);
                    bGrossATOErr.setVisibility(View.VISIBLE);
                    bGrossATOErr.setText("What was the turnover you had last year?");
                    turnoverBusinessTxt.requestFocus();
                }
            }
        });

        profitBusinessTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    bGrossAPErr.setVisibility(View.VISIBLE);
                    bGrossAPErr.setText("Please enter gross annual profit (in Rs.)");
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
                    bGrossAPErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(profitBusinessTxt);
                    bGrossAPErr.setVisibility(View.VISIBLE);
                    bGrossAPErr.setText("What was the profit you made last year?");
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
                    amountErr.setText("Please enter an valid loan amount.");
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
                    amountErr.setVisibility(View.VISIBLE);
                    amountErr.setError("Amount can not be Empty.");
                    amountTxt.requestFocus();
                    return;
                }
                Integer i = Integer.parseInt(s.toString());

                if(i < 25000 || i > 300000){
                    removeSuccess(amountTxt);
                    amountErr.setVisibility(View.VISIBLE);
                    amountErr.setText("Amount can not be less then 25000 or greater than 300000");
                    amountTxt.requestFocus();
                }else if(i%5000 != 0){
                    amountErr.setVisibility(View.VISIBLE);
                    amountErr.setText("Please enter amount in the multiples of 5000");
                    amountTxt.requestFocus();
                } else {
                    showSuccess(amountTxt);
                    amountErr.setVisibility(View.GONE);
                }
            }
        });

        descriptionTxt = findViewById(R.id.description);
        descriptionTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    descriptionErr.setVisibility(View.VISIBLE);
                    descriptionErr.setText("Please enter a loan description.");
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
                    descriptionErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(descriptionTxt);
                    descriptionErr.setVisibility(View.VISIBLE);
                    descriptionErr.setText("Please explain why you need this loan");
                    descriptionTxt.requestFocus();
                }
            }
        });

        companyTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    companyErr.setVisibility(View.VISIBLE);
                    companyErr.setText("Please enter your company name.");
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
                    companyErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(companyTxt);
                    companyErr.setVisibility(View.VISIBLE);
                    companyErr.setText("Please search and select a company.");
                    companyTxt.requestFocus();
                }
            }
        });

        workExperienceMonthTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    workEMErr.setVisibility(View.VISIBLE);
                    workEMErr.setText("Please enter experience in month.");
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
                    workEMErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(workExperienceMonthTxt);
                    workEMErr.setVisibility(View.GONE);
                    workEMErr.setText("Please tell us your work experience in month.");
                    workExperienceMonthTxt.requestFocus();
                }
            }
        });

        workExperienceTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    workEYErr.setVisibility(View.VISIBLE);
                    workEYErr.setText("Please enter experience in year.");
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
                    workEYErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(workExperienceTxt);
                    workEYErr.setVisibility(View.VISIBLE);
                    workEYErr.setError("Please tell us your work experience in year.");
                    workExperienceTxt.requestFocus();
                }
            }
        });

        workingSinceEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    workSinceErr.setVisibility(View.VISIBLE);
                    workSinceErr.setText("Please tell us the date on which you joined current organization.");
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
                    workSinceErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(workExperienceTxt);
                    workSinceErr.setVisibility(View.VISIBLE);
                    workSinceErr.setError("Please tell us the date on which you joined current organization.");
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
                    monthlyRentErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(monthlyRentTxt);
                    monthlyRentErr.setVisibility(View.VISIBLE);
                    monthlyRentErr.setText("What is your monthly rent amount");
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
                    sibilScoreErr.setText("Please enter credit score amount.");
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
                    otherMIErr.setText("Please enter other income.");
                } else {
                    otherMIErr.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().trim().length() != 0){
                    showSuccess(otherIncomeTxt);
                }else {
                    removeSuccess(otherIncomeTxt);
                }
            }
        });

        loanEmiTxt = findViewById(R.id.loanEmi);
        ccOutstandingTxt = findViewById(R.id.ccOutstanding);



        loanEmiTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    emiErr.setVisibility(View.VISIBLE);
                    emiErr.setText("Please enter the total EMI amount.");
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
                    emiErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(loanEmiTxt);
                    emiErr.setVisibility(View.VISIBLE);
                    emiErr.setText("Please enter the total EMI amount.");
                    loanEmiTxt.requestFocus();
                }
            }
        });

        ccOutstandingTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    ccAmountErr.setVisibility(View.VISIBLE);
                    ccAmountErr.setText("Please enter total outstanding amount.");
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
                    ccAmountErr.setVisibility(View.GONE);
                }else {
                    removeSuccess(ccOutstandingTxt);
                    ccAmountErr.setVisibility(View.VISIBLE);
                    ccAmountErr.setError("What is your existing Credit Card outstanding balance.");
                    ccOutstandingTxt.requestFocus();
                }
            }
        });

        dropdown = findViewById(R.id.spinner1);
        final String[] items = new String[]{ "Please select" ,"3", "6", "9" , "12", "15","18" ,"21","24"};
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
        final String[] itemsEmpType = new String[]{"Please select", "Salaried", "Self Employed", "Business" };
        ArrayAdapter<String> adapterEmpType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsEmpType);
        dropdownEmpType.setAdapter(adapterEmpType);

        spinnerPaymentMode = findViewById(R.id.spinnerPaymentMode);
        final String[] itemsPaymentMode = new String[]{"Please select", "Cash", "Cheque", "Credit to Bank Account" };
        adapterPaymentMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsPaymentMode);
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
        adapterHouseType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsHouseType);
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


                    adapterPurpose = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, purps);
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
                    pincodeErr.setText("Please provide your city pincode.");
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
                    pincodeErr.setVisibility(View.GONE);
                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            pincodeErr.setVisibility(View.GONE);
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
                            pincodeErr.setVisibility(View.VISIBLE);
                            pincodeErr.setText("Invalid Pincode");
                            pincodeEditTxt.requestFocus();
                        }
                    });


                }else{
                    pincodeErr.setVisibility(View.VISIBLE);
                    pincodeErr.setText("Invalid Pincode");
                    pincodeEditTxt.requestFocus();
                }
            }
        });

        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        client.get(backend.BASE_URL + "api/v1/retriveDetails/eligibility/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try {

                    String bloan_amount = c.getString("bloan_amount");
                    if (bloan_amount != null) {
                        double amountDouble = Double.parseDouble(bloan_amount);
                        int amountInt = (int) amountDouble;
                        amountTxt.setText(amountInt+"");
                    }
                    String bloan_tenure = c.getString("bloan_tenure");
                    for (int i=0; i<items.length; i++){
                        if (items[i].equals(bloan_tenure)){
                            dropdown.setSelection(i);
                        }
                    }

                    String bloan_purpose = c.getString("bloan_purpose");
                    if (bloan_purpose != null){
                        int purposePosition = Integer.parseInt(bloan_purpose);
                        dropdownPurpose.setSelection(purposePosition);
                    }

                    String bloan_desc = c.getString("bloan_desc");
                    descriptionTxt.setText(bloan_desc);

                    String dateOfBirth = c.getString("dateOfBirth");
                    long date = Long.parseLong(dateOfBirth);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String date1 = simpleDateFormat.format(new Date(date*1000));
//                    dobEditTxt.setText(getDate(date, "dd/MM/yyyy"));
                    dobEditTxt.setText(date1+"");

                    JSONObject localAddressObj = c.getJSONObject("localAddress");

                    String localAddressAddress = localAddressObj.getString("address");
                    String localAddressPincode = localAddressObj.getString("pincode");
                    pincodeEditTxt.setText(localAddressPincode);
                    String localAddressCity = localAddressObj.getString("city");
                    cityEditTxt.setText(localAddressCity);
                    String localAddressState = localAddressObj.getString("state");


                    String marriedStatus = c.getString("married");
                    if (marriedStatus.equals("M")){
                        married.setChecked(true);
                        spouseIncomeLayout.setVisibility(View.VISIBLE);
                        spouseIncomeLayout.setVisibility(View.VISIBLE);
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
                                    spouseErr.setText("Please enter spouse income.");
                                } else {
                                    spouseErr.setVisibility(View.GONE);
                                }
                            }
                        });
                        String spouse_income = c.getString("spouse_income");
                        spouseIncomeTxt.setText(spouse_income);
                    } else {
                        single.setChecked(true);
                        spouseIncomeLayout.setVisibility(View.GONE);
                    }



                    String residence_type = c.getString("residence_type");
                    if (residence_type != null){
                        if (residence_type.equals("rented") ){
                            spinnerHouseType.setSelection(1);
                            String rented_amount = c.getString("rented_amount");
                            monthlyRentTxt.setText(rented_amount);
                            String has_guarantor = c.getString("has_guarantor");
                            if (has_guarantor.equals("yes")){
                                gurantor_yes.setChecked(true);
                            } else {
                                gurantor_no.setChecked(true);
                            }
                            String stayingSince = c.getString("stayingSince");
                            String inputPattern = "MM-dd-yyyy";
                            String outputPattern = "dd/MM/yyyy";
                            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                            Date date2 = null;
                            String str = null;
                            try {
                                date2 = inputFormat.parse(stayingSince);
                                str = outputFormat.format(date2);
                                stayingSinceEditTxt.setText(str);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            String fin_own_home = c.getString("fin_own_home");
                            if (fin_own_home.equals("Yes")) {
                                spinnerHouseType.setSelection(2);
                            }else spinnerHouseType.setSelection(3);
                        }
                    }

                    String fin_own_home = c.getString("fin_own_home");
                    if (fin_own_home.equals("Yes")) {
                        spinnerHouseType.setSelection(2);
                    }
                    String loan_running = c.getString("loan_running");
                    if (loan_running.equals("yes")){
                        loan_yes.setChecked(true);
                        loanEmiLayout.setVisibility(View.VISIBLE);
                        String total_current_emi = c.getString("total_current_emi");
                        loanEmiTxt.setText(total_current_emi);
                    } else {
                        loan_no.setChecked(true);
                        loanEmiLayout.setVisibility(View.GONE);
                    }

                    String credit_card = c.getString("credit_card");
                    if (credit_card.equals("yes")){
                        cc_yes.setChecked(true);
                        ccOutstandingLayout.setVisibility(View.VISIBLE);
                        String cc_amt_outstanding = c.getString("cc_amt_outstanding");
                        ccOutstandingTxt.setText(cc_amt_outstanding);
                    } else {
                        ccOutstandingLayout.setVisibility(View.GONE);
                        cc_no.setChecked(true);
                    }

                    String other_monthly_income = c.getString("other_monthly_income");
                    otherIncomeTxt.setText(other_monthly_income);


                    String bloan_usr_cibil_score = c.getString("bloan_usr_cibil_score");
                    if (bloan_usr_cibil_score != null)
                        creditScoreTxt.setText(bloan_usr_cibil_score);

                    String empType = c.getString("empType");

//                    for (int i=0; i<itemsEmpType.length; i++) {
//
//                        if (itemsEmpType[i].equals(empType)) {
//                            dropdownEmpType.setSelection(i);
                    if (empType.equals("Salaried Employee")) {
                        dropdownEmpType.setSelection(1);
                        String cmp_name = c.getString("emp_comp_name");
                        companyTxt.setText(cmp_name);
                        String fin_monthly_salary = c.getString("fin_monthly_salary");
                        monthlyIncomeSalariedTxt.setText(fin_monthly_salary);
                        String fin_salary_mode = c.getString("fin_salary_mode");
                        for (int j=0; j<itemsPaymentMode.length; j++) {
                            if (itemsPaymentMode[j].equals(fin_salary_mode)) {
                                spinnerPaymentMode.setSelection(j);
                            }
                        }
//                        if (fin_salary_mode != null){
//                            int paymentModePosition = adapterPaymentMode.getPosition(fin_salary_mode);
//                            spinnerPaymentMode.setSelection(paymentModePosition);
//                        }
                        String emp_self_exp = c.getString("emp_self_exp");
                        String inputPattern = "MM-dd-yyyy";
                        String outputPattern = "dd/MM/yyyy";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                        Date empWorkDate = null;
                        String str1 = null;
                        try {
                            empWorkDate = inputFormat.parse(emp_self_exp);
                            str1 = outputFormat.format(empWorkDate);
                            stayingSinceEditTxt.setText(str1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        workingSinceEditTxt.setText(str1);
                        String emp_sal_work_year = c.getString("emp_sal_work_year");
                        workExperienceTxt.setText(emp_sal_work_year);
                        String emp_sal_work_month = c.getString("emp_sal_work_month");
                        workExperienceMonthTxt.setText(emp_sal_work_month);

                    } else if (empType.equals("Self Employed Professional")) {
                        dropdownEmpType.setSelection(2);
                        String em_self_profession = c.getString("em_self_profession");
                        professionTypeTxt.setText(em_self_profession);
                        String emp_self_exp = c.getString("established");
                        selfEmpExp.setText(emp_self_exp);
                        String fin_annual_turnover = c.getString("fin_annual_turnover");
                        turnoverSelfEmpTxt.setText(fin_annual_turnover);
                        String fin_income = c.getString("fin_income");
                        profitSelfEmpTxt.setText(fin_income);

                    } else if (empType.equals("Business")) {
                        dropdownEmpType.setSelection(3);
                        String established = c.getString("established");
                        businessFormEstablishedYr.setText(established);
                        String emp_bus_type = c.getString("emp_bus_type");
                        professionBusinessTxt.setText(emp_bus_type);
                        String fin_annual_turnover = c.getString("fin_annual_turnover");
                        turnoverBusinessTxt.setText(fin_annual_turnover);
                        String fin_income = c.getString("fin_income");
                        profitBusinessTxt.setText(fin_income);
                    }


                }catch (JSONException e) {

//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    Log.e("printStackTrace",""+e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
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
                String businessEY = businessFormEstablishedYr.getText().toString().trim();
                String bTurnover = turnoverBusinessTxt.getText().toString().trim();
                String bProfit = profitBusinessTxt.getText().toString().trim();
                String professionType = professionTypeTxt.getText().toString().trim();
                String professionalExp = selfEmpExp.getText().toString().trim();
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
                    descriptionErr.setText("Please enter a loan description.");
                } else {
                    descriptionErr.setVisibility(View.GONE);
                }
                if (dobStr.isEmpty()){
                    dobErr.setVisibility(View.VISIBLE);
                    dobErr.setText("Please enter your date of birth.");
                } else {
                    dobErr.setVisibility(View.GONE);
                }
                if (pincode.isEmpty()){
                    pincodeErr.setVisibility(View.VISIBLE);
                    pincodeErr.setText("Please provide your city pincode.");
                } else {
                    pincodeErr.setVisibility(View.GONE);
                }

                if (city.isEmpty()){
                    cityErr.setVisibility(View.VISIBLE);
                    cityErr.setText("Please provide the current city you live in.");
                } else {
                    cityErr.setVisibility(View.GONE);
                }

//                if (otherIncome.isEmpty()) {
//                    otherMIErr.setVisibility(View.VISIBLE);
//                    otherMIErr.setText("Please enter other income amount.");
//                } else {
//                    otherIncomeTxt.setVisibility(View.GONE);
//                }

//                if (creditScore.isEmpty()){
//                    sibilScoreErr.setVisibility(View.VISIBLE);
//                    sibilScoreErr.setText("Please enter credit score.");
//                } else {
//                    sibilScoreErr.setVisibility(View.GONE);
//                }

                if (single.isChecked() || married.isChecked()){
                    genderErr.setVisibility(View.GONE);
                    if (married.isChecked()){
                        if (spouseIncome.isEmpty()){
                            spouseErr.setVisibility(View.VISIBLE);
                            spouseErr.setText("Please enter spouse income.");
                        } else {
                            spouseErr.setVisibility(View.GONE);
                        }
                    } else if (single.isChecked()){
                            spouseErr.setVisibility(View.GONE);
                    }
                }else {
                    genderErr.setVisibility(View.VISIBLE);
                    genderErr.setText("Please select your marital status.");
                }

                if (loan_yes.isChecked() || loan_no.isChecked()){
                    loanRadioErr.setVisibility(View.GONE);
                    if (loan_yes.isChecked()) {
                        if (loanEmi.isEmpty()) {
                            emiErr.setVisibility(View.VISIBLE);
                            emiErr.setText("Please enter the total EMI amount.");
                        } else {
                            emiErr.setVisibility(View.GONE);
                        }
                    }
                }else {
                    loanRadioErr.setVisibility(View.VISIBLE);
                    loanRadioErr.setText("Please select one of option above.");
                }

                if (cc_yes.isChecked() || cc_no.isChecked()){
                    ccRadioErr.setVisibility(View.GONE);
                    if (cc_yes.isChecked())
                        if (ccOutstanding.isEmpty()){
                            ccAmountErr.setVisibility(View.VISIBLE);
                            ccAmountErr.setText("Please enter total outstanding amount.");
                        } else {
                            ccAmountErr.setVisibility(View.GONE);
                        }
                }else {
                    ccRadioErr.setVisibility(View.VISIBLE);
                    ccRadioErr.setText("Please select one of option above.");
                }


                if (dropdownPurpose.getSelectedItemPosition() != 0){
                    purposeErr.setVisibility(View.GONE);
                }else {
                    purposeErr.setVisibility(View.VISIBLE);
                    purposeErr.setText("Please select your loan purpose.");
                }

                if (spinnerHouseType.getSelectedItemPosition() != 0){
                    houseTypeErr.setVisibility(View.GONE);
                    if (spinnerHouseType.getSelectedItemPosition() == 1) {
                        if (monthlyRent_et.isEmpty()) {
                            monthlyRentErr.setVisibility(View.VISIBLE);
                            monthlyRentErr.setText("Please provide your monthly rent.");
                        } else {
                            monthlyRentErr.setVisibility(View.GONE);
                        }
                        if (gurantor_yes.isChecked() || gurantor_no.isChecked()) {
                            gurantorRadiodErr.setVisibility(View.GONE);
                        } else {
                            gurantorRadiodErr.setVisibility(View.VISIBLE);
                            gurantorRadiodErr.setText("Please select one option.");
                        }
                        if (stayingSince_et.isEmpty()){
                            stayingSinceErr.setVisibility(View.VISIBLE);
                            stayingSinceErr.setText("Please select the date.");
                        } else {
                            sibilScoreErr.setVisibility(View.GONE);
                        }

                    }
                }else {
                    houseTypeErr.setVisibility(View.VISIBLE);
                    houseTypeErr.setText("Please select at least one residence type.");
                }


                if (dropdownEmpType.getSelectedItemPosition() != 0){
                    empTypeErr.setVisibility(View.GONE);
                    if (dropdownEmpType.getSelectedItemPosition() == 1) {
                        if (company.isEmpty()) {
                            companyErr.setVisibility(View.VISIBLE);
                            companyErr.setText("Please enter your company name.");
                        } else {
                            companyErr.setVisibility(View.GONE);
                        }

                        if (miSalary.isEmpty()) {
                            monthlyIncomeErr.setVisibility(View.VISIBLE);
                            monthlyIncomeErr.setText("Please provide your monthly salary (in Rs.).");
                        } else {
                            monthlyIncomeErr.setVisibility(View.GONE);
                        }

                        if (spinnerPaymentMode.getSelectedItemPosition() != 0){
                            salaryErr.setVisibility(View.GONE);
                        }else {
                            salaryErr.setVisibility(View.VISIBLE);
                            salaryErr.setText("Please select the salary mode.");
                        }

                        if (workingSince_et.isEmpty()){
                            workSinceErr.setVisibility(View.VISIBLE);
                            workSinceErr.setText("Please tell us the date on which you joined current organization.");
                        } else {
                            workSinceErr.setVisibility(View.GONE);
                        }

                        if (workingExperience.isEmpty()){
                            workEYErr.setVisibility(View.VISIBLE);
                            workEYErr.setText("Please enter work experience in year.");
                        } else {
                            workEYErr.setVisibility(View.GONE);
                        }

                        if (workEM.isEmpty()){
                            workEMErr.setVisibility(View.VISIBLE);
                            workEMErr.setText("Please enter work experience in month.");
                        } else {
                            workEMErr.setVisibility(View.GONE);
                        }

                    } else if (dropdownEmpType.getSelectedItemPosition() == 2){
                        if (professionType.isEmpty()){
                            professionTypeErr.setVisibility(View.VISIBLE);
                            professionTypeErr.setText("What is your profession type ?");
                        } else {
                            professionTypeErr.setVisibility(View.GONE);
                        }

                        if (professionalExp.isEmpty()){
                            totalProfessionEYrErr.setVisibility(View.VISIBLE);
                            totalProfessionEYrErr.setText("Please provide your total professional experience.");
                        } else {
                            totalProfessionEYrErr.setVisibility(View.GONE);
                        }

                        if (sTurnover.isEmpty()){
                            pGrossATOErr.setVisibility(View.VISIBLE);
                            pGrossATOErr.setText("Please enter your annual turnover (in Rs.).");
                        } else {
                            pGrossATOErr.setVisibility(View.GONE);
                        }
                        if (sProfit.isEmpty()){
                            pGrossAPErr.setVisibility(View.VISIBLE);
                            pGrossAPErr.setText("Please enter gross annual profit (in Rs.).");
                        } else {
                            pGrossAPErr.setVisibility(View.GONE);
                        }
                    }else if (dropdownEmpType.getSelectedItemPosition() == 3){
                        if (businessType_et.isEmpty()){
                            businessTypeErr.setVisibility(View.VISIBLE);
                            businessTypeErr.setText("Please provide type of your business ?");
                        } else {
                            businessTypeErr.setVisibility(View.GONE);
                        }
                        if (businessEY.isEmpty()){
                            businessEYrErr.setVisibility(View.VISIBLE);
                            businessEYrErr.setText("Please provide establishment year of your business.");
                        } else {
                            businessEYrErr.setVisibility(View.GONE);
                        }
                        if (bTurnover.isEmpty()){
                            bGrossATOErr.setVisibility(View.VISIBLE);
                            bGrossATOErr.setText("Please enter your annual turnover (in Rs.).");
                        } else {
                            bGrossATOErr.setVisibility(View.GONE);
                        }
                        if (bProfit.isEmpty()){
                            bGrossAPErr.setVisibility(View.VISIBLE);
                            bGrossAPErr.setText("Please enter gross annual profit (in Rs.).");
                        } else {
                            bGrossAPErr.setVisibility(View.GONE);
                        }
                    } else Log.d("Emptype","=== null");
                }else {
                    empTypeErr.setVisibility(View.VISIBLE);
                    empTypeErr.setText("Please select your employment type.");
                }

                if (dropdown.getSelectedItemPosition() != 0){
                    tenureLoneErr.setVisibility(View.GONE);
                }else {
                    tenureLoneErr.setVisibility(View.VISIBLE);
                    tenureLoneErr.setText("Please select loan tenure.");
                }

                Integer desiredAmount = 0;
                try{
                    desiredAmount = Integer.parseInt(amountTxt.getText().toString());
                }catch(Exception e){

                }

                if(desiredAmount < 25000 || desiredAmount > 300000){
                    Toast.makeText(RegistrationCheckEligibility.this, "Amount can not be less then 25000 or greater than 300000", Toast.LENGTH_SHORT).show();
                    amountTxt.requestFocus();
                    return;
                }else if(desiredAmount % 5000 != 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please enter amount in the multiples of 5000", Toast.LENGTH_SHORT).show();
                    amountTxt.requestFocus();
                    return;
                }

                String tenure = dropdown.getSelectedItem().toString();
                if (tenure.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select loan tenure.", Toast.LENGTH_SHORT).show();
                    dropdown.requestFocus();
                    return;
                }
                Integer purpose = dropdownPurpose.getSelectedItemPosition();
                if (purpose.equals(0)){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your loan purpose.", Toast.LENGTH_SHORT).show();
                    dropdownPurpose.requestFocus();
                    return;
                }
//                String description = descriptionTxt.getText().toString();

                if (description.length()==0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please enter a loan description.", Toast.LENGTH_SHORT).show();
                    descriptionTxt.requestFocus();
                    return;
                }

                RadioButton radioMarried = findViewById(R.id.radio_married);
                RadioButton radioSingle = findViewById(R.id.radio_single);
                Boolean married = false;


                Integer spouseIncomeRate = 0;
                if (radioSingle.isChecked()){
                    married = false;
                }else if (radioMarried.isChecked()){
                    married = true;
                    try{
                        spouseIncomeRate = Integer.parseInt(spouseIncome);
                    }catch (Exception e){

                    }
                    if (spouseIncomeRate.equals(0)){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide spouse income.", Toast.LENGTH_SHORT).show();
                        spouseIncomeTxt.requestFocus();
                        return;
                    }
                }else{
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your marital status.", Toast.LENGTH_SHORT).show();
                    radioSingle.requestFocus();
                    return;
                }

                if (dobStr.length() == 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please enter your date of birth.", Toast.LENGTH_SHORT).show();
                    dobEditTxt.requestFocus();
                    return;
                }

                Date dob = strToDate(dobStr);
//                String inputPattern = "dd-MM-yyyy";
//                String outputPattern = "dd/MM/yyyy";
//                SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
//                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
//
//                Date date2 = null;
//                String str = null;
//                try {
//                    date2 = inputFormat.parse(dobStr);
//                    str = outputFormat.format(date2);
////                    stayingSinceEditTxt.setText(str);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }


                if (pincode.length() == 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please provide your city pincode.", Toast.LENGTH_SHORT).show();
                    pincodeEditTxt.requestFocus();
                    return;
                }

                if (city.length() == 0){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please provide the current city you live in.", Toast.LENGTH_SHORT).show();
                    cityEditTxt.requestFocus();
                    return;
                }

                String state = addressState;

                String empType = dropdownEmpType.getSelectedItem().toString();

                if (empType.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your employment type.", Toast.LENGTH_SHORT).show();
                    dropdownEmpType.requestFocus();
                    return;
                }

                String businessType  = "";
                Integer establishedYr = 0;
                Integer grossTurnover = 0;
                Integer grossAnnualProfit = 0;
                Integer tProfessionalExp = 0;

                if (empType == "Business"){
                    businessType = professionBusinessTxt.getText().toString();

                    if (businessType.length()==0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business type", Toast.LENGTH_SHORT).show();
                        professionBusinessTxt.requestFocus();
                        return;
                    }
                    try{
                        establishedYr = Integer.parseInt(businessEY);
                        grossTurnover = Integer.parseInt(bTurnover);
                        grossAnnualProfit = Integer.parseInt(bProfit);
                    }catch ( Exception e){
                        e.printStackTrace();
                    }

                    if (businessEY.length() <= 3) {
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your valid business established year", Toast.LENGTH_SHORT).show();
                        businessFormEstablishedYr.requestFocus();
                        return;
                    } else
                        if (!(1800 <= establishedYr && establishedYr <= year)) {
                            Toast.makeText(RegistrationCheckEligibility.this, "Please mention your valid business established year", Toast.LENGTH_SHORT).show();
                            businessFormEstablishedYr.requestFocus();
                            return;
                        }
                    if (grossTurnover == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        turnoverBusinessTxt.requestFocus();
                        return;
                    }

                    if (grossAnnualProfit == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        profitBusinessTxt.requestFocus();
                        return;
                    }
                }

                if (empType == "Self Employed"){
                    businessType = professionTypeTxt.getText().toString();
                    if (businessType.length()==0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business type", Toast.LENGTH_SHORT).show();
                        professionTypeTxt.requestFocus();
                        return;
                    }
                    try{
                        tProfessionalExp = Integer.parseInt(professionalExp);
                        grossTurnover = Integer.parseInt(sTurnover);
                        grossAnnualProfit = Integer.parseInt(sProfit);
                    }catch ( Exception e){

                    }

                    if (tProfessionalExp == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your total professional experience", Toast.LENGTH_SHORT).show();
                        selfEmpExp.requestFocus();
                        return;
                    }

                    if (grossTurnover == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        turnoverSelfEmpTxt.requestFocus();
                        return;
                    }

                    if (grossAnnualProfit == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please mention your business gross turnover", Toast.LENGTH_SHORT).show();
                        profitSelfEmpTxt.requestFocus();
                        return;
                    }

                }

                String expYear="0";
                String expMonth="0";
                Date workingSince = null;
                Integer monthlyIncome = null;
                String compName = "";
                String empPaymentType = "";
                if (empType == "Salaried"){
                    compName = companyTxt.getText().toString();

                    if (compName.length()== 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please enter your company name.", Toast.LENGTH_SHORT).show();
                        companyTxt.requestFocus();
                        return;
                    }

//                    if (compName.length()== 0){
//                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your company name", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    String incomeSalTxt = incomeSalariedTxt.getText().toString();
                    if (incomeSalTxt.length()== 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your monthly salary (in Rs.)", Toast.LENGTH_SHORT).show();
                        incomeSalariedTxt.requestFocus();
                        return;
                    }
                    try{
                        monthlyIncome = Integer.parseInt(incomeSalTxt);
                    }catch (Exception e){

                    }

                    empPaymentType = spinnerPaymentMode.getSelectedItem().toString();
                    if (empPaymentType.equals("Please select")){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please select the salary mode.", Toast.LENGTH_SHORT).show();
                        spinnerPaymentMode.requestFocus();
                        return;
                    }
                    String wrkingSinceTxt = workingSinceEditTxt.getText().toString();

                    if (wrkingSinceTxt.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please tell us the date on which you joined current organization.", Toast.LENGTH_SHORT).show();
                        workingSinceEditTxt.requestFocus();
                        return;
                    }
                    workingSince = strToDate(wrkingSinceTxt);
                    expYear = workExperienceTxt.getText().toString();
                    expMonth = workExperienceMonthTxt.getText().toString();

                    if (expYear.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your experience", Toast.LENGTH_SHORT).show();
                        workExperienceTxt.requestFocus();
                        return;
                    }

                    if (expMonth.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your experience", Toast.LENGTH_SHORT).show();
                        workExperienceMonthTxt.requestFocus();
                        return;
                    }
                }

                String houseType = spinnerHouseType.getSelectedItem().toString();
                if (houseType.equals("Please select")){
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select your residence type", Toast.LENGTH_SHORT).show();
                    spinnerHouseType.requestFocus();
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
                        loanEmiTxt.requestFocus();
                        return;
                    }

                }else{
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select if you have any loan running", Toast.LENGTH_SHORT).show();
                    noLoan.requestFocus();
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
                        ccOutstandingTxt.requestFocus();
                        return;
                    }
                } else if (noCC.isChecked()){
                    creditCard = false;

                }else{
                    Toast.makeText(RegistrationCheckEligibility.this, "Please select if you have any credit card", Toast.LENGTH_SHORT).show();
                    yesCC.requestFocus();
                    return;
                }

                Integer cibilScore = 0;
                try{
                    cibilScore = Integer.parseInt(creditScoreTxt.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
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
                        haveGurantee.requestFocus();
                        return;
                    }

                    monthlyRent = monthlyRentTxt.getText().toString();
                    if (monthlyRent.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please provide your monthly rent", Toast.LENGTH_SHORT).show();
                        monthlyRentTxt.requestFocus();
                        return;
                    }
                    String stayingSinceEdtTxt = stayingSinceEditTxt.getText().toString();
                    if (stayingSinceEdtTxt.length() == 0){
                        Toast.makeText(RegistrationCheckEligibility.this, "Please select a date since you are living at current residence", Toast.LENGTH_SHORT).show();
                        stayingSinceEditTxt.requestFocus();
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
                    e.printStackTrace();
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
                        employementDetails.put("fin_salary_mode" , empPaymentType);
                        employementDetails.put("workingSince" , workingSince);
                        employementDetails.put("expYear" , expYear);
                        employementDetails.put("expMonth" , expMonth);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else if (empType == "Self Employed"){
                    try{
                        employementDetails.put("established" , tProfessionalExp);
                        employementDetails.put("grossTurnover" , grossTurnover);
                        employementDetails.put("grossAnnualProfit" , grossAnnualProfit);
                        employementDetails.put("type" , businessType);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                } else if (empType == "Business"){
                    try{
                        employementDetails.put("established" , establishedYr);
                        employementDetails.put("grossTurnover" , grossTurnover);
                        employementDetails.put("grossAnnualProfit" , grossAnnualProfit);
                        employementDetails.put("type" , businessType);
                    }catch (JSONException e){
                        e.printStackTrace();
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
                    e.printStackTrace();
                }

                try{
                    jsonParams.put("loanDetails" , loanDetails);
                    jsonParams.put("personalDetails" , personalDetails);
                    jsonParams.put("empType" , empType);
                    jsonParams.put("employementDetails" , employementDetails);
                    jsonParams.put("financialDetails" , financialDetails);
                }catch (JSONException e){
                    e.printStackTrace();
                }

                StringEntity entity = null;

                try{
                    entity = new StringEntity(jsonParams.toString());
                }catch(Exception e){

                }

//                sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

                String session_id = sharedPreferences.getString("session_id" , null);
                String csrf_token = sharedPreferences.getString("csrf_token" , null);

                client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/checkEligibility/?csrf_token=" + csrf_token + "&session_id=" + session_id , entity , "application/json", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Boolean eligible = false;
                        String chances = "high";
                        int baseInterest = 0;
                        try {
                            eligible = response.getBoolean("eligible");
                            chances = response.getString("chances");
                            baseInterest = response.getInt("interest");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        String msg = "";
                        Integer icon = null;
//                        String first = "Congratulations, You are eligible \n";
//                        String next = "<font color='#0000EE'>Expected Interest Rate</font>";
//                        String third = "";
                        if (eligible){
                            if (baseInterest >= 18 && baseInterest <= 24){
                                icon = R.drawable.meter_high;
                                chances = "high";
                                msg = "Congratulations, You are eligible \n" + "Expected Interest Rate \n \n" + (baseInterest-2) +"% to " + (baseInterest+2)+"%";
                            } else if (baseInterest >= 25 && baseInterest <= 30){
                                icon = R.drawable.meter_medium;
                                chances = "medium";
                                msg = "Congratulations, You are eligible\n" + "Expected Interest Rate \n \n"+ (baseInterest-2) +"% to " + (baseInterest+2)+"%";
                            } else if (baseInterest > 30){
                                icon = R.drawable.thumsup_icon;
                                chances = "low";
                                msg = "Congratulations, You are eligible \n" + "Expected Interest Rate \n \n" + (baseInterest-2) +"% to " + (baseInterest+2)+"%";
                            }
                        } else {
                            msg = "Sorry, You are not eligible";
                            icon = R.drawable.thumbsdown_icon;
                        }

                        final LovelyStandardDialog dialog = new LovelyStandardDialog(RegistrationCheckEligibility.this);
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
                                .setNegativeButton(android.R.string.no, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                })
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
//        generateHashkey();
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
        businessEYrErr = findViewById(R.id.businessEYrErrTxt);
        bGrossATOErr = findViewById(R.id.turnoverBusinessErrTxt);
        bGrossAPErr = findViewById(R.id.profitBusinessErrTxt);
        professionTypeErr= findViewById(R.id.professionSelfEmpErrTxt);
        totalProfessionEYrErr= findViewById(R.id.tpeYrErrTxt);
        pGrossATOErr = findViewById(R.id.turnoverSelfEmpErrTxt);
        pGrossAPErr = findViewById(R.id.profitSelfEmpErrTxt);
        houseTypeErr = findViewById(R.id.houseTypeErrTxt);
        monthlyRentErr = findViewById(R.id.monthlyRentErrTxt);
        gurantorRadiodErr = findViewById(R.id.canProvideGuranteeErrTxt);
        stayingSinceErr = findViewById(R.id.stayingSinceErrTxt);
        otherMIErr = findViewById(R.id.otherIncomeErrTxt);
        spouseErr = findViewById(R.id.spouseIncomeErrTxt);
        spouseErr.setVisibility(View.GONE);
        loanRadioErr = findViewById(R.id.radioLoanErrTxt);
        emiErr = findViewById(R.id.loanEmiErrTxt);
        ccRadioErr = findViewById(R.id.radioCCErrTxt);
        ccAmountErr = findViewById(R.id.ccOutstandingErrTxt);
        sibilScoreErr = findViewById(R.id.creditScoreErrTxt);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.e("HashKey==---==--=",Base64.encodeToString(md.digest(),
                                Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }
    }

    public void loginLinkedIn(View view){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
//                imgLogin.setVisibility(View.GONE);
//                btnLogout.setVisibility(View.VISIBLE);
//                imgProfile.setVisibility(View.VISIBLE);
                tv_linkined_connect.setVisibility(View.VISIBLE);
                fetchPersonalInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.e("LinkedIn",error.toString());
            }
        }, true);
    }

    // set the permission to retrieve basic -
    //information of User's linkedIn account
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE,
                Scope.R_EMAILADDRESS);
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
        if(callbackManager!=null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }else {
            LISessionManager.getInstance(getApplicationContext())
                    .onActivityResult(this,
                            requestCode, resultCode, data);
            tv_linkined_connect.setVisibility(View.VISIBLE);
            tv_linkined_connect.setText("Connected");
        }
    }
    private void fetchPersonalInfo(){
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                try {
                    JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                    String firstName = jsonObject.getString("firstName");
                    String lastName = jsonObject.getString("lastName");
//                    String pictureUrl = jsonObject.getString("pictureUrl");
                    String emailAddress = jsonObject.getString("emailAddress");

//                    Picasso.with(getApplicationContext()).load(pictureUrl).into(imgProfile);

                    StringBuilder sb = new StringBuilder();
                    sb.append("First Name: "+firstName);
                    sb.append("\n\n");
                    sb.append("Last Name: "+lastName);
                    sb.append("\n\n");
                    sb.append("Email: "+emailAddress);
                    tv_linkined_connect.setText(sb);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                Log.e("Linkedin",liApiError.getMessage());
            }
        });
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
                    ccAmountErr.setVisibility(View.GONE);
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
                    emiErr.setVisibility(View.GONE);
                    loanEmiLayout.setVisibility(LinearLayout.GONE);
                    creditScoreTxt.requestFocus();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

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
