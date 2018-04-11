package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class EducationalDetails extends AppCompatActivity {

    AutoCompleteTextView degree, college , specialization;
    Drawable successTick;
    Spinner dropdown;

    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_details);
        StepView mStepView = (StepView) findViewById(R.id.step_view);

        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(4);


        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );


        degree = findViewById(R.id.degree);
        degree.setAdapter(new AutoCompleteAdapter(this, degree.getText().toString() , "degreeSearch"));

        degree.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(degree);
                }else {
                    removeSuccess(degree);
                }
            }
        });

        college = findViewById(R.id.college);
        college.setAdapter(new AutoCompleteAdapter(this, college.getText().toString() , "collegeSearch"));

        college.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(college);
                }else {
                    removeSuccess(college);
                }
            }
        });

        specialization = findViewById(R.id.specialization);
        specialization.setAdapter(new AutoCompleteAdapter(this, specialization.getText().toString() , "specializationSearch"));

        specialization.addTextChangedListener(new TextWatcher() {
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
                    showSuccess(specialization);
                }else {
                    removeSuccess(specialization);
                }
            }
        });

        dropdown = findViewById(R.id.spinner);
        items = new String[]{"Please select", "2004" , "2005" , "2006" , "2007", "2008" , "2009" , "2010" , "2011", "2012" , "2013" , "2014" , "2015", "2016" , "2017"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);


        Button nextBtn = findViewById(R.id.next_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(false);
            }
        });



        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);
        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);
        client.get(backend.BASE_URL + "/api/v1/retriveDetails/educational/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try{
                    specialization.setText(c.getString("oth_specialization"));
                    degree.setText(c.getString("oth_degree"));
                    college.setText(c.getString("oth_college"));
//                    dropdown.setSelection(items.index(c.getString("oth_graduation_year")));
                }catch (JSONException e) {

                }
            }

        });

        Button previous = findViewById(R.id.previous_button);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });


        Button saveBtn = findViewById(R.id.save_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(true);
            }
        });

    }

    public void previous(){

        StringEntity entity = null;

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        String url = "/api/v1/borrowerRegistration/previous/?csrf_token=" + csrf_token + "&session_id=" + session_id;

        client.post(getApplicationContext(), backend.BASE_URL + url ,entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Intent i = new Intent(getApplicationContext(), EmployementDetails.class);
                startActivity(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }



    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }


    public void save(final Boolean stay){

        JSONObject educationalForm = new JSONObject();

        try{

            String degr = degree.getText().toString();
            String colleg = college.getText().toString();
            String spec = specialization.getText().toString();
            String yr = dropdown.getSelectedItem().toString();
            educationalForm.put("degree" , degr);
            educationalForm.put("college" , colleg);
            educationalForm.put("specialization" , spec);
            educationalForm.put("year" , yr );

            if (yr.equals("Please select")){
                Toast.makeText(this, "Please select the graduation year", Toast.LENGTH_SHORT).show();
                return;
            }

            if (degr.length() == 0){
                Toast.makeText(this, "Please enter your Highest degree", Toast.LENGTH_SHORT).show();
                return;
            }
            if (colleg.length() == 0){
                Toast.makeText(this, "Please enter your college name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (spec.length() == 0){
                Toast.makeText(this, "Please enter your specialization", Toast.LENGTH_SHORT).show();
                return;
            }

        }catch (JSONException e){

        }

        JSONObject jsonParams = new JSONObject();

        try{
            jsonParams.put("educationalForm" , educationalForm);
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

        client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/borrowerRegistration/educationalDetails/?next=1&csrf_token=" + csrf_token + "&session_id=" + session_id , entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if(!stay){
                    Intent i = new Intent(getApplicationContext(), DocumentsActivity.class);
                    startActivity(i);
                }else {
                    Toast.makeText(EducationalDetails.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

}
