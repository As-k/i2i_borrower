package in.co.cioc.i2i;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.githang.stepview.StepView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import cz.msebera.android.httpclient.Header;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class DocumentsActivity extends AppCompatActivity {

    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;
    Drawable successTick;

    AutoCompleteTextView panPassword, aadharPassword, permAddressPassword, localAddressPassword, statement12Password, salarySlipLastPassword, salarySlip2Password, salarySlip3Password;

    public static final int REQUEST_CODE_CAMERA = 0012;

    private String [] items = {"Camera","Select a file"};

    private ArrayList<String> filePaths = new ArrayList<>();

    private Spinner dropdownPerm , dropdownCurrent;

    private ImageView panUpload , aadharUpload , permAddress , currentAddress , bankStatement;
    private ImageView empSalarySlip1, empSalarySlip2 , empSalarySlip3 , empForm16 , empHighestDegree ;
    private ImageView businessPan , businessCompanyReg , businessITR1 , businessITR2, businessITR3 ;
    private ImageView selfEmpPan , selfEmpCompanyReg, selfEmpITR1 , selfEmpITR2, selfEmpITR3 , selfEmpHighestDegree;

    List<Integer> uploadedFiles = new ArrayList<Integer>();

    private TextView pan , aadhar;
    LinearLayout businessForm , salariedForm , selfEmpForm;
    CheckBox agreeCB , authorizeCB;
    TextView agreeTxt , authorizeTxt, tncCBErr, personalCBErr;;


    String empType;
    Integer REQUEST_CODE_DOC =0;

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9090) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FilePickerBuilder.getInstance().setMaxCount(1)
                        .setActivityTheme(R.style.AppTheme)
                        .pickFile(this);
            } else {
                // User refused to grant permission.
            }
        }
    }

    public void findIdAutoText(){
        panPassword = findViewById(R.id.panPassword);
        aadharPassword = findViewById(R.id.aadharPassword);
        permAddressPassword = findViewById(R.id.permAddressPassword);
        localAddressPassword = findViewById(R.id.localAddressPassword);
        statement12Password = findViewById(R.id.statement12Password);
        salarySlipLastPassword = findViewById(R.id.salarySlipLastPassword);
        salarySlip2Password = findViewById(R.id.salarySlip2Password);
        salarySlip3Password = findViewById(R.id.salarySlip3Password);
//        form16Password = findViewById(R.id.form16Password);
        tncCBErr = findViewById(R.id.tncCheckboxErrTxt);
        personalCBErr = findViewById(R.id.personalInfoCheckBoxErrTxt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        StepView mStepView = (StepView) findViewById(R.id.step_view);

        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(5);

        successTick = this.getResources().getDrawable( R.drawable.ic_check_green_24dp );
        int h = successTick.getIntrinsicHeight();
        int w = successTick.getIntrinsicWidth();
        successTick.setBounds( 0, 0, w, h );


        businessForm = findViewById(R.id.business_form);
        selfEmpForm = findViewById(R.id.selfemp_form);
        salariedForm = findViewById(R.id.empl_form);

        businessForm.setVisibility(LinearLayout.GONE);
        selfEmpForm.setVisibility(LinearLayout.GONE);
        salariedForm.setVisibility(LinearLayout.GONE);

        findIdAutoText();

        authorizeCB = findViewById(R.id.authorize);
        authorizeTxt = findViewById(R.id.authorizetxt);
        agreeCB = findViewById(R.id.agree);
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
                    ds.setColor(Color.rgb(210,60,50)); // need put invalidate here to make text change to own color when pressed on Highlight Link
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
                    ds.setColor(Color.rgb(100,0,200)); // need put invalidate here to make text change to RED when pressed on Highlight Link
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

        pan = findViewById(R.id.pan);
        aadhar = findViewById(R.id.aadhar);

        panPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||doc_pancard", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        aadharPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||aadhar_card", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        permAddressPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||doc_parmanent_add_proof", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        localAddressPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    int pass = Integer.parseInt(s.toString().trim());
                    uploadPassword("i2i_borrower_document_details||doc_current_add_proof", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        statement12Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||doc_account_statement", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        salarySlipLastPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||doc_sal_last_month", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        salarySlip2Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||doc_sal_2nd_month", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        salarySlip3Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().trim().length()>0){
                    uploadPassword("i2i_borrower_document_details||doc_sal_3rd_month", s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        form16Password.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
//                if (s.toString().trim().length()>0){
//                    uploadPassword("i2i_borrower_document_details||doc_form_16", s.toString().trim());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });



        sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);
        client.get(backend.BASE_URL + "/api/v1/retriveDetails/regulatory/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try {
                    empType = c.getString("emp_type");
                    pan.setText(c.getString("usr_pan"));
                    aadhar.setText(c.getString("aadhar_card"));

                    if (empType.equals("Salaried Employee") ){
                        salariedForm.setVisibility(LinearLayout.VISIBLE);
                    }else if(empType.equals("Self Employed Professional")){
                        selfEmpForm.setVisibility(LinearLayout.VISIBLE);
                    }else {
                        businessForm.setVisibility(LinearLayout.VISIBLE);
                    }

                }catch (JSONException e) {

                }
            }


        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    9090);
            return;
        }else{
//            openImage();

        }


        dropdownPerm = findViewById(R.id.permAddressSpinner);
        dropdownCurrent = findViewById(R.id.localAddressSpinner);
        String[] items = new String[]{"Please select", "House Tax Receipt", "Electricity Bill", "Registration Document" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownPerm.setAdapter(adapter);
        dropdownCurrent.setAdapter(adapter);

        //http://localhost:8080/api/v1/saveFile/?csrf_token=PvKMr0O9JCGaS1yO52GxSnMtG&session_id=6jJoBMIsZOMsTu47TTZ7jd4Fm
        //tableColumn"
//        i2i_borrower_document_details||doc_parmanent_add_proof
//        password
//        file

//        upload("/storage/emulated/0/Android/data/in.co.cioc.i2i/cache/EasyImage/6ca7818b-0143-4a63-912f-2a82bc046d777339243640262566125.jpg" , "i2i_borrower_document_details||doc_parmanent_add_proof");

        panUpload = findViewById(R.id.panUploadFile);
        aadharUpload = findViewById(R.id.aadharUploadFile);
        permAddress = findViewById(R.id.permAddressUploadFile);
        currentAddress = findViewById(R.id.localAddressUploadFile);
        bankStatement = findViewById(R.id.statement12UploadFile);
        empSalarySlip1= findViewById(R.id.salarySlipLastUploadFile);
        empSalarySlip2 = findViewById(R.id.salarySlip2UploadFile);
        empSalarySlip3 = findViewById(R.id.salarySlip3UploadFile);
//        empForm16 = findViewById(R.id.form16UploadFile);
        empHighestDegree = findViewById(R.id.markSheetUploadFile);
        businessPan = findViewById(R.id.businessPANUploadFile);
        businessCompanyReg = findViewById(R.id.businessRegistrationtUploadFile);
        businessITR1 = findViewById(R.id.businessIncomeTax1UploadFile);
        businessITR2 = findViewById(R.id.businessIncomeTax2UploadFile);
        businessITR3 = findViewById(R.id.businessIncomeTax3UploadFile);
        selfEmpPan = findViewById(R.id.selfempPANUploadFile);
        selfEmpCompanyReg = findViewById(R.id.selfempRegistrationtUploadFile);
        selfEmpITR1= findViewById(R.id.selfempIncomeTax1UploadFile);
        selfEmpITR2= findViewById(R.id.selfempIncomeTax2UploadFile);
        selfEmpITR3 = findViewById(R.id.selfempIncomeTax3UploadFile);
        selfEmpHighestDegree= findViewById(R.id.selfempMarkSheetUploadFile);

        View.OnClickListener uploader = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage(view.getId());
            }
        };

        panUpload.setOnClickListener(uploader);
        aadharUpload.setOnClickListener(uploader);
        permAddress.setOnClickListener(uploader);
        currentAddress.setOnClickListener(uploader);
        bankStatement.setOnClickListener(uploader);
        empSalarySlip1.setOnClickListener(uploader);
        empSalarySlip2.setOnClickListener(uploader);
        empSalarySlip3.setOnClickListener(uploader);
        empHighestDegree.setOnClickListener(uploader);
        businessPan.setOnClickListener(uploader);
        businessCompanyReg.setOnClickListener(uploader);
        businessITR1.setOnClickListener(uploader);
        businessITR2.setOnClickListener(uploader);
        businessITR3.setOnClickListener(uploader);
        selfEmpPan.setOnClickListener(uploader);
        selfEmpCompanyReg.setOnClickListener(uploader);
        selfEmpITR1.setOnClickListener(uploader);
        selfEmpITR2.setOnClickListener(uploader);
        selfEmpITR3.setOnClickListener(uploader);
        selfEmpHighestDegree.setOnClickListener(uploader);
    }

    private void openImage(final Integer elementCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                REQUEST_CODE_DOC = 0;
                if(items[i].equals("Camera")){
                    EasyImage.openCamera(DocumentsActivity.this, elementCode);
                }else if(items[i].equals("Select a file")){
                    REQUEST_CODE_DOC = elementCode;
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setActivityTheme(R.style.Theme_AppCompat).addFileSupport("ZIP", new String[]{".zip",".rar"}, R.drawable.ic_file).enableDocSupport(false)
                            .addFileSupport("PDF",new String[]{".pdf",".PDF"}, R.drawable.ic_file).enableCameraSupport(true)
                            .pickFile(DocumentsActivity.this);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void uploadPassword(String tableColumn, String pass){

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        RequestParams params = new RequestParams();

        params.put("tableColumn", tableColumn);
        params.put("userID","");
        params.put("password", pass);


        client.post(backend.BASE_URL + "/api/v1/saveFile/?csrf_token=" + csrf_token + "&session_id=" + session_id, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    public void upload(String filePath ,final String table, final int id){

        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        RequestParams params = new RequestParams();
        params.put("tableColumn", table);

        try {
            params.put("file", new File(filePath));
        } catch(FileNotFoundException e) {

        }

        uploadedFiles.add(id);
        TextView errTv;
        if (id == R.id.panUploadFile){
            errTv = findViewById(R.id.panUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.aadharUploadFile){
            errTv = findViewById(R.id.aadharUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.permAddressUploadFile){
            errTv = findViewById(R.id.permAddressUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.localAddressUploadFile){
            errTv = findViewById(R.id.localAddressUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.statement12UploadFile){
            errTv = findViewById(R.id.statement12UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.salarySlipLastUploadFile){
            errTv = findViewById(R.id.salarySlipLastUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.salarySlip2UploadFile){
            errTv = findViewById(R.id.salarySlip2UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.salarySlip3UploadFile){
            errTv = findViewById(R.id.salarySlip3UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
//        }else if (id == R.id.form16UploadFile){
//            errTv = findViewById(R.id.form16UploadFileErrTxt);
//            errTv.setText("Succfully uploaded");
//            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.markSheetUploadFile){
            errTv = findViewById(R.id.markSheetUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.businessPANUploadFile){
            errTv = findViewById(R.id.businessPANUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.businessRegistrationtUploadFile){
            errTv = findViewById(R.id.businessRegistrationtUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.businessIncomeTax1UploadFile){
            errTv = findViewById(R.id.businessIncomeTax1UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.businessIncomeTax2UploadFile){
            errTv = findViewById(R.id.businessIncomeTax2UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.businessIncomeTax3UploadFile){
            errTv = findViewById(R.id.businessIncomeTax3UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.selfempPANUploadFile){
            errTv = findViewById(R.id.selfempPANUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.selfempRegistrationtUploadFile){
            errTv = findViewById(R.id.selfempRegistrationtUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.selfempIncomeTax1UploadFile){
            errTv = findViewById(R.id.selfempIncomeTax1UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.selfempIncomeTax2UploadFile){
            errTv = findViewById(R.id.selfempIncomeTax2UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.selfempIncomeTax3UploadFile){
            errTv = findViewById(R.id.selfempIncomeTax3UploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }else if (id == R.id.selfempMarkSheetUploadFile){
            errTv = findViewById(R.id.selfempMarkSheetUploadFileErrTxt);
            errTv.setText("Succfully uploaded");
            errTv.setTextColor(getResources().getColor(R.color.green));
        }

        client.post(backend.BASE_URL + "/api/v1/saveFile/?csrf_token=" + csrf_token + "&session_id=" + session_id, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                String s ="das";

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (REQUEST_CODE_DOC) {

            case R.id.panUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String filePath = filePaths.get(0);

                upload(filePath , "i2i_borrower_document_details||doc_pancard" , R.id.panUploadFile);

                Toast.makeText(DocumentsActivity.this, filePath , Toast.LENGTH_SHORT).show();
                break;

            case R.id.aadharUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String aadharfilePath = filePaths.get(0);
                upload(aadharfilePath , "i2i_borrower_document_details||aadhar_card", R.id.aadharUploadFile);
                Toast.makeText(DocumentsActivity.this, aadharfilePath , Toast.LENGTH_SHORT).show();
                break;

            case R.id.permAddressUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String permaddfilePath = filePaths.get(0);
                upload(permaddfilePath , "i2i_borrower_document_details||doc_parmanent_add_proof", R.id.permAddressUploadFile);
                Toast.makeText(DocumentsActivity.this, permaddfilePath , Toast.LENGTH_SHORT).show();
                break;

            case R.id.localAddressUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String localaddfilePath = filePaths.get(0);
                upload(localaddfilePath , "i2i_borrower_document_details||doc_current_add_proof" , R.id.localAddressUploadFile);
                Toast.makeText(DocumentsActivity.this, localaddfilePath , Toast.LENGTH_SHORT).show();
                break;

            case R.id.statement12UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String statement12UploadFile = filePaths.get(0);
                upload(statement12UploadFile , "i2i_borrower_document_details||doc_account_statement", R.id.statement12UploadFile);
                Toast.makeText(DocumentsActivity.this, statement12UploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.salarySlipLastUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String salarySlipLastUploadFile = filePaths.get(0);
                upload(salarySlipLastUploadFile , "i2i_borrower_document_details||doc_sal_last_month", R.id.salarySlipLastUploadFile);
                Toast.makeText(DocumentsActivity.this, salarySlipLastUploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.salarySlip2UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String salarySlip2UploadFile = filePaths.get(0);
                upload(salarySlip2UploadFile , "i2i_borrower_document_details||doc_sal_2nd_month", R.id.salarySlip2UploadFile);
                Toast.makeText(DocumentsActivity.this, salarySlip2UploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.salarySlip3UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String salarySlip3UploadFile = filePaths.get(0);
                upload(salarySlip3UploadFile , "i2i_borrower_document_details||doc_sal_3rd_month", R.id.salarySlip3UploadFile);
                Toast.makeText(DocumentsActivity.this, salarySlip3UploadFile , Toast.LENGTH_SHORT).show();
                break;
//            case R.id.form16UploadFile:
//                filePaths = new ArrayList<>();
//                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//                String form16UploadFile = filePaths.get(0);
//                upload(form16UploadFile , "i2i_borrower_document_details||doc_form_16", R.id.form16UploadFile);
//                Toast.makeText(DocumentsActivity.this, form16UploadFile , Toast.LENGTH_SHORT).show();
//                break;

            case R.id.markSheetUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String markSheetUploadFile = filePaths.get(0);
                upload(markSheetUploadFile , "i2i_borrower_document_details||doc_marksheet", R.id.markSheetUploadFile);
                Toast.makeText(DocumentsActivity.this, markSheetUploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.businessPANUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String businessPANUploadFile = filePaths.get(0);
                upload(businessPANUploadFile , "i2i_borrower_document_details||doc_com_pancard", R.id.businessPANUploadFile);
                Toast.makeText(DocumentsActivity.this, businessPANUploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.businessRegistrationtUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String businessRegistrationtUploadFile = filePaths.get(0);
                upload(businessRegistrationtUploadFile , "i2i_borrower_document_details||doc_com_reg_doc", R.id.businessRegistrationtUploadFile);
                Toast.makeText(DocumentsActivity.this, businessRegistrationtUploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.businessIncomeTax1UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String businessIncomeTax1UploadFile = filePaths.get(0);
                upload(businessIncomeTax1UploadFile , "i2i_borrower_document_details||doc_return_last", R.id.businessIncomeTax1UploadFile);
                Toast.makeText(DocumentsActivity.this, businessIncomeTax1UploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.businessIncomeTax2UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String businessIncomeTax2UploadFile = filePaths.get(0);
                upload(businessIncomeTax2UploadFile , "i2i_borrower_document_details||doc_return_2nd", R.id.businessIncomeTax2UploadFile);
                Toast.makeText(DocumentsActivity.this, businessIncomeTax2UploadFile , Toast.LENGTH_SHORT).show();
                break;
            case R.id.businessIncomeTax3UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String businessIncomeTax3UploadFile = filePaths.get(0);
                upload(businessIncomeTax3UploadFile , "i2i_borrower_document_details||doc_return_3rd", R.id.businessIncomeTax3UploadFile);
                Toast.makeText(DocumentsActivity.this, businessIncomeTax3UploadFile , Toast.LENGTH_SHORT).show();
                break;
            case R.id.selfempPANUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String selfempPANUploadFile = filePaths.get(0);
                //upload(selfempPANUploadFile , "i2i_borrower_document_details||doc_com_reg_doc", R.id.businessIncomeTax3UploadFile);
                Toast.makeText(DocumentsActivity.this, selfempPANUploadFile , Toast.LENGTH_SHORT).show();
                break;
            case R.id.selfempRegistrationtUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String selfempRegistrationtUploadFile = filePaths.get(0);
                upload(selfempRegistrationtUploadFile , "i2i_borrower_document_details||doc_com_reg_doc", R.id.selfempRegistrationtUploadFile);
                Toast.makeText(DocumentsActivity.this, selfempRegistrationtUploadFile , Toast.LENGTH_SHORT).show();
                break;

            case R.id.selfempIncomeTax1UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String selfempIncomeTax1UploadFile = filePaths.get(0);
                upload(selfempIncomeTax1UploadFile , "i2i_borrower_document_details||doc_return_last", R.id.selfempIncomeTax1UploadFile);
                Toast.makeText(DocumentsActivity.this, selfempIncomeTax1UploadFile , Toast.LENGTH_SHORT).show();
                break;
            case R.id.selfempIncomeTax2UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String selfempIncomeTax2UploadFile = filePaths.get(0);
                upload(selfempIncomeTax2UploadFile , "i2i_borrower_document_details||doc_return_2nd", R.id.selfempIncomeTax2UploadFile);
                Toast.makeText(DocumentsActivity.this, selfempIncomeTax2UploadFile , Toast.LENGTH_SHORT).show();
                break;
            case R.id.selfempIncomeTax3UploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String selfempIncomeTax3UploadFile = filePaths.get(0);
                upload(selfempIncomeTax3UploadFile , "i2i_borrower_document_details||doc_return_3rd", R.id.selfempIncomeTax3UploadFile);
                Toast.makeText(DocumentsActivity.this, selfempIncomeTax3UploadFile , Toast.LENGTH_SHORT).show();
                break;
            case R.id.selfempMarkSheetUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String selfempMarkSheetUploadFile = filePaths.get(0);
                upload(selfempMarkSheetUploadFile , "i2i_borrower_document_details||doc_marksheet", R.id.selfempMarkSheetUploadFile);
                Toast.makeText(DocumentsActivity.this, selfempMarkSheetUploadFile , Toast.LENGTH_SHORT).show();
                break;


        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type){
                    case R.id.panUploadFile:
                        String filePath = imageFile.getAbsolutePath();
                        upload(filePath , "i2i_borrower_document_details||doc_pancard", R.id.panUploadFile);
                        Toast.makeText(DocumentsActivity.this, filePath , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.aadharUploadFile:
                        String aadharUploadFile = imageFile.getAbsolutePath();
                        upload(aadharUploadFile , "i2i_borrower_document_details||aadhar_card", R.id.aadharUploadFile);
                        Toast.makeText(DocumentsActivity.this, aadharUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.permAddressUploadFile:
                        String permAddressUploadFile = imageFile.getAbsolutePath();
                        upload(permAddressUploadFile , "i2i_borrower_document_details||doc_parmanent_add_proof", R.id.permAddressUploadFile);
                        Toast.makeText(DocumentsActivity.this, permAddressUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.localAddressUploadFile:
                        String localAddressUploadFile = imageFile.getAbsolutePath();
                        upload(localAddressUploadFile , "i2i_borrower_document_details||doc_current_add_proof", R.id.localAddressUploadFile);
                        Toast.makeText(DocumentsActivity.this, localAddressUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.statement12UploadFile:
                        String statement12UploadFile = imageFile.getAbsolutePath();
                        upload(statement12UploadFile , "i2i_borrower_document_details||doc_account_statement", R.id.statement12UploadFile);
                        Toast.makeText(DocumentsActivity.this, statement12UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.salarySlipLastUploadFile:
                        String salarySlipLastUploadFile = imageFile.getAbsolutePath();
                        upload(salarySlipLastUploadFile , "i2i_borrower_document_details||doc_sal_last_month", R.id.salarySlipLastUploadFile);
                        Toast.makeText(DocumentsActivity.this, salarySlipLastUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.salarySlip2UploadFile:
                        String salarySlip2UploadFile = imageFile.getAbsolutePath();
                        upload(salarySlip2UploadFile , "i2i_borrower_document_details||doc_sal_2nd_month", R.id.salarySlip2UploadFile);
                        Toast.makeText(DocumentsActivity.this, salarySlip2UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.salarySlip3UploadFile:
                        String salarySlip3UploadFile = imageFile.getAbsolutePath();
                        upload(salarySlip3UploadFile , "i2i_borrower_document_details||doc_sal_3rd_month", R.id.salarySlip3UploadFile);
                        Toast.makeText(DocumentsActivity.this, salarySlip3UploadFile , Toast.LENGTH_SHORT).show();
                        break;
//                    case R.id.form16UploadFile:
//                        String form16UploadFile = imageFile.getAbsolutePath();
//                        //upload(form16UploadFile , "i2i_borrower_document_details||doc_parmanent_add_proof", R.id.form16UploadFile);
//                        Toast.makeText(DocumentsActivity.this, form16UploadFile , Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.markSheetUploadFile:
                        String markSheetUploadFile = imageFile.getAbsolutePath();
                        upload(markSheetUploadFile , "i2i_borrower_document_details||doc_marksheet", R.id.markSheetUploadFile);
                        Toast.makeText(DocumentsActivity.this, markSheetUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.businessPANUploadFile:
                        String businessPANUploadFile = imageFile.getAbsolutePath();
                        upload(businessPANUploadFile , "i2i_borrower_document_details||doc_com_pancard", R.id.businessPANUploadFile);
                        Toast.makeText(DocumentsActivity.this, businessPANUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.businessRegistrationtUploadFile:
                        String businessRegistrationtUploadFile = imageFile.getAbsolutePath();
                        upload(businessRegistrationtUploadFile , "i2i_borrower_document_details||doc_com_reg_doc", R.id.businessRegistrationtUploadFile);
                        Toast.makeText(DocumentsActivity.this, businessRegistrationtUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.businessIncomeTax1UploadFile:
                        String businessIncomeTax1UploadFile = imageFile.getAbsolutePath();
                        upload(businessIncomeTax1UploadFile , "i2i_borrower_document_details||doc_return_last", R.id.businessIncomeTax1UploadFile);
                        Toast.makeText(DocumentsActivity.this, businessIncomeTax1UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.businessIncomeTax2UploadFile:
                        String businessIncomeTax2UploadFile = imageFile.getAbsolutePath();
                        upload(businessIncomeTax2UploadFile , "i2i_borrower_document_details||doc_return_2nd", R.id.businessIncomeTax2UploadFile);
                        Toast.makeText(DocumentsActivity.this, businessIncomeTax2UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.businessIncomeTax3UploadFile:
                        String businessIncomeTax3UploadFile = imageFile.getAbsolutePath();
                        upload(businessIncomeTax3UploadFile , "i2i_borrower_document_details||doc_return_3rd", R.id.businessIncomeTax3UploadFile);
                        Toast.makeText(DocumentsActivity.this, businessIncomeTax3UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.selfempPANUploadFile:
                        String selfempPANUploadFile = imageFile.getAbsolutePath();
                        upload(selfempPANUploadFile , "i2i_borrower_document_details||doc_com_pancard", R.id.selfempPANUploadFile);
                        Toast.makeText(DocumentsActivity.this, selfempPANUploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.selfempRegistrationtUploadFile:
                        String selfempRegistrationtUploadFile = imageFile.getAbsolutePath();
                        upload(selfempRegistrationtUploadFile , "i2i_borrower_document_details||doc_com_reg_doc", R.id.selfempRegistrationtUploadFile);
                        Toast.makeText(DocumentsActivity.this, selfempRegistrationtUploadFile , Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.selfempIncomeTax1UploadFile:
                        String selfempIncomeTax1UploadFile = imageFile.getAbsolutePath();
                        upload(selfempIncomeTax1UploadFile , "i2i_borrower_document_details||doc_return_last", R.id.selfempIncomeTax1UploadFile);
                        Toast.makeText(DocumentsActivity.this, selfempIncomeTax1UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.selfempIncomeTax2UploadFile:
                        String selfempIncomeTax2UploadFile = imageFile.getAbsolutePath();
                        upload(selfempIncomeTax2UploadFile , "i2i_borrower_document_details||doc_return_2nd", R.id.selfempIncomeTax2UploadFile);
                        Toast.makeText(DocumentsActivity.this, selfempIncomeTax2UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.selfempIncomeTax3UploadFile:
                        String selfempIncomeTax3UploadFile = imageFile.getAbsolutePath();
                        upload(selfempIncomeTax3UploadFile , "i2i_borrower_document_details||doc_return_3rd", R.id.selfempIncomeTax3UploadFile);
                        Toast.makeText(DocumentsActivity.this, selfempIncomeTax3UploadFile , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.selfempMarkSheetUploadFile:
                        String selfempMarkSheetUploadFile = imageFile.getAbsolutePath();
                        upload(selfempMarkSheetUploadFile , "i2i_borrower_document_details||doc_marksheet", R.id.selfempMarkSheetUploadFile);
                        Toast.makeText(DocumentsActivity.this, selfempMarkSheetUploadFile , Toast.LENGTH_SHORT).show();
                        break;


                }
            }
        });
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


    public void finalSubmit(View view){

//        permAddressDocType	House Tax Receipt
//        currentAddressDocType	Landlord Declaration
//        authorize	true
//        agree	true
//        eKYCpan	true
//        eKYCaadhar	true
//        showErrors	true
//        http://localhost:8080/api/v1/borrowerRegistration/finalSubmit/?csrf_token=PvKMr0O9JCGaS1yO52GxSnMtG&session_id=6jJoBMIsZOMsTu47TTZ7jd4Fm

        if (uploadedFiles.size()<10 && empType == "Salaried Employee"){
            Toast.makeText(this, "Please uploaded all the files", Toast.LENGTH_SHORT).show();
            return;
        } else if (uploadedFiles.size()<11 && empType == "Self Employed Professional"){
            Toast.makeText(this, "Please uploaded all the files", Toast.LENGTH_SHORT).show();
            return;
        } else if (uploadedFiles.size()<9){
            Toast.makeText(this, "Please uploaded all the files", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!agreeCB.isChecked()) {
            tncCBErr.setVisibility(View.VISIBLE);
            tncCBErr.setText("Please read and agree to our terms and conditions.");
            return;
        } else {
            tncCBErr.setVisibility(View.GONE);
        }

        if (!authorizeCB.isChecked()) {
            personalCBErr.setVisibility(View.VISIBLE);
            personalCBErr.setText("Please read and agree to our privacy policy terms.");
            return;
        } else {
            personalCBErr.setVisibility(View.GONE);
        }


        final String session_id = sharedPreferences.getString("session_id" , null);
        final String csrf_token = sharedPreferences.getString("csrf_token" , null);

        RequestParams params = new RequestParams();

        if(dropdownCurrent.getSelectedItem().toString().equals("Please select") ||dropdownPerm.getSelectedItem().toString().equals("Please select") ){
            Toast.makeText(this, "Please select the address type", Toast.LENGTH_SHORT).show();
            return;
        }


        params.put("permAddressDocType", dropdownPerm.getSelectedItem().toString());
        params.put("currentAddressDocType", dropdownCurrent.getSelectedItem().toString());
        params.put("authorize", true);
        params.put("agree", true);
        params.put("eKYCpan", true);

        if (!agreeCB.isChecked()) {
            tncCBErr.setVisibility(View.VISIBLE);
            tncCBErr.setText("Please read and agree to our terms and conditions.");
        } else {
            tncCBErr.setVisibility(View.GONE);
            if (!authorizeCB.isChecked()) {
                personalCBErr.setVisibility(View.VISIBLE);
                personalCBErr.setText("Please read and agree to our privacy policy terms.");
            } else {
                personalCBErr.setVisibility(View.GONE);
                client.post(backend.BASE_URL + "/api/v1/borrowerRegistration/finalSubmit/?csrf_token=" + csrf_token + "&session_id=" + session_id, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);
//              http://localhost:8080/api/v1/logout/?csrf_token=PvKMr0O9JCGaS1yO52GxSnMtG&session_id=6jJoBMIsZOMsTu47TTZ7jd4Fm
                        client.post(backend.BASE_URL + "/api/v1/logout/?csrf_token=" + csrf_token + "&session_id=" + session_id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                                super.onSuccess(statusCode, headers, c);
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            }

                        });

                    }

                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
