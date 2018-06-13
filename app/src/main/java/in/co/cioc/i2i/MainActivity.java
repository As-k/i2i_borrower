package in.co.cioc.i2i;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rilixtech.CountryCodePicker;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import droidninja.filepicker.FilePickerBuilder;


public class MainActivity extends AppCompatActivity {
    EditText mPasswordView;
    EditText rePasswordView;
    EditText fName;
    EditText mName;
    EditText lName;
    EditText adhar;
    EditText pan;
    EditText email;
    EditText mobile;
    CountryCodePicker ccp;

    private EditText mobileOTPEdit;
    private EditText emailOTPEdit;
    RadioButton male;
    SharedPreferences sharedPreferences;
    RadioButton feMale;
    LinearLayout btnLayout , otpMasterLayout;
    Drawable successTick;

    Registration r;
    Backend backend;
    private CheckBox tncCB;
    private CheckBox personalCB;
    TextView agreeTxt , authorizeTxt;
    TextView otpView;


    TextView genderErr, fnameErr, lnameErr, aadharErr, panErr, emailErr, mobileErr, passwordErr, password2Err, otpmobileErr, otpemailErr, tncCBErr, personalCBErr;

    RequestParams requestParams = new RequestParams();
    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    String jsonResponse , mobileOtp , emailOtp;

    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String password_pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,14}$";
    String f_name, l_name, m_name, p_email, p_adhar, p_pan, password, p_mobile, repassword;
    Button register_button;
    boolean fnameValid, lnameValid, panValid, aadharValid, mobileValid, emailValid, passwordValid, repasswordValid, emailOTPValid, mobOTPValid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        backend = new Backend();
        otpView = findViewById(R.id.otpView);

        Toast.makeText(this, backend.BASE_URL, Toast.LENGTH_LONG).show();

//        StepView mStepView = (StepView) findViewById(R.id.step_view);
//        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
//        mStepView.setSteps(steps);
//
//        mStepView.selectedStep(1);
        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        otpMasterLayout = findViewById(R.id.otpMasterLayout);
