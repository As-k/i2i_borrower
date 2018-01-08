package in.co.cioc.i2i;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.githang.stepview.StepView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class DocumentsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CAMERA = 0012;

    private String [] items = {"Camera","Select a file"};

    private ArrayList<String> filePaths = new ArrayList<>();

    private Spinner dropdownPerm , dropdownCurrent;

    private ImageView panUpload , aadharUpload , permAddress , currentAddress , bankStatement;
    private ImageView empSalarySlip1, empSalarySlip2 , empSalarySlip3 , empForm16 , empHighestDegree ;
    private ImageView businessPan , businessCompanyReg , businessITR1 , businessITR2, businessITR3 ;
    private ImageView selfEmpPan , selfEmpCompanyReg, selfEmpITR1 , selfEmpITR2, selfEmpITR3 , selfEmpHighestDegree;

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
        String[] items = new String[]{"House Tax Receipt", "Electricity Bill", "Registration Document" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownPerm.setAdapter(adapter);
        dropdownCurrent.setAdapter(adapter);


        panUpload = findViewById(R.id.panUploadFile);
        aadharUpload = findViewById(R.id.panUploadFile);
        permAddress = findViewById(R.id.panUploadFile);
        currentAddress = findViewById(R.id.panUploadFile);
        bankStatement = findViewById(R.id.panUploadFile);
        empSalarySlip1= findViewById(R.id.panUploadFile);
        empSalarySlip2 = findViewById(R.id.panUploadFile);
        empSalarySlip3 = findViewById(R.id.panUploadFile);
        empForm16 = findViewById(R.id.panUploadFile);
        empHighestDegree = findViewById(R.id.panUploadFile);
        businessPan = findViewById(R.id.panUploadFile);
        businessCompanyReg = findViewById(R.id.panUploadFile);
        businessITR1 = findViewById(R.id.panUploadFile);
        businessITR2 = findViewById(R.id.panUploadFile);
        businessITR3 = findViewById(R.id.panUploadFile);
        selfEmpPan = findViewById(R.id.panUploadFile);
        selfEmpCompanyReg = findViewById(R.id.panUploadFile);
        selfEmpITR1= findViewById(R.id.panUploadFile);
        selfEmpITR2= findViewById(R.id.panUploadFile);
        selfEmpITR3 = findViewById(R.id.panUploadFile);
        selfEmpHighestDegree= findViewById(R.id.panUploadFile);

        View.OnClickListener uploader = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean yes = view.getId() == R.id.panUploadFile;

                openImage(view.getId());

                Toast.makeText(DocumentsActivity.this, "das", Toast.LENGTH_SHORT).show();
            }
        };

        panUpload.setOnClickListener(uploader);

    }

    private void openImage(final Integer elementCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (REQUEST_CODE_DOC)
        {

            case R.id.panUploadFile:
                filePaths = new ArrayList<>();
                filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                Toast.makeText(DocumentsActivity.this, filePaths.get(0), Toast.LENGTH_SHORT).show();
                break;
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                switch (type){
                    case R.id.panUploadFile:
                        Toast.makeText(DocumentsActivity.this, imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }
}
