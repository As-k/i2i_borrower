package in.co.cioc.i2i;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();
    SharedPreferences sharedPreferences;
    Drawable successTick;

    public static final int REQUEST_CODE_CAMERA = 0012;

    private String [] items = {"Camera","Select a file"};

    private ArrayList<String> filePaths = new ArrayList<>();

    private Spinner dropdownPerm , dropdownCurrent;

    private ImageView panUpload , aadharUpload , permAddress , currentAddress , bankStatement;
    private ImageView empSalarySlip1, empSalarySlip2 , empSalarySlip3 , empForm16 , empHighestDegree ;
    private ImageView businessPan , businessCompanyReg , businessITR1 , businessITR2, businessITR3 ;
    private ImageView selfEmpPan , selfEmpCompanyReg, selfEmpITR1 , selfEmpITR2, selfEmpITR3 , selfEmpHighestDegree;

    private TextView pan , aadhar;
    LinearLayout businessForm , salariedForm , selfEmpForm;
    CheckBox agreeCB , authorizeCB;


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

        agreeCB = findViewById(R.id.agree);
        authorizeCB = findViewById(R.id.authorize);

        pan = findViewById(R.id.pan);
        aadhar = findViewById(R.id.aadhar);

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
        empForm16 = findViewById(R.id.form16UploadFile);
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

    public void upload(String filePath , String table){



        String session_id = sharedPreferences.getString("session_id" , null);
        String csrf_token = sharedPreferences.getString("csrf_token" , null);

        RequestParams params = new RequestParams();
        params.put("tableColumn", table);

        try {
            params.put("file", new File(filePath));
        } catch(FileNotFoundException e) {

        }

        client.post(backend.BASE_URL + "/api/v1/saveFile/?csrf_token=" + csrf_token + "&session_id=" + session_id, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                String s ="das";

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (REQUEST_CODE_DOC)
        {

            case R.id.panUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                String filePath = filePaths.get(0);
                Toast.makeText(DocumentsActivity.this, filePath , Toast.LENGTH_SHORT).show();
                break;
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type){
                    case R.id.panUploadFile:
                        String filePath = imageFile.getAbsolutePath();
                        Toast.makeText(DocumentsActivity.this, filePath , Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
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



        if (!agreeCB.isChecked()){
            agreeCB.setError("Please agree to our Terms and Conditions");
            agreeCB.requestFocus();
            return;
        }
        if (!authorizeCB.isChecked()){
            authorizeCB.setError("Please authorize us to validate your profile");
            authorizeCB.requestFocus();
            return;
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
