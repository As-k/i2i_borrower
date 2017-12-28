package in.co.cioc.i2i;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class RegistrationCheckEligibility extends AppCompatActivity {

    Boolean loading;
    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend;
    Spinner dropdownPurpose , spinnerPaymentMode;
    private int year, month, day;
    private Calendar calendar;
    private EditText bodEditTxt, pincodeEditTxt , cityEditTxt, workingSinceEditTxt;
    Drawable successTick;

    EditText amountTxt , descriptionTxt;

    EditText professionTypeTxt, turnoverSelfEmpTxt , profitSelfEmpTxt;
    EditText professionBusinessTxt, turnoverBusinessTxt , profitBusinessTxt;

    EditText incomeSalariedTxt , workingSinceTxt, workExperienceTxt , workExperienceMonthTxt;
    AutoCompleteTextView companyTxt;

    LinearLayout loanEmiLayout , ccOutstandingLayout , rentForm , spouseIncomeLayout;

    EditText loanEmiTxt , ccOutstandingTxt, creditScoreTxt;

    public void onRadioButtonClickedMarital(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_single:
                if (checked)

                    // Pirates are the best
                    break;
            case R.id.radio_married:
                if (checked)
                    // Ninjas rule
                    break;
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

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }else if (id == 1000) {
            return new DatePickerDialog(this,
                    WorkingSinceListner, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            bodEditTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2).append("/").append(arg1));
            showSuccess(bodEditTxt);
        }
    };


    private DatePickerDialog.OnDateSetListener WorkingSinceListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0,
                              int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            workingSinceEditTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2).append("/").append(arg1));
            showSuccess(workingSinceEditTxt);
        }
    };

    private void insertIntoDP(Integer amt , String desc , String date , String filePath){
        // insert here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_check_eligibility);

        backend = new Backend();

        insertIntoDP(423, "$32423", "432423" , "432423");

        bodEditTxt = findViewById(R.id.dob);

        workingSinceEditTxt = findViewById(R.id.workingSince);
        incomeSalariedTxt = findViewById(R.id.incomeSalaried);
        workExperienceTxt = findViewById(R.id.workExperience);
        workExperienceMonthTxt = findViewById(R.id.workExperienceMonth);
        companyTxt = findViewById(R.id.company);
        companyTxt.setAdapter(new CompanyAutoCompleteAdapter(this,companyTxt.getText().toString()));

        amountTxt = findViewById(R.id.amount);
        descriptionTxt = findViewById(R.id.description);

        amountTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Integer i = Integer.parseInt(s.toString());

                if(i<25000 || i > 300000){
                    removeSuccess(amountTxt);
                    amountTxt.setError("Amount can not be less then 25000 or greater than 300000");
                    amountTxt.requestFocus();
                }else if(i%5000 != 0){
                    amountTxt.setError("Please enter amount in the multiples of 5000");
                    amountTxt.requestFocus();
                } else {
                    showSuccess(amountTxt);
                }
            }
        });

        descriptionTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    showSuccess(descriptionTxt);
                }else {
                    removeSuccess(descriptionTxt);
                    descriptionTxt.setError("Please explain why you need this loan");
                    descriptionTxt.requestFocus();
                }
            }
        });



        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"3", "6", "9" , "12", "15","18" ,"21","24"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdownEmpType = findViewById(R.id.spinnerEmpType);
        String[] itemsEmpType = new String[]{"Salaried", "Self Employed", "Business" };
        ArrayAdapter<String> adapterEmpType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsEmpType);
        dropdownEmpType.setAdapter(adapterEmpType);

        Spinner spinnerPaymentMode = findViewById(R.id.spinnerPaymentMode);
        String[] itemsPaymentMode = new String[]{"Cash", "Cheque", "Credit to Bank Account" };
        ArrayAdapter<String> adapterPaymentMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsPaymentMode);
        spinnerPaymentMode.setAdapter(adapterPaymentMode);

        Spinner spinnerHouseType = findViewById(R.id.spinnerHouseType);
        String[] itemsHouseType = new String[]{"Rented", "Own", "Parental" };
        ArrayAdapter<String> adapterHouseType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsHouseType);
        spinnerHouseType.setAdapter(adapterHouseType);

        final LinearLayout selfEmpForm = findViewById(R.id.emp_form_selfEmp);
        final LinearLayout businessForm = findViewById(R.id.emp_form_business);
        final LinearLayout salariedForm = findViewById(R.id.emp_form_salaried);

        spouseIncomeLayout = findViewById(R.id.spouseIncomeLayout);
        spouseIncomeLayout.setVisibility(LinearLayout.GONE);

        selfEmpForm.setVisibility(LinearLayout.GONE);
        businessForm.setVisibility(LinearLayout.GONE);

        professionTypeTxt = findViewById(R.id.professionSelfEmp);
        turnoverSelfEmpTxt = findViewById(R.id.turnoverSelfEmp);
        profitSelfEmpTxt = findViewById(R.id.profitSelfEmp);

        professionBusinessTxt = findViewById(R.id.professionBusiness);
        turnoverBusinessTxt = findViewById(R.id.turnoverBusiness);
        profitBusinessTxt = findViewById(R.id.profitBusiness);

        loanEmiTxt = findViewById(R.id.loanEmi);
        ccOutstandingTxt = findViewById(R.id.ccOutstanding);
        creditScoreTxt = findViewById(R.id.creditScore);

        loading = true;

        spinnerHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
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

                if (position == 0){
                    salariedForm.setVisibility(LinearLayout.VISIBLE);
                    selfEmpForm.setVisibility(LinearLayout.GONE);
                    businessForm.setVisibility(LinearLayout.GONE);
                    if (!loading){
                        companyTxt.requestFocus();
                    }

                }else if (position == 1){
                    salariedForm.setVisibility(LinearLayout.GONE);
                    selfEmpForm.setVisibility(LinearLayout.VISIBLE);
                    businessForm.setVisibility(LinearLayout.GONE);
                    professionTypeTxt.requestFocus();

                }else if (position == 2){
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


                    ArrayAdapter<String> adapterPurpose = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, purps);
                    dropdownPurpose.setAdapter(adapterPurpose);
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
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = s.toString();

                if(s.length() == 6){
                    showSuccess(pincodeEditTxt);

                    client.get(backend.BASE_URL + "/api/v1/pincodeSearch/" + s.toString() , new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                cityEditTxt.setText(response.getString("pin_city"));
                                showSuccess(cityEditTxt);
                            }catch (JSONException e){

                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                    });


                }else{
                    pincodeEditTxt.setError("Invalid Pincode");
                    View focusView = pincodeEditTxt;
                    focusView.requestFocus();
                }
            }
        });


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes_creditCard:
                if (checked)
                    ccOutstandingLayout.setVisibility(LinearLayout.VISIBLE);
                    ccOutstandingTxt.requestFocus();
                    break;
            case R.id.radio_no_creditCard:
                if (checked)
                    creditScoreTxt.requestFocus();
                    ccOutstandingLayout.setVisibility(LinearLayout.GONE);
                    break;
            case R.id.radio_yes_loan:
                if (checked)
                    loanEmiLayout.setVisibility(LinearLayout.VISIBLE);
                    loanEmiTxt.requestFocus();
                break;
            case R.id.radio_no_loan:
                if (checked)
                    creditScoreTxt.requestFocus();
                    loanEmiLayout.setVisibility(LinearLayout.GONE);
                break;
            case R.id.radio_single:
                if (checked)
                    spouseIncomeLayout.setVisibility(LinearLayout.GONE);
                break;
            case R.id.radio_married:
                if (checked)
                    spouseIncomeLayout.setVisibility(LinearLayout.VISIBLE);
                break;
        }
    }

    public void showSuccess(EditText edit){
        removeSuccess(edit);
        edit.setCompoundDrawablesRelative( null, null, successTick, null );
    }

    public void removeSuccess(EditText edit){
        edit.setCompoundDrawables(null, null, null, null);
    }
}
