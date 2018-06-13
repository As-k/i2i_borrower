package in.co.cioc.i2i;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    public static String  PREFS_NAME="mypre";
    public static String PREF_FNAME="fname";
    public static String PREF_PASSWORD="password";
    public static String PREF_EMAIL="email";
    public static String PREF_PHONE="phone";
    public static String PREF_MNAME="mname";
    public static String PREF_LNAME="lname";
    public static String PREF_ID="id";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    TextView emailErr, passwordErr, emailPasswordErr;
    Drawable successTick;
    private Pattern pattern;
    private Matcher matcher;
    boolean emailValid, passwordValid;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static AsyncHttpClient client = new AsyncHttpClient(true,80, 443);
    Backend backend = new Backend();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );
        pattern = Pattern.compile(EMAIL_PATTERN);

        emailErr = findViewById(R.id.emailErrTxt);
        passwordErr = findViewById(R.id.passwordErrTxt);
        emailPasswordErr = findViewById(R.id.email_passErrTxt);

        mEmailView.addTextChangedListener(new TextWatcher() {
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
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals("")) {
                    passwordErr.setVisibility(View.VISIBLE);
                    passwordErr.setText("Please enter your Password.");
                    mPasswordView.requestFocus();
                    removeSuccess(mEmailView);
                    passwordValid = false;
                } else {
                    passwordErr.setVisibility(View.GONE);
                    passwordValid = true;
                }
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

//        mEmailView.setText("test@gmail.com");
//        mPasswordView.setText("comman#44561");

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);


        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        // /api/v1/notifications/?csrf_token=4Iku9T1qI5rp4hXvuw3JjnQqt&session_id=LN8nyo986bo4ohtlPhtqvkWqU

        if (session_id != null && csrf_token != null){
            client.get(backend.BASE_URL + "/api/v1/notifications/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                    super.onSuccess(statusCode, headers, c);

                    try {
                        String firstName = c.getString("firstName");
                        String  middleName= c.getString("middleName");
                        String  lastName= c.getString("lastName");
                        Integer  id= c.getInt("id");

                        Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                        startActivity(i);

                    }catch (JSONException e) {

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e,  JSONObject c) {
                    super.onFailure(statusCode, headers, e, c);


                }

            });
        }



    }
    RequestParams requestParams = new RequestParams();
    public void emailIdValidation(String s){
        if(validateEmail(s.toString())){
            emailErr.setVisibility(View.GONE);
            client.get(backend.BASE_URL + "/api/v1/checkEmail/" + s.toString() +"/i2i_users||usr_email", requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    String jsonResponse = response.toString();
                    System.out.println(jsonResponse);
                    Integer count = -1;
                    try {
                        count = response.getInt("count");
                    }catch (JSONException err){
                    }
                    if (count > 0){
                        emailErr.setVisibility(View.GONE);
                        showSuccess(mEmailView);
                        emailValid = true;
                    }else {
                        emailErr.setVisibility(View.VISIBLE);
                        removeSuccess(mEmailView);
                        emailErr.setText("Email-id entered by you is not registered with us. Please enter correct email-id.");
                        emailValid = false;
                        mEmailView.requestFocus();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        }else{
            emailErr.setVisibility(View.VISIBLE);
            emailErr.setText("Please enter your valid login Email-Id.");
            emailValid = false;
            removeSuccess(mEmailView);
            emailErr.requestFocus();
        }
        if (s.toString().trim().equals("")){
            emailErr.setVisibility(View.VISIBLE);
            emailValid = false;
            removeSuccess(mEmailView);
            emailErr.setText("Please enter your valid login Email-Id.");
            mEmailView.requestFocus();
        }
    }

    public boolean validateEmail(final String hex) {
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }
    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }


    public void register(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public void resetPassword(View v){
        Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(i);
    }

    private void attemptLogin() {

        Toast.makeText(this, backend.BASE_URL, Toast.LENGTH_LONG).show();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (password.isEmpty()){
            passwordErr.setVisibility(View.VISIBLE);
            passwordErr.setText("Please enter your Password.");
            mPasswordView.requestFocus();
        } else {
            passwordErr.setVisibility(View.GONE);
        }
        if (email.isEmpty()){
            emailErr.setVisibility(View.VISIBLE);
            emailErr.setText("Please enter your valid login Email-Id.");
            mEmailView.requestFocus();
        } else {
            emailErr.setVisibility(View.GONE);
        }

        if (!emailValid) {
            emailIdValidation(email);
        }

        if (!emailValid){
            Toast.makeText(this, "Please enter your valid login Email-Id.", Toast.LENGTH_SHORT).show();
            mEmailView.requestFocus();
            return;
        }

        if (!passwordValid){
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
            mPasswordView.requestFocus();
            return;
        }

        boolean cancel = false;
        View focusView = null;

        RequestParams params = new RequestParams();
        params.put("usr_email", email);
        params.put("usr_password", password);

        client.post(backend.BASE_URL + "/api/v1/login/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);
                emailPasswordErr.setVisibility(View.GONE);
                directToAccount(c);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,  JSONObject c) {
                super.onFailure(statusCode, headers, e, c);



                if(statusCode == 401){
                    emailPasswordErr.setVisibility(View.GONE);
                    directToAccount(c);
                }else {
                    emailPasswordErr.setVisibility(View.VISIBLE);
                    emailPasswordErr.setText("Password entered by you is incorrect. Your account will be blocked after 3 successive failed login attempts.");
                    Toast.makeText(LoginActivity.this, "Username or Password incorrect", Toast.LENGTH_SHORT).show();
                }

            }

        });


    }

    void directToAccount(JSONObject c){
        try {


            Bundle bundle = new Bundle();

            String session_id = c.getString("session_id");
            String csrf_token = c.getString("csrf_token");


            bundle.putString("session_id", session_id);
            bundle.putString("csrf_token", csrf_token);

            Intent i = new Intent(getApplicationContext(), RegistrationOTP.class);
            i.putExtras(bundle);
            startActivity(i);

//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("session_id", session_id);
//            editor.putString("csrf_token", csrf_token);
//            editor.commit();
//
//            Intent i = new Intent(getApplicationContext(), AccountActivity.class);
//            startActivity(i);

        }catch(JSONException e){

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



}