//        otpMasterLayout.setVisibility(LinearLayout.GONE);
        mobileOTPEdit = findViewById(R.id.mobileOTP);
        emailOTPEdit = findViewById(R.id.emailOTP);


        fName = findViewById(R.id.firstName);
        mName = findViewById(R.id.middleName);
        lName = findViewById(R.id.lastName);
        adhar = findViewById(R.id.aadhar);
        pan = findViewById(R.id.pan);
        pan.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        email = findViewById(R.id.email);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        mobile = findViewById(R.id.mobile);

        mPasswordView = findViewById(R.id.password);
        rePasswordView = findViewById(R.id.rePassword);

        male = findViewById(R.id.radio_male);
        feMale = findViewById(R.id.radio_female);

        genderErr = findViewById(R.id.genderErrTxt);
        fnameErr = findViewById(R.id.fnameErrTxt);
        lnameErr = findViewById(R.id.lnameErrTxt);
        aadharErr = findViewById(R.id.aadharErrTxt);
        panErr = findViewById(R.id.panErrTxt);
        emailErr = findViewById(R.id.emailErrTxt);
        mobileErr = findViewById(R.id.mobileErrTxt);
        otpmobileErr = findViewById(R.id.mobileOTPErrTxt);
        otpemailErr = findViewById(R.id.emailOTPErrTxt);
        tncCBErr = findViewById(R.id.tncCheckboxErrTxt);
        personalCBErr = findViewById(R.id.personalInfoCheckBoxErrTxt);


        isStoragePermissionGranted();


        View focusView = null;

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );


        pattern = Pattern.compile(EMAIL_PATTERN);
        final Drawable img = this.getResources().getDrawable( R.drawable.user_icon);
        fName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    fnameErr.setVisibility(View.VISIBLE);
                    fnameErr.setText("Please enter first name.");
                    textChange(fName);
                    fnameValid = false;
                } else{
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
                    showSuccess(fName);
                    fnameValid = true;
                }else {
                    removeSuccess(fName);
                    fnameValid = false;
                }
            }
        });

        lName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().equals("")){
                    lnameErr.setVisibility(View.VISIBLE);
                    lnameErr.setText("Please enter last name.");
                    textChange(lName);
                    lnameValid = false;
                } else{
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
                    showSuccess(lName);
                    lnameValid = true;
                }else {
                    removeSuccess(lName);
                    lnameValid = false;
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
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                panCardValidation(s.toString().trim());
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {


            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailIdValidation(s.toString().trim());
            }
        });

        adhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aadharCardValidation(s.toString().trim());
            }
        });



        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mobileNoValidation(s.toString().trim());
            }
        });

        passwordErr = findViewById(R.id.passwordErrTxt);
        password2Err = findViewById(R.id.password2ErrTxt);

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordValidation(s.toString().trim());
            }
        });

        rePasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")) {
                    password2Err.setVisibility(View.VISIBLE);
                    textChange(rePasswordView);
                    password2Err.setText("Confirm Password does not match.");
                    repasswordValid = false;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = mPasswordView.getText().toString();

                if(s.toString().equals(pass)){
                    showSuccess(rePasswordView);
                    getOtp();
                    password2Err.setVisibility(View.GONE);
                    repasswordValid = true;
                } else {
                    repasswordValid = false;
                    password2Err.setVisibility(View.VISIBLE);
                    textChange(rePasswordView);
                    password2Err.setText("Confirm Password does not match.");
                }
            }
        });


        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailOTPEdit.setText("");
                mobileOTPEdit.setText("");
                f_name = fName.getText().toString().trim();
                l_name = lName.getText().toString().trim();
                m_name = mName.getText().toString().trim();
                p_email = email.getText().toString().trim();
                p_mobile = mobile.getText().toString().trim();
                p_adhar = adhar.getText().toString().trim();
                p_pan = pan.getText().toString().trim();
                password = mPasswordView.getText().toString().trim();
                repassword = rePasswordView.getText().toString().trim();


                if (password.isEmpty()){
                    passwordErr.setVisibility(View.VISIBLE);
                    passwordErr.setText("Please enter Password.");
                    mPasswordView.requestFocus();
                } else {
                    passwordErr.setVisibility(View.GONE);
                    if (repassword.isEmpty()){
                        password2Err.setVisibility(View.VISIBLE);
                        password2Err.setText("Confirm Password does not match.");
                        rePasswordView.requestFocus();
                    } else {
                        password2Err.setVisibility(View.GONE);
                    }
                }

                if (p_mobile.isEmpty()){
                    mobileErr.setVisibility(View.VISIBLE);
                    mobileErr.setText("Please enter your mobile number.");
                    mobile.requestFocus();
                } else {
                    mobileErr.setVisibility(View.GONE);
                }

                if (p_email.isEmpty()){
                    emailErr.setVisibility(View.VISIBLE);
                    emailErr.setText("Please enter your Email-Id.");
                    email.requestFocus();
                } else {
                    emailErr.setVisibility(View.GONE);
                }

                if (p_pan.isEmpty()){
                    panErr.setVisibility(View.VISIBLE);
                    panErr.setText("Please enter valid PAN number.");
                    pan.requestFocus();
                } else {
                    panErr.setVisibility(View.GONE);
                }

                if (p_adhar.isEmpty()){
                    aadharErr.setVisibility(View.VISIBLE);
                    aadharErr.setText("Please enter valid Aadhaar no.");
                    adhar.requestFocus();
                } else {
                    aadharErr.setVisibility(View.GONE);
                }

                passwordValidation(password);
                mobileNoValidation(p_mobile);
                emailIdValidation(p_email);
                panCardValidation(p_pan);
                aadharCardValidation(p_adhar);

                if (l_name.isEmpty()){
                    lnameErr.setVisibility(View.VISIBLE);
                    lnameErr.setText("Please enter last name.");
                    lName.requestFocus();
                } else {
                    lnameErr.setVisibility(View.GONE);
                }

                if (f_name.isEmpty()){
                    fnameErr.setVisibility(View.VISIBLE);
                    fnameErr.setText("Please enter first name.");
                    fName.requestFocus();
                } else {
                    fnameErr.setVisibility(View.GONE);
                }

                if (male.isChecked() || feMale.isChecked()){
                    genderErr.setVisibility(View.GONE);
                }else {
                    genderErr.setVisibility(View.VISIBLE);
                    genderErr.setText("Please choose gender.");
                    Toast.makeText(MainActivity.this, "Please choose gender.", Toast.LENGTH_SHORT).show();
                }



                if (!fnameValid){
                    Toast.makeText(MainActivity.this, "Please enter first name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!lnameValid){
                    Toast.makeText(MainActivity.this, "Please enter last name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!aadharValid){
                    Toast.makeText(MainActivity.this, "Please enter valid Aadhaar no.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!panValid){
                    Toast.makeText(MainActivity.this, "Please enter valid PAN card number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailValid){
                    Toast.makeText(MainActivity.this, "Please enter your Email-Id", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mobileValid){
                    Toast.makeText(MainActivity.this, "Please enter your mobile no.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordValid){
                    Toast.makeText(MainActivity.this, "Please enter Password.", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (repassword.equals(password)) {
                    password2Err.setVisibility(View.GONE);
                    RequestParams params = new RequestParams();

                    params.put("firstName", fName.getText().toString());
                    params.put("middleName", mName.getText().toString());
                    params.put("lastName", lName.getText().toString());

                    if (male.isChecked()) {
                        params.put("gender", "M");
                    } else if (feMale.isChecked()) {
                        params.put("gender", "F");
                    } else {
                        return;
                    }

                    params.put("aadhar", adhar.getText().toString().trim());
                    params.put("panNumber", pan.getText().toString().trim());
                    params.put("emailID", email.getText().toString().trim());
                    params.put("password", mPasswordView.getText().toString().trim());
                    params.put("password2", rePasswordView.getText().toString().trim());
                    params.put("mobileNum", mobile.getText().toString().trim());
                    params.put("mobileCode", ccp.getSelectedCountryCode());
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
                            } catch (JSONException err) {
                            }

                            if (otpSent) {

                                try {
                                    mobileOtp = Integer.toString(c.getInt("mobileOTP"));
                                    emailOtp = Integer.toString(c.getInt("emailOTP"));

                                    otpView.setText(mobileOtp + ":" + emailOtp);

                                } catch (JSONException e) {

                                }

//                                otpMasterLayout.setVisibility(LinearLayout.VISIBLE);
                                register_button.setText("√ Sent (Resend ?)");
                                emailOTPEdit.setText("");
                                mobileOTPEdit.setText("");
//                            Intent i = new Intent(getApplicationContext(), RegistrationOTP.class);
//                            i.putExtras(bundle);
//                            startActivity(i);
//                            register_button.setText("Resend");
                            }

                        }

                    });
                } else {
                    password2Err.setVisibility(View.VISIBLE);
                    password2Err.setText("Confirm Password does not match.");
                    rePasswordView.requestFocus();
                }

            }
        });

        // related to OTP

        tncCB = findViewById(R.id.tncCheckbox);
        personalCB = findViewById(R.id.personalInfoCheckBox);
        authorizeTxt = findViewById(R.id.authorizetxt);
        agreeTxt = findViewById(R.id.agreetxt);


        ClickableSpan termsAndConditions = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.i2ifunding.com/terms-conditions")));
                view.invalidate(); // need put invalidate here to make text change to GREEN after clicked
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                if (authorizeTxt.isPressed() && authorizeTxt.getSelectionStart() != -1 && authorizeTxt.getText()
                        .toString()
                        .substring(authorizeTxt.getSelectionStart(), authorizeTxt.getSelectionEnd())
                        .equals("Terms and Conditions")) {
                    authorizeTxt.invalidate();
                    ds.setColor(Color.rgb(200,50,50)); // need put invalidate here to make text change to RED when pressed on Highlight Link
                } else {
                    ds.setColor(getResources().getColor(R.color.orange));
                }
                // dont put invalidate here because if you put invalidate here `updateDrawState` will called forever
            }
        };

        ClickableSpan privacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.i2ifunding.com/privacy-policy")));
                view.invalidate(); // need put invalidate here to make text change to GREEN after clicked
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                if (agreeTxt.isPressed() && agreeTxt.getSelectionStart() != -1 && agreeTxt.getText()
                        .toString()
                        .substring(agreeTxt.getSelectionStart(), agreeTxt.getSelectionEnd())
                        .equals("Privacy Policy")) {
                    agreeTxt.invalidate();
                    ds.setColor(Color.rgb(200,50,50)); // need put invalidate here to make text change to RED when pressed on Highlight Link
                } else {
                    ds.setColor(getResources().getColor(R.color.orange));
                }
                // dont put invalidate here because if you put invalidate here `updateDrawState` will called forever
            }
        };

        makeLinks(authorizeTxt, new String[] {
                "Terms and Conditions"
        }, new ClickableSpan[] {
                termsAndConditions
        });

        makeLinks(agreeTxt, new String[] {
                "Privacy Policy"
        }, new ClickableSpan[] {
                privacyPolicy
        });

        RequestParams requestParams = new RequestParams();



        emailOTPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailOTPValidation(s.toString().trim());
            }
        });

        mobileOTPEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,  int before, int count) {
                mobileOTPValidation(s.toString().trim());
            }
        });

        Button submit_btn = findViewById(R.id.submit_button);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                mobileOTP	884214
