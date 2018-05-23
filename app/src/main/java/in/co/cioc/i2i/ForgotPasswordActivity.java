package in.co.cioc.i2i;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class ForgotPasswordActivity extends AppCompatActivity {

    AutoCompleteTextView resetEmail;
    TextView emailErr, forgot, check, tryAgain;
    RequestParams requestParams = new RequestParams();
    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    String jsonResponse;
    Backend backend;
    Drawable successTick;

    LinearLayout resetemailFrom;
    Button sendPassword;

    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backend = new Backend();
        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );


        pattern = Pattern.compile(EMAIL_PATTERN);

        resetEmail = findViewById(R.id.reset_email);

        resetemailFrom = findViewById(R.id.email_reset_form);

        forgot = findViewById(R.id.forgotPassword);
        check = findViewById(R.id.checkPassword);
        emailErr = findViewById(R.id.resetemailErrTxt);
        tryAgain = findViewById(R.id.tryAgain);
        tryAgain.setVisibility(View.GONE);

        sendPassword = findViewById(R.id.reset_password_button);

    }

    public void sendPassword(View v){

        final String email = resetEmail.getText().toString().trim();
        if (email.isEmpty()){
            emailErr.setVisibility(View.VISIBLE);
            emailErr.setText("Please enter your Email-Id.");
        } else {
            if (validateEmail(email)){
                emailErr.setVisibility(View.GONE);
                client.get(backend.BASE_URL + "/api/v1/checkEmail/" + email +"/i2i_users||usr_email", requestParams, new JsonHttpResponseHandler() {
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
                            resetEmail.setError("Email ID already exist");
                            emailErr.setVisibility(View.VISIBLE);
                            emailErr.setText("Please enter your Email-Id.");
                            resetEmail.requestFocus();
                        }else if (count > 0) {
                                showSuccess(resetEmail);
                                sendEmail(email);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                });



            }else{
                resetEmail.setError("Invalid Email-Id");
                resetEmail.requestFocus();
            }
        }


    }

    public void sendEmail(String email){
        RequestParams requestParams = new RequestParams();
        requestParams.put("usr_email", email);

        client.post(backend.BASE_URL + "/api/v1/resetPassword/", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                jsonResponse = response.toString();
                System.out.println(jsonResponse);
                resetemailFrom.setVisibility(View.GONE);
                emailErr.setVisibility(View.GONE);
                tryAgain.setVisibility(View.VISIBLE);
                forgot.setText("Thank you!");
                check.setText("Please check your email for the link to create a new password.");
                SpannableStringBuilder sb = new SpannableStringBuilder();
                String regularText = "Didn't receive the email? Be sure to check your spam folder. If you still don't see the email. Please make sure you are using correct email address. Click here to ";
                String clickableText = "try again";
                sb.append(regularText);
                sb.append(clickableText);
                sb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(Color.BLUE);
                    }
                }, sb.length()-clickableText.length(), sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tryAgain.setText(sb);
                tryAgain.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    String error = errorResponse.getString("message");
                    resetemailFrom.setVisibility(View.VISIBLE);
                    tryAgain.setVisibility(View.GONE);
                    emailErr.setVisibility(View.VISIBLE);
                    emailErr.setText(error);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
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

}
