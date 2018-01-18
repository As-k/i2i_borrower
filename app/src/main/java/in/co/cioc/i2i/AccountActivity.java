package in.co.cioc.i2i;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AccountActivity extends AppCompatActivity {

    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();

    SharedPreferences sharedPreferences;
    TextView name , userID, logoutBtn;
    Button continueBtn;
    Integer reg_stage;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        progress = new ProgressDialog(this);
        progress.setTitle("Please wait");
        progress.setMessage("Checkin login details...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();



        name = findViewById(R.id.name);
        userID = findViewById(R.id.userID);
        logoutBtn = findViewById(R.id.logout);
        continueBtn = findViewById(R.id.continue_button);

        continueBtn.setVisibility(Button.GONE);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

            }
        });


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
                        Integer usrStatus = c.getInt("userStatus");

                        if (usrStatus == 0){
                            continueBtn.setVisibility(Button.VISIBLE);
                        }

                        name.setText(firstName + "  " + lastName);
                        userID.setText(id.toString());

                        reg_stage = c.getInt("reg_stage");

                        // To dismiss the dialog


                    }catch (JSONException e) {

                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject c){
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }

            });
        }else {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);

        }

    }

    public void continueRegistration(View view){

        Intent i = null;
        if (reg_stage == 1){
            i = new Intent(getApplicationContext(), RegistrationCheckEligibility.class);
        }else if(reg_stage == 2){
            i = new Intent(getApplicationContext(), UserDetails.class);
        }else if(reg_stage == 3){
            i = new Intent(getApplicationContext(), EmployementDetails.class);
        }else if(reg_stage == 4){
            i = new Intent(getApplicationContext(), EducationalDetails.class);
        }else if(reg_stage == 5){
            i = new Intent(getApplicationContext(), DocumentsActivity.class);
        }

        if (i != null){
            startActivity(i);
        }

    }
}
