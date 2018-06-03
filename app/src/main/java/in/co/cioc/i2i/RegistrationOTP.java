package in.co.cioc.i2i;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;


public class RegistrationOTP extends AppCompatActivity {

    private String session_id = "";
    private String csrf_token = "";
    private EditText mobileOTPEdit;
    private Drawable successTick;
    private static AsyncHttpClient client = new AsyncHttpClient(true,80,443);
    Backend backend;
    SharedPreferences sharedPreferences;

    TextView otpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_otp);

        Intent intentExtra = getIntent();

        otpView = findViewById(R.id.otpView);

        backend = new Backend();

        Bundle extrasBundle = intentExtra.getExtras();
        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );

        mobileOTPEdit = findViewById(R.id.mobileOTP);

        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        try{
            csrf_token = extrasBundle.getString("csrf_token");
            session_id = extrasBundle.getString("session_id");

        }catch (RuntimeException e){
        }


        client.get(getApplicationContext(), backend.BASE_URL + "/api/v1/requestLoginOTP/?csrf_token=" + csrf_token + "&session_id=" + session_id , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });


        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                String otp = parseCode(messageText);
                mobileOTPEdit.setText(otp);

                String url = backend.BASE_URL + "/api/v1/checkLoginOTP/?otp=" + otp +"&csrf_token=" + csrf_token + "&session_id=" + session_id;
                client.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("session_id", session_id);
                        editor.putString("csrf_token", csrf_token);
                        editor.commit();
                        Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                });

            }
        });


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

    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }

}