//                emailOTP	306787
//                emailID	dasdas@sdsf.com
                String mob_otp = mobileOTPEdit.getText().toString().trim();
                String email_otp = emailOTPEdit.getText().toString().trim();
                p_email = email.getText().toString().trim();
                p_mobile = mobile.getText().toString().trim();
                f_name = fName.getText().toString().trim();
                l_name = lName.getText().toString().trim();
                m_name = mName.getText().toString().trim();
                p_email = email.getText().toString().trim();
                p_mobile = mobile.getText().toString().trim();
                p_adhar = adhar.getText().toString().trim();
                p_pan = pan.getText().toString().trim();
                password = mPasswordView.getText().toString().trim();
                repassword = rePasswordView.getText().toString().trim();

                if (!tncCB.isChecked()) {
                    tncCBErr.setVisibility(View.VISIBLE);
                    tncCBErr.setText("Please read and agree to our terms and conditions.");
                } else {
                    tncCBErr.setVisibility(View.GONE);
                }

                if (!personalCB.isChecked()) {
                    personalCBErr.setVisibility(View.VISIBLE);
                    personalCBErr.setText("Please read and agree to our privacy policy terms.");
                } else {
                    personalCBErr.setVisibility(View.GONE);
                }

                if (email_otp.isEmpty()) {
                    otpemailErr.setVisibility(View.VISIBLE);
                    otpemailErr.setText("Please enter OTP sent to your email.");
                    emailOTPEdit.requestFocus();
                } else {
                    otpemailErr.setVisibility(View.GONE);
                }

                if (mob_otp.isEmpty()) {
                    otpmobileErr.setVisibility(View.VISIBLE);
                    otpmobileErr.setText("Please enter OTP sent to your mobile.");
                    mobileOTPEdit.requestFocus();
                } else {
                    otpmobileErr.setVisibility(View.GONE);
                }

                if (password.isEmpty()) {
                    passwordErr.setVisibility(View.VISIBLE);
                    passwordErr.setText("Please enter Password.");
                    mPasswordView.requestFocus();
                } else {
                    passwordErr.setVisibility(View.GONE);
                    if (repassword.isEmpty() || !password.equals(repassword)) {
                        password2Err.setVisibility(View.VISIBLE);
                        password2Err.setText("Confirm Password does not match.");
                        rePasswordView.requestFocus();
                    } else {
                        password2Err.setVisibility(View.GONE);
                    }
                }

                if (p_mobile.isEmpty()) {
                    mobileErr.setVisibility(View.VISIBLE);
                    mobileErr.setText("Please enter your mobile number.");
                    mobile.requestFocus();
                } else {
                    mobileErr.setVisibility(View.GONE);
                }

                if (p_email.isEmpty()) {
                    emailErr.setVisibility(View.VISIBLE);
                    emailErr.setText("Please enter your Email-Id.");
                    email.requestFocus();
                } else {
                    emailErr.setVisibility(View.GONE);
                }

                if (p_pan.isEmpty()) {
                    panErr.setVisibility(View.VISIBLE);
                    panErr.setText("Please enter valid PAN card number.");
                    pan.requestFocus();
                } else {
                    panErr.setVisibility(View.GONE);
                }

                if (p_adhar.isEmpty()) {
                    aadharErr.setVisibility(View.VISIBLE);
                    aadharErr.setText("Please enter valid Aadhaar no.");
                    adhar.requestFocus();
                } else {
                    aadharErr.setVisibility(View.GONE);
                }

