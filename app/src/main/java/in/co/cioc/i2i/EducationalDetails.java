package in.co.cioc.i2i;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
    TextView degreeErr, collegeErr, specializationErr, dropdownErr;

    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
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


        degreeErr = findViewById(R.id.degreeErrTxt);
        collegeErr = findViewById(R.id.collegeErrTxt);
        specializationErr = findViewById(R.id.specializationErrTxt);
        dropdownErr = findViewById(R.id.graduationYrErrTxt);

        degree = findViewById(R.id.degree);
        degree.setAdapter(new AutoCompleteAdapter(this, degree.getText().toString() , "degreeSearch"));

        degree.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    degreeErr.setVisibility(View.VISIBLE);
                    degreeErr.setText("Please provide the highest latest degree.");
                } else {
                    degreeErr.setVisibility(View.GONE);
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    collegeErr.setVisibility(View.VISIBLE);
                    collegeErr.setText("Please provide college name.");
                } else {
                    collegeErr.setVisibility(View.GONE);
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
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().equals("")){
                    specializationErr.setVisibility(View.VISIBLE);
                    specializationErr.setText("Please provide specialization.");
                } else {
                    specializationErr.setVisibility(View.GONE);
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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dropdownErr.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                    String year = c.getString("oth_graduation_year");
                    for (int i=0; i<items.length; i++){
                        if (year.equals(items[i])){
                            dropdown.setSelection(i);
                        }
                    }

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

        if (dropdown.getSelectedItemPosition() == 0){
            dropdownErr.setVisibility(View.VISIBLE);
            dropdownErr.setText("Please select your graduation year.");
        } else {
            dropdownErr.setVisibility(View.GONE);
        }

        String degr = degree.getText().toString().trim();
        String colleg = college.getText().toString().trim();
        String spec = specialization.getText().toString().trim();
        String yr = dropdown.getSelectedItem().toString().trim();

        if (degr.isEmpty()){
            degreeErr.setVisibility(View.VISIBLE);
            degreeErr.setText("Please provide the highest latest degree.");
        } else {
            degreeErr.setVisibility(View.GONE);
        }

        if (colleg.isEmpty()){
            collegeErr.setVisibility(View.VISIBLE);
            collegeErr.setText("Please provide college name.");
        } else {
            collegeErr.setVisibility(View.GONE);
        }

        if (spec.isEmpty()){
            specializationErr.setVisibility(View.VISIBLE);
            specializationErr.setText("Please provide specialization.");
        } else {
            specializationErr.setVisibility(View.GONE);
        }


        JSONObject educationalForm = new JSONObject();

        try{
            educationalForm.put("degree" , degr);
            educationalForm.put("college" , colleg);
            educationalForm.put("specialization" , spec);
            educationalForm.put("year" , yr );

            if (degr.length() == 0){
                Toast.makeText(this, "Please provide the highest latest degree.", Toast.LENGTH_SHORT).show();
                degree.requestFocus();
                return;
            }
            if (colleg.length() == 0){
                Toast.makeText(this, "Please provide college name.", Toast.LENGTH_SHORT).show();
                college.requestFocus();
                return;
            }
            if (spec.length() == 0){
                Toast.makeText(this, "Please provide specialization.", Toast.LENGTH_SHORT).show();
                specialization.requestFocus();
                return;
            }

            if (yr.equals("Please select")){
                Toast.makeText(this, "Please select your graduation year.", Toast.LENGTH_SHORT).show();
                dropdown.requestFocus();
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

        String url = "";
        if (!stay){
            save(true);
            url += "1";
        }else{
            url += "0";

        }

        client.post(getApplicationContext(), backend.BASE_URL + "/api/v1/borrowerRegistration/educationalDetails/?next="+url+"&csrf_token=" + csrf_token + "&session_id=" + session_id , entity , "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if(!stay){
                    Intent i = new Intent(getApplicationContext(), DocumentsActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(EducationalDetails.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack()) {
//            Toast.makeText(this, "Fill All Details.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    public boolean shouldAllowBack(){
        return false;
    }

}
