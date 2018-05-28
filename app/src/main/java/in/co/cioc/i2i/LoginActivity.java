package in.co.cioc.i2i;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.text.TextUtils;
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

    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.activity_login);

        getWindow().setBackgroundDrawableResource(R.drawable.bg);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
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

        boolean cancel = false;
        View focusView = null;

        RequestParams params = new RequestParams();
        params.put("usr_email", email);
        params.put("usr_password", password);

        client.post(backend.BASE_URL + "/api/v1/login/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                directToAccount(c);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,  JSONObject c) {
                super.onFailure(statusCode, headers, e, c);



                if(statusCode == 401){
                    directToAccount(c);
                }else {
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