//                if (!fnameValid)
//                    if (!lnameValid)
                if (!mobileValid)
                {
                    emailOTPValidation(email_otp);
                    mobileOTPValidation(mob_otp);
                    passwordValidation(password);
                    mobileNoValidation(p_mobile);
                }
                if (!emailValid) {
                    emailIdValidation(p_email);
                }
                if (!panValid){
                    panCardValidation(p_pan);
                }

                if (!aadharValid) {
                    aadharCardValidation(p_adhar);
                }

                if (l_name.isEmpty()){
                    lnameErr.setVisibility(View.VISIBLE);
                    lnameErr.setText("Please enter last name.");
                    lName.requestFocus();
                } else {
                    lnameErr.setVisibility(View.GONE);
                }

                if (f_name.isEmpty()){
                    fnameErr.setVisibility(View.VISIBLE);
                    fnameErr.setText("Please enter first name.");
                    fName.requestFocus();
                } else {
                    fnameErr.setVisibility(View.GONE);
                }

                if (male.isChecked() || feMale.isChecked()){
                    genderErr.setVisibility(View.GONE);
                }else {
                    genderErr.setVisibility(View.VISIBLE);
                    genderErr.setText("Please choose gender.");
                    Toast.makeText(MainActivity.this, "Please choose gender.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!aadharValid){
                    Toast.makeText(MainActivity.this, "Please enter valid Aadhaar no.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!panValid){
                    Toast.makeText(MainActivity.this, "Please enter valid PAN card", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailValid){
                    Toast.makeText(MainActivity.this, "Please enter email-id", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mobileValid){
                    Toast.makeText(MainActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordValid){
                    Toast.makeText(MainActivity.this, "Please enter Password.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!fnameValid){
                    Toast.makeText(MainActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!lnameValid){
                    Toast.makeText(MainActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!aadharValid){
                    Toast.makeText(MainActivity.this, "Please enter aadhar card", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!panValid){
                    Toast.makeText(MainActivity.this, "Please enter pan card", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailValid){
                    Toast.makeText(MainActivity.this, "Please enter email-id", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mobileValid){
                    Toast.makeText(MainActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordValid){
                    Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!repasswordValid || !password.equals(repassword)){
                    Toast.makeText(MainActivity.this, "Confirm Password does not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mobOTPValid){
                    Toast.makeText(MainActivity.this, "Please enter OTP sent to your mobile", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailOTPValid){
                    Toast.makeText(MainActivity.this, "Please enter OTP sent to your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(register_button.getText().toString().equals("√ Sent (Resend ?)")) {
                    if (!tncCB.isChecked()) {
                        tncCBErr.setVisibility(View.VISIBLE);
                        tncCBErr.setText("Please read and agree to our terms and conditions.");
                    } else {
                        tncCBErr.setVisibility(View.GONE);
                        if (!personalCB.isChecked()) {
                            personalCBErr.setVisibility(View.VISIBLE);
                            personalCBErr.setText("Please read and agree to our privacy policy terms.");
                        } else {
                            personalCBErr.setVisibility(View.GONE);
                            if (true) {
                                if (true) {
                                    JSONObject userObj = new JSONObject();

                                    try {
                                        userObj.put("firstName", fName.getText().toString());
                                        userObj.put("middleName", mName.getText().toString());
                                        userObj.put("lastName", lName.getText().toString());

                                        if (male.isChecked()) {
                                            userObj.put("gender", "M");
                                        } else if (feMale.isChecked()) {
                                            userObj.put("gender", "F");
                                        } else {
                                            return;
                                        }

                                        userObj.put("aadhar", adhar.getText().toString());
                                        userObj.put("panNumber", pan.getText().toString());
                                    } catch (JSONException e) {

                                    }

                                    JSONObject jsonParams = new JSONObject();
                                    try {
                                        jsonParams.put("mobileOTP", mobileOTPEdit.getText().toString());
                                        jsonParams.put("emailOTP", emailOTPEdit.getText().toString());
                                        jsonParams.put("emailID", email.getText().toString());
                                        jsonParams.put("user", userObj);
                                    } catch (JSONException e) {

                                    }

                                    StringEntity entity = null;

                                    try {
                                        entity = new StringEntity(jsonParams.toString());
                                    } catch (Exception e) {

                                    }


                                    client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/borrowerRegistration/submitBasic/", entity, "application/json", new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                                            super.onSuccess(statusCode, headers, c);

                                            try {
                                                String session_id = c.getString("session_id");
                                                String csrf_token = c.getString("csrf_token");


                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("session_id", session_id);
                                                editor.putString("csrf_token", csrf_token);
                                                editor.putString("email", email.getText().toString());
                                                editor.putString("mobile", mobile.getText().toString());
                                                editor.commit();
                                            } catch (JSONException e) {

                                            }
                                            Intent i = new Intent(getApplicationContext(), RegistrationCheckEligibility.class);
                                            startActivity(i);
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject c) {
                                            System.out.println("Console is: " + "Faild OTP verification");
                                        }

                                    });
                                }
                            }
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please click on GET OTP for new OTP.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                //Log.d("Text",messageText);
                String otp = parseCode(messageText);
                mobileOTPEdit.setText(otp);

            }
        });


//        KeyboardVisibilityEvent.setEventListener(
//                this,
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        // some code depending on keyboard visiblity status
//                        btnLayout = (LinearLayout) findViewById(R.id.linearLayout);
//
//                        if (isOpen){
//                            btnLayout.setVisibility(LinearLayout.GONE);
//                        }else {
//                            new android.os.Handler().postDelayed(
//                                    new Runnable() {
//                                        public void run() {
//                                            btnLayout.setVisibility(LinearLayout.VISIBLE);
//                                        }
//                                    },
//                                    300);
//
//                        }
//
//                    }
//                });


    }

    public void panCardValidation(String s){
        if (s.length() > 10){
            pan.setText(s.toString().substring(0,10));
            panValid = false;
        }

        if(s.length() != 0 && s.length() != 10){
            // show error message on pan
            panErr.setVisibility(View.VISIBLE);
            panErr.setText("Please enter valid PAN card number.");
            textChange(pan);
            panValid = false;
            pan.requestFocus();
        }else if(s.length() == 0){
            panErr.setVisibility(View.VISIBLE);
            textChange(pan);
            panErr.setText("PAN card is required.");
            panValid = false;
            pan.requestFocus();
        }else {
            // check regex and backend validity of the pan
            System.out.println("Checking PAN");
            panErr.setVisibility(View.GONE);
            if(Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$").matcher(s.toString()).find()){

                panErr.setVisibility(View.GONE);
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
                            getOtp();
                            panValid = true;
                        }else{
                            panValid = false;
                            panErr.setVisibility(View.VISIBLE);
                            textChange(pan);
                            panErr.setText("Please enter valid PAN card number.");
                            pan.requestFocus();
                            if (count>0) {
                                panValid = false;
                                panErr.setVisibility(View.VISIBLE);
                                panErr.setText("This PAN is already registered with us.");
                                textChange(pan);
                                pan.requestFocus();
                            }
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
                panValid = false;
                panErr.setVisibility(View.VISIBLE);
                panErr.setText("Invalid PAN Card details");
                textChange(pan);
                pan.requestFocus();
            }
        }

        if (s.toString().trim().equals("")){
            panErr.setVisibility(View.VISIBLE);
            panErr.setText("Please enter valid PAN card number.");
            textChange(pan);
            pan.requestFocus();
        }
    }

    public void aadharCardValidation(String s){

        if(s.length() == 12){

            aadharErr.setVisibility(View.GONE);
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
                        getOtp();
                        aadharErr.setVisibility(View.GONE);
                        aadharValid = true;
                    } else {
                        aadharValid = false;
                        aadharErr.setVisibility(View.VISIBLE);
                        aadharErr.setText("Please enter valid Aadhaar no.");
                        textChange(adhar);
                        adhar.requestFocus();
                    }if (count>0){
                        aadharValid = false;
                        aadharErr.setVisibility(View.VISIBLE);
                        textChange(adhar);
                        aadharErr.setText("This Aadhaar number is already registered with us.");
                        adhar.requestFocus();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });



        }else{
            aadharValid = false;
            textChange(adhar);
            aadharErr.setVisibility(View.VISIBLE);
            aadharErr.setText("Invalid Aadhaar number");
            adhar.requestFocus();
        }
        if (s.toString().trim().equals("")) {
            aadharErr.setVisibility(View.VISIBLE);
            aadharErr.setText("Please enter valid Aadhaar no.");
            textChange(adhar);
            aadharValid = false;
            adhar.requestFocus();
        }
    }

    public void mobileNoValidation(String s){
        if(s.length() == 10){
            mobileErr.setVisibility(View.GONE);
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
                        getOtp();
                        mobileErr.setVisibility(View.GONE);
                        mobileValid = true;
                    } else {
                        mobileValid = false;
                        mobileErr.setVisibility(View.VISIBLE);
                        mobileErr.setText("Your mobile number is invalid.");
                        textChange(mobile);
                        mobile.requestFocus();
                        if (count>0) {
                            mobileValid = false;
                            mobileErr.setVisibility(View.VISIBLE);
                            mobileErr.setText("This mobile number is already registered with us.");
                            mobile.requestFocus();
                            textChange(mobile);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });



        }else{
            mobileValid = false;
            textChange(mobile);
            mobileErr.setVisibility(View.VISIBLE);
            mobileErr.setText("Invalid Mobile number");
            mobile.requestFocus();
        }
        if (s.toString().trim().equals("")) {
            mobileErr.setVisibility(View.VISIBLE);
            mobileErr.setText("Please enter your mobile number.");
            textChange(mobile);
            mobile.requestFocus();
            mobileValid = false;
        }
    }

    public void emailIdValidation(String s){

        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(validateEmail(s.toString())){
            emailErr.setVisibility(View.GONE);
            textChange(email);
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
                    if (count == 0){
                        emailErr.setVisibility(View.GONE);
                        showSuccess(email);
                        getOtp();
                        emailValid = true;
                    }else {
                        emailValid = false;
                        textChange(email);
                        emailErr.setVisibility(View.VISIBLE);
                        emailErr.setText("Please enter your Email-Id.");
                        email.requestFocus();
                        if (count > 0) {
                            emailValid = false;
                            textChange(email);
                            emailErr.setVisibility(View.VISIBLE);
                            emailErr.setText("This Email-Id is already registered with us.");
                            email.requestFocus();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        }else{
            emailValid = false;
            textChange(email);
            emailErr.setVisibility(View.VISIBLE);
            emailErr.setText("Invalid Email-Id");
            email.requestFocus();
        }
        if (s.toString().trim().equals("")){
            emailErr.setVisibility(View.VISIBLE);
            emailErr.setText("Please enter your Email-Id.");
            textChange(email);
            emailValid = false;
            email.requestFocus();
        }
    }

    public void passwordValidation(String s){

        updatePasswordStrengthView(s.toString());

        if(PasswordStrength.calculateStrength(s.toString()).getText(MainActivity.this).equals("Strong")){
            showSuccess(mPasswordView);
            getOtp();
            passwordErr.setVisibility(View.VISIBLE);
            passwordErr.setTextColor(getResources().getColor(R.color.green));
            passwordErr.setText("Your Password is Strong.");
            passwordValid = true;
        } else {
            passwordValid = false;
            textChange(mPasswordView);
            passwordErr.setVisibility(View.VISIBLE);
            passwordErr.setTextColor(getResources().getColor(R.color.cb_errorRed));
            passwordErr.setText("Your Password is Weak.");
        }
        if (s.toString().trim().equals("")) {
            passwordErr.setVisibility(View.VISIBLE);
            textChange(mPasswordView);
            passwordErr.setText("Please enter Password.");
            passwordValid = false;
        }
    }

    public void emailOTPValidation(String s){
        if(s.length() == 6){
            String url = backend.BASE_URL + "/api/v1/checkEmailOTP/" + email.getText().toString()+"/"+ s.toString() +"/?partner=false";
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                    super.onSuccess(statusCode, headers, c);
                    try {
                        if (c.getBoolean("valid")){
                            showSuccess(emailOTPEdit);
                            emailOTPValid = true;
                            otpemailErr.setVisibility(View.GONE);
                        } else{
                            emailOTPValid = false;
                            otpemailErr.setVisibility(View.VISIBLE);
                            otpemailErr.setText("Invalid email OTP");
                            emailOTPEdit.requestFocus();
                        }
                    }catch(JSONException e){
                        removeSuccess(emailOTPEdit);
                        otpemailErr.setVisibility(View.VISIBLE);
                        otpemailErr.setText("Invalid email OTP");
                        emailOTPEdit.requestFocus();
                    }

                }

            });
        }else {
            removeSuccess(emailOTPEdit);
            otpemailErr.setVisibility(View.VISIBLE);
            otpemailErr.setText("Invalid email OTP");
        }
        if (s.toString().trim().equals("")) {
            otpemailErr.setVisibility(View.VISIBLE);
            otpemailErr.setText("Please enter OTP sent to your email.");
        }
    }

    public void mobileOTPValidation(String s){
        if(s.length() == 6){
            String url = backend.BASE_URL + "/api/v1/checkMobileOTP/" + mobile.getText().toString() +'/' + s.toString() + "/?partner=false";
            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                    super.onSuccess(statusCode, headers, c);
                    try {
                        if (c.getBoolean("valid")){
                            showSuccess(mobileOTPEdit);
                            mobOTPValid = true;
                            otpmobileErr.setVisibility(View.GONE);
                        } else{
                            mobOTPValid = false;
                            otpmobileErr.setVisibility(View.VISIBLE);
                            otpmobileErr.setText("Invalid mobile OTP");
                            mobileOTPEdit.requestFocus();
                        }
                    }catch(JSONException e){
                        removeSuccess(mobileOTPEdit);
                        otpmobileErr.setVisibility(View.VISIBLE);
                        otpmobileErr.setText("Invalid mobile OTP");
                        mobileOTPEdit.requestFocus();
                    }

                }

                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject obj){
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            });
        }else {
            removeSuccess(mobileOTPEdit);
//                    mobileOTPEdit.setError("Mobile OTP not correct");
            otpmobileErr.setVisibility(View.VISIBLE);
            otpmobileErr.setText("Invalid mobile OTP");
        }
        if (s.toString().trim().equals("")) {
            otpmobileErr.setVisibility(View.VISIBLE);
            otpmobileErr.setText("Please enter OTP sent to your mobile.");
        }
    }


    public void textChange(EditText edit){
        register_button.setText("Get OTP");
        removeSuccess(edit);

    }

    public void getOtp(){
        emailOTPEdit.setText("");
        mobileOTPEdit.setText("");
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                }
                return;
            }
        }
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    private void updatePasswordStrengthView(String password) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView strengthView = (TextView) findViewById(R.id.password_strength);
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;

        if (password.isEmpty()) {
            strengthView.setText("Weak");
            progressBar.setProgress(0);
            return;
        }

        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }

    public boolean validateEmail(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
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
            case R.id.radio_male: {
                if (checked)
                    genderErr.setVisibility(View.GONE);
                break;
            }
            case R.id.radio_female: {
                if (checked)
                    genderErr.setVisibility(View.GONE);
                break;
            }
        }
    }

    public void tncCB(View v){
        if(tncCB.isChecked()){
            tncCBErr.setVisibility(View.GONE);
        }
    }

    public void personalCB(View v){
        if (personalCB.isChecked()){
            personalCBErr.setVisibility(View.GONE);
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

    public static String getMonthInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return (dateFormat.format(date));
    }

    public static String getYearInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);
    }

    public static String getdayInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

}
