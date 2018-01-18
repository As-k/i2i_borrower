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
import android.widget.Spinner;

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
        String[] items = new String[]{"2004" , "2005" , "2006" , "2007", "2008" , "2009" , "2010" , "2011", "2012" , "2013" , "2014" , "2015", "2016" , "2017"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Button nextBtn = findViewById(R.id.next_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
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


    public void save(){

        JSONObject educationalForm = new JSONObject();

        try{
            educationalForm.put("degree" , degree.getText().toString());
            educationalForm.put("college" , college.getText().toString());
            educationalForm.put("specialization" , specialization.getText().toString());
            educationalForm.put("year" , dropdown.getSelectedItem().toString());
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
                Intent i = new Intent(getApplicationContext(), DocumentsActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

}
