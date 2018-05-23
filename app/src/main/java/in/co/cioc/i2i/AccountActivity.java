package in.co.cioc.i2i;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;

import static in.co.cioc.i2i.LoginActivity.PREFS_NAME;
import static in.co.cioc.i2i.LoginActivity.PREF_FNAME;
import static in.co.cioc.i2i.LoginActivity.PREF_PASSWORD;

public class AccountActivity extends AppCompatActivity {

    private static AsyncHttpClient client = new AsyncHttpClient(true , 80, 443);
    Backend backend = new Backend();

    SharedPreferences sharedPreferences;
    TextView name , userID, logoutBtn , couponCodeStatus;
    Button continueBtn , payNPost;
    Integer reg_stage;
    ProgressDialog progress;

//    from the old app
    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    Bundle initial_bundle;
    String usr_id, usr_phone, usr_email, usr_name, full_name;
    String otp;
    ProgressDialog dialog;
    ProgressDialog dialog1;
    int sendc = 0, sends = 0, sends2 = 0, sendm = 0;

    JSONArray json_location;
    List<Thread> allThreads = new ArrayList<Thread>();
    final ThreadPoolExecutor executor = new ThreadPoolExecutor(NUMBER_OF_CORES * 2, NUMBER_OF_CORES * 2, 60L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
    final ThreadPoolExecutor executor2 = new ThreadPoolExecutor(NUMBER_OF_CORES * 2, NUMBER_OF_CORES * 2, 60L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
    JSONObject finalsms;
    JSONArray smsarr;
    int cs = 0;
    int ce = 1000;
    int ss = 0;
    int se = 200;
    int cas = 0;
    int cae = 300;
    int rcounter = 1000;
    JSONArray contactsarr;
    JSONArray callarr;
    String strDate = "";
    String strdatef = " ";
    com.android.volley.RequestQueue mRequestQueue;
    LinearLayout sl;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    double longitudeGPS, latitudeGPS;
    double longitudeNetwork, latitudeNetwork;
    AVLoadingIndicatorView avi;


    public static String PREFS_NAME = "mypre";
    public static String PREF_FNAME2 = "fname";
    public static String PREF_PASSWORD = "password";
    public static String PREF_FNAME = "c41586290";


    LinearLayout accountView , continueView;

    Boolean posted = false;
    Boolean promoCodeValid = false;

    LinearLayout promoCodeLayout , promocodeControls;
    ScrollView account_scroll;


    public void oldMain(){
        Intent intent = getIntent();
        initial_bundle = intent.getExtras();
        finalsms = new JSONObject();
        smsarr = new JSONArray();
        contactsarr = new JSONArray();
        // Instantiate the RequestQueue with the cache and network.
        // Instantiate the cache
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Cache cache = new DiskBasedCache(getCacheDir(), 2 * 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new com.android.volley.RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();
        callarr = new JSONArray();

//        usr_id = initial_bundle.getString("user");
//        usr_phone = initial_bundle.getString("user_phone");
//        usr_email = initial_bundle.getString("user_email");
//        full_name = initial_bundle.getString("user_fname") + " " + initial_bundle.getString("user_lname");



        sendc = 0;
        sends = 0;
        sendm = 0;
        usr_name = full_name;
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString("uid", usr_id)
                .putString("un", full_name)
                .putString("ue", usr_email)
                .putString("up", usr_phone)
                .commit();

        String indicator = getIntent().getStringExtra("indicator");
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String con = pref.getString(PREF_FNAME, null);
        int GET_MY_PERMISSION = 1;
        checkpermission();

        startv a = new startv();
        a.execute();
    }

    public void onRadioButtonClickedMarital(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes:
                if (checked)
                    promoCodeLayout.setVisibility(LinearLayout.VISIBLE);
                    break;
            case R.id.radio_no:
                if (checked)
                    promoCodeLayout.setVisibility(LinearLayout.GONE);
                    break;
        }
    }


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

        account_scroll = findViewById(R.id.account_scroll);
        promoCodeLayout = findViewById(R.id.promoCodeLayout);
        promoCodeLayout.setVisibility(LinearLayout.GONE);

        couponCodeStatus = findViewById(R.id.couponCodeStatus);
        promocodeControls = findViewById(R.id.promocodeControls);

        name = findViewById(R.id.name);
        userID = findViewById(R.id.userID);
        logoutBtn = findViewById(R.id.logout);
        continueBtn = findViewById(R.id.continue_button);
        payNPost = findViewById(R.id.payNPost);
        continueBtn.setVisibility(Button.GONE);

        payNPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pay the fees

                if (promoCodeValid){
                    // make the post request
                }else{
                    // start payumoney
                    pay();

                }

            }
        });

        accountView  = findViewById(R.id.accountView);
        continueView = findViewById(R.id.continueLayout);

        accountView.setVisibility(LinearLayout.GONE);
        continueView.setVisibility(LinearLayout.GONE);


        final Button couponApplyBtn = findViewById(R.id.couponApply);
        couponApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText promo = findViewById(R.id.couponCode);
                String promoTxt = promo.getText().toString();

                if (promoTxt.length() == 0){
                    Toast.makeText(AccountActivity.this, "Please enter a promo code to apply", Toast.LENGTH_SHORT).show();
                    return;
                }

                couponCodeStatus.setVisibility(TextView.VISIBLE);
                if (promoTxt.equals("FEE")){
                    promoCodeValid = true;
                    promoCodeLayout.setVisibility(LinearLayout.GONE);
                    couponCodeStatus.setTextColor(getResources().getColor(R.color.green));
                    couponCodeStatus.setText("Coupon code applied");
                    promocodeControls.setVisibility(LinearLayout.GONE);
                    payNPost.setText("Post your loan");
                }else{
                    promoCodeLayout.setVisibility(LinearLayout.VISIBLE);
                    couponCodeStatus.setTextColor(getResources().getColor(R.color.cb_errorRed));
                    couponCodeStatus.setText("Invalid promo code");
                }

            }
        });

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
                            accountView.setVisibility(LinearLayout.GONE);
                            continueView.setVisibility(LinearLayout.VISIBLE);
                        }else{
                            accountView.setVisibility(LinearLayout.VISIBLE);
                            continueView.setVisibility(LinearLayout.GONE);
                        }

                        usr_id = id.toString();
                        usr_phone = c.getString("phone");
                        usr_email = c.getString("email");
                        usr_name = firstName;
                        full_name = firstName+" " + middleName + " " + lastName;

                        name.setText(firstName + "  " + lastName);
                        userID.setText(id.toString());

                        reg_stage = c.getInt("reg_stage");
                        oldMain();

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


    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public void checkpermission(View view) {
        checkpermission();
    }

    public void checkpermission() {
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED || !isLocationEnabled()) {

            ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_MULTIPLE_REQUEST);
            checkLocation();
        } else {
//            sl.setVisibility(View.VISIBLE); // check message below
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_CALL_LOG)
                            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
//                        sl.setVisibility(View.VISIBLE); // after getting the permissions , show a button to recieve the otp
                    }
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    // CIOC : Call this success button to start initiating the verification


    private void showDialogMessage(String message, String message2) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setMessage(message2);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    public static String getMonthInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return (dateFormat.format(date));
    }

    public static String getYearInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);
    }

    public static String getdayInt(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

    public List<Sms> getAllSms(String folderName) {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/" + folderName);
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        int totalSMS = c.getCount();
        int hcp = 0;
        //Log.d("sas",String.valueOf(totalSMS)+folderName);
        if (ss > se) {
            sends = 1;
            sends2 = 1;
            return null;
        }
        if (totalSMS > ss + 100) {
            if (folderName.equals("inbox"))
                sends = 0;
            else
                sends2 = 0;
        }
        if (c.moveToFirst() && ss <= totalSMS) {
            for (int i = 0; i < Math.min(300, totalSMS); i++) {
                objSms = new Sms();
                //	Log.d("asa", c.toString());
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                String cid = c.getString(c.getColumnIndexOrThrow("_id"));
                objSms.setAddress(c.getString(c
                        .getColumnIndexOrThrow("address")));
                String cadd = c.getString(c.getColumnIndexOrThrow("address"));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                String cbody = c.getString(c.getColumnIndexOrThrow("body"));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                String cread = c.getString(c.getColumnIndexOrThrow("read"));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                String cdate = c.getString(c.getColumnIndexOrThrow("date"));
                //	Log.d("date", cdate);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String day = "-", month = "-", year = "-";
                String date2 = "";
                try {
                    Date date = new Date(Long.parseLong(cdate));
                    day = getdayInt(date);
                    month = getMonthInt(date);
                    year = getYearInt(date);
                    date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lstSms.add(objSms);
                c.moveToNext();
                if (!(i >= ss && i < ss + 100))
                    continue;
                final String fcname = cid, fcphone = cadd, fcemail = cbody, fcstreet = cread, fccity = day, fcpostal = month, fclocation = year;
                String sy = usr_phone, sy2 = fcphone;
                if (folderName.equals("inbox")) {
                    sy2 = usr_phone;
                    sy = fcphone;
                }
                final String x1 = sy;
                final String x2 = sy2;
                JSONObject j1 = new JSONObject();
                try {
                    j1.put("user_id", usr_id);
                    j1.put("Timestamp", strdatef);
                    //Log.d("times","time "+strdatef);
                    j1.put("OwnerName", usr_name);
                    j1.put("OwnerPhone", usr_phone);
                    j1.put("OwnerEmail", usr_email);
                    j1.put("MessageID", fcname);
                    j1.put("SenderAddress", x1);
                    j1.put("ReceiverAddress", x2);
                    j1.put("Message", fcemail);
                    j1.put("ReadState", fcstreet);
                    //Log.d("asas","date: "+date2);
                    j1.put("Date", date2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //executor.execute(new sms2(fcname, x1, x2, fcemail, fcstreet, fccity, fcpostal, fclocation));
                if (rcounter > 0) {
                    rcounter--;
                    smsarr.put(j1);
                }
            }

        }

        // else {
        // throw new RuntimeException("You have no SMS in " + folderName);
        // }
        c.close();
        return lstSms;
    }

    private String getCallDetails(Context context) {
        final StringBuffer stringBuffer = new StringBuffer();
        final StringBuffer stringBufferp = new StringBuffer();
        final StringBuffer stringBuffert = new StringBuffer();
        final StringBuffer stringBufferda = new StringBuffer();
        final StringBuffer stringBufferdu = new StringBuffer();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return "permission_not_granted";
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int count = 0;
        int max = 200;
        while (cas < cae && cursor.moveToNext()) {
            count++;
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String calltime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(callDayTime);
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            JSONObject jh = new JSONObject();
            try {
                jh.put("user_id", usr_id);
                jh.put("Timestamp", strdatef);
                jh.put("OwnerName", usr_name);
                jh.put("OwnerPhone", usr_phone);
                jh.put("OwnerEmail", usr_email);
                jh.put("PhoneNumber", phNumber);
                jh.put("CallType", dir);
                //Log.d("call","calldate"+calltime);
                jh.put("CallDateTime", calltime);
                jh.put("CallDuration", callDuration);
                if (count >= cas && count < cas + 100)
                    callarr.put(jh);
                else
                    sendm = 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            stringBuffer.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            stringBufferp.append(phNumber + '\n');
            stringBuffert.append(dir + '\n');
            stringBufferda.append(callDayTime + "\n");
            stringBufferdu.append(callDuration + '\n');
            stringBuffer.append("\n----------------------------------");
        }
        cursor.close();
        cas += 100;
        if (callarr.length() == 0) {
            sendbmail bm = new sendbmail();
            bm.execute();
            return null;
        } else if (!isJSONValid(callarr.toString())) {
            if (sendm == 0) {
                addingcalls();
            } else {
                sendbmail bm = new sendbmail();
                bm.execute();

            }
            return null;
        } else {
            final JSONArray fcallarr = callarr;
            //executor2.execute(new call(stringBufferp.toString(),stringBuffert.toString(),stringBufferda.toString(),stringBufferdu.toString(),""));
            String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonecalllog";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //		Log.d("resp3",response);
                    //appendLog("\n");
                    //appendLog(String.valueOf(response.toString()));

                    if (sends == 1 && sends2 == 1 && sendc == 1 && sendm == 1) {
                        sendbmail bm = new sendbmail();
                        bm.execute();
                    } else if (sendm == 0)
                        addingcalls();
                    else {
                        sendbmail bm = new sendbmail();
                        bm.execute();

                    }

                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  Log.d("call",String.valueOf(error));
                    //appendLog("\n");
                    //appendLog(String.valueOf(error));
                    sendbmail bm = new sendbmail();
                    bm.execute();

                    //This code is executed if there is an error.
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("calllog", fcallarr.toString()); //Add the data you'd like to send to the server.
                    //appendLog("\n");
                    //appendLog(String.valueOf(fcallarr.toString()));
                    return MyData;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                    return params;
                }
            };

            MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(MyStringRequest);
        }

        return stringBuffer.toString();
    }

    public void openalert(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AccountActivity.this);
        builder1.setMessage("Are you sure want to logout");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit().remove(PREF_FNAME2).remove(PREF_PASSWORD).apply();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    void addingcontacts() {
        contactsarr = new JSONArray();
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            synccontacts();
            //t.start();
        }
        //Log.d("sasa",String.valueOf(contactsarr));

    }

    void addingsms() {
        sends = 1;
        smsarr = new JSONArray();
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            getAllSms("inbox");
        }
        if (!isJSONValid(smsarr.toString())) {
            if (sends == 0) {
                ss += 100;
                addingsms();
            } else {
                ss = 0;
                addingsms2();
            }
            return;
        }
        final JSONArray fsmsarr = smsarr;
        if (fsmsarr.length() == 0) {
            ss = 0;
            addingsms2();
            return;
        } else {
            String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonesms";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //  Log.d("resp2",response);
                    //appendLog("\n");
                    //appendLog(String.valueOf(response));
                    if (sends == 1 && sends2 == 1 && sendc == 1 && sendm == 1) {
                        sendbmail bm = new sendbmail();
                        bm.execute();
                    } else if (sends == 0) {
                        ss += 100;
                        addingsms();
                    } else {
                        ss = 0;
                        addingsms2();
                    }
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //appendLog("\n");
                    //appendLog(String.valueOf(error));
                    //Log.d("sms2",String.valueOf(error));
                    ss = 0;
                    addingsms2();
                    return;
                    //This code is executed if there is an error.
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("sms", smsarr.toString()); //Add the data you'd like to send to the server.
                    //appendLog("\n");
                    //appendLog(smsarr.toString());
                    return MyData;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");


                    return params;
                }
            };


            MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            //	logLargeString("sms",smsarr.toString());
            mRequestQueue.add(MyStringRequest);
        }
    }

    public void logLargeString(String TAG, String str) {
        if (str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(TAG, str.substring(3000));
        } else {
            Log.i(TAG, str); // continuation
        }
    }

    void addingsms2() {
        sends2 = 1;
        smsarr = new JSONArray();
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            getAllSms("sent");

        }
        if (!isJSONValid(smsarr.toString())) {
            if (sends2 == 0) {
                ss += 100;
                addingsms2();
            } else {
                addingcalls();
            }
            return;
        }
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonesms";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("resp2",response);
                if (sends == 1 && sends2 == 1 && sendc == 1 && sendm == 1) {
                    sendbmail bm = new sendbmail();
                    bm.execute();
                } else if (sends2 == 0) {
                    ss += 100;
                    addingsms2();
                } else {
                    addingcalls();
                }
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                addingcalls();
                //Log.d("sms",String.valueOf(error));
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("sms", String.valueOf(smsarr)); //Add the data you'd like to send to the server.
                return MyData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }
        };

        //logLargeString("smsarr2",String.valueOf(smsarr));
        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(MyStringRequest);
    }

    void addingcalls() {
        callarr = new JSONArray();
        sendm = 1;
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            getCallDetails(this);
        }
//        Log.d("sasa",String.valueOf(callarr));

    }

    void syncallcontacts() {
        int SPLASH_TIME_OUT = 10000;
        //time a = new time();
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString("verified", "done")
                .apply();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        strDate = sdf.format(c.getTime());
        Date dateStart;
        try {
            dateStart = sdf2.parse(strDate);
            strdatef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//		Log.d("asas",strDate);
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString("strdate", strDate).apply();
        //a.execute();
        SharedPreferences pref2 = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String hg = pref2.getString("i2ir", null);
        if (hg == null) {

            startService(new Intent(this, BackServicce.class));
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString("i2ir", "asa").apply();
        }
        try {
            uploadphoneinfo();
            getPackages();
            locationupdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addingcontacts();

    }

    void synccontacts() {
        sendc = 1;
        contactsarr = new JSONArray();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        String cname;
        String cphone;
        String cemail;
        String cstreet;
        String ccity;
        String cpostal;
        String clocation;
        int counter = 0;
        int tesu = 0;
        if (ce < cs) {
            addingsms();
            return;
        }
        if (cur.getCount() <= cs) {
            addingsms();
            return;
        }

        if (cur.getCount() > 0) {
            tesu = 0;
            //  if(cs+150<cur.getCount()){
            //     sendc=0;
            //}
            while (cur.moveToNext()) {
                counter++;

                if (!(counter >= cs && counter < cs + 150))
                    continue;

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //Log.d("name",": "+name);
                cphone = "";
                cemail = "";
                clocation = "null";
                cstreet = "";
                ccity = "";
                cpostal = "";
                cname = name;
                JSONArray jo = new JSONArray();
                JSONArray jo1 = new JSONArray();
                JSONObject conc = new JSONObject();
                int counterp = 0;
                String PhoneNumberOne = "";
                String PhoneNumberTwo = "";
                String PhoneNumberThree = "";
                String EmailOne = "";
                String EmailTwo = "";
                String EmailThree = "";

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Query phone here.  Covered next
                    String contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        String[] PHONES_PROJECTION = new String[]{"_id", "display_name", "data1", "data3"};//
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                        while (pCur.moveToNext()) {
                            // Do something with phones
                            counterp++;

                            if (counterp == 1)
                                PhoneNumberOne = pCur.getString(2);
                            else if (counterp == 2)
                                PhoneNumberTwo = pCur.getString(2);
                            else
                                PhoneNumberThree = pCur.getString(2);
                            //jo.put(pCur.getString(2));

                            //          Log.d("ss",pCur.getString(2));
                        }
                        pCur.close();
                    }
                    int countere = 0;
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        countere++;
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        //   Log.d("email2",": "+email);
                        if (countere == 1)
                            EmailOne = "";
                        else if (countere == 2)
                            EmailTwo = "";
                        else
                            EmailThree = "";
                        //jo1.put(email);
                        String ee = "Email" + String.valueOf(countere);
                        try {
                            conc.put(ee, email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cemail += email + '\n';

                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    }
                    emailCur.close();
                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, addrWhere, addrWhereParams, null);
                    while (addrCur.moveToNext()) {
                        String poBox = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        cstreet += street + '\n';
                        String city = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        ccity += city + '\n';
                        //    Log.d("city",city+"assa");
                        String state = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        cpostal += postalCode + '\n';
                        String country = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                    }
                    addrCur.close();
                }
                try {
                    conc.put("user_id", usr_id);
                    conc.put("Timestamp", strdatef);
                    conc.put("OwnerName", usr_name);
                    conc.put("OwnerPhone", usr_phone);
                    conc.put("OwnerEmail", usr_email);
                    conc.put("PhoneNumberOne", PhoneNumberOne);
                    conc.put("PhoneNumberTwo", PhoneNumberTwo);
                    conc.put("PhoneNumberThree", PhoneNumberThree);
                    conc.put("EmailOne", EmailOne);
                    conc.put("EmailTwo", EmailTwo);
                    conc.put("EmailThree", EmailThree);
                    conc.put("Name", cname);
                    conc.put("Street", cstreet);
                    conc.put("City", ccity);
                    conc.put("PostalCode", cpostal);
                    conc.put("Location", clocation);
                    if (counter >= cs && counter < cs + 150)
                        contactsarr.put(conc);
                    else if (counter > cs + 150)
                        sendc = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String fcname = cname, fcphone = cphone, fcemail = cemail, fcstreet = cstreet, fccity = ccity, fcpostal = cpostal, fclocation = clocation;
                //executor2.execute(new contacts(fcname, fcphone, fcemail, fcstreet, fccity, fcpostal, fclocation));
            }
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_FNAME, "done")
                    .commit();
        }
        cs += 150;
        JSONArray ef = new JSONArray();
        //Log.d("sss",ef.toString()+ef.length());
        final JSONArray fcontactsarr = contactsarr;
        if (fcontactsarr.length() == 0) {
            sendc = 1;
            addingsms();
            return;
        } else if (!isJSONValid(fcontactsarr.toString())) {
            if (sendc == 0) {
                addingcontacts();
            } else {
                addingsms();
            }
            return;
        } else {
            String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonecontacts";
            StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("resp1", response);
                    //appendLog("\n");
                    //appendLog(String.valueOf(response));
                    if (sends == 1 && sends2 == 1 && sendc == 1 && sendm == 1) {
                        sendbmail bm = new sendbmail();
                        bm.execute();
                    } else if (sendc == 0) {
                        addingcontacts();
                    } else {
                        addingsms();
                    }
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("contacts", String.valueOf(error));
                    //appendLog("\n");
                    //appendLog(String.valueOf(error));
                    addingsms();
                    //This code is executed if there is an error.
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("contacts", fcontactsarr.toString()); //Add the data you'd like to send to the server.
                    //appendLog("\n");
                    //appendLog(contactsarr.toString());

                    return MyData;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");


                    return params;
                }
            };
            //Log.d("sasa",String.valueOf(smsarr));
            //Log.d("sasa",String.valueOf(callarr));

            MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(MyStringRequest);
        }
    }

    public void appendLog2(String text) {
        File logFile = new File("sdcard/logsat.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class contacts implements Runnable {
        private String col1;
        private String col2;
        private String col3;
        private String col4;
        private String col5;
        private String col6;
        private String col7;

        public contacts(String name, String phone, String email, String street, String city, String postal, String location) {
            col1 = name;
            col2 = phone;
            col3 = email;
            col4 = street;
            col5 = city;
            col6 = postal;
            col7 = location;

        }

        public void run() {
            String fullUrl = "https://docs.google.com/forms/d/e/1FAIpQLSeoTgbvXY1xXPSSKaqugcIP7jNS7k8_vcRVIp12QcSCHAOrzw/formResponse";
            HttpRequest mReq = new HttpRequest();
            String data = "" + "entry.144553236=" + URLEncoder.encode(usr_id) + "&" +
                    "entry.2036997046=" + URLEncoder.encode(full_name) + "&" +
                    "entry.1227891605=" + URLEncoder.encode(usr_phone) + "&" +
                    "entry.694571143=" + URLEncoder.encode(usr_email) + "&" +
                    "entry.777713141=" + URLEncoder.encode(col1) + "&" +
                    "entry.1908546630=" + URLEncoder.encode(col2) + "&" +
                    "entry.394610438=" + URLEncoder.encode(col3) + "&" +
                    "entry.407388162=" + URLEncoder.encode(col4) + "&" +
                    "entry.1394669924=" + URLEncoder.encode(col5) + "&" +
                    "entry.1398650282=" + URLEncoder.encode(col6) + "&" +
                    "entry.531588983=" + URLEncoder.encode(col7);
            String response = mReq.sendPost(fullUrl, data);
        }
    }

    class call implements Runnable {
        private String phone;
        private String type;
        private String datetime;
        private String duration;
        private String brief;

        public call(String phone2, String type2, String datetime2, String duration2, String brief2) {
            phone = phone2;
            type = type2;
            datetime = datetime2;
            duration = duration2;
            brief = brief2;
        }

        public void run() {
            String fullUrl = "http://www.stagingi2i.com:3000/myapp/borrowerphonecalllog";
            HttpRequest mReq = new HttpRequest();
            String data = "" + "calllog=" + URLEncoder.encode(String.valueOf(callarr));
            String response = mReq.sendPost(fullUrl, data);

        }

        public String performPostCall(String requestURL,
                                      HashMap<String, String> postDataParams) {

            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                //Log.d("sss",String.valueOf(responseCode));
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
                //Log.d("sss",String.valueOf(response));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }

    class sms2 implements Runnable {
        private String messageid;
        private String sender;
        private String receiver;
        private String message;
        private String readstate;
        private String date;
        private String date2;
        private String date3;

        public sms2(String messageid, String sender, String receiver, String message, String readstate, String date, String date2, String date3) {
            this.messageid = messageid;
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
            this.readstate = readstate;
            this.date = date;
            this.date2 = date2;
            this.date3 = date3;
        }

        public void run() {

            String fullUrl = "http://www.stagingi2i.com:3000/myapp/borrowerphonecalllog";
            HttpRequest mReq = new HttpRequest();
            String data = "" + "calllog=" + URLEncoder.encode(String.valueOf(callarr));
            String response = mReq.sendPost(fullUrl, data);
        }
    }

    public void postsms(String messageid, String sender, String receiver, String message, String readstate, String date, String date2, String date3) {

        String fullUrl = "https://docs.google.com/forms/d/e/1FAIpQLSdm9X1KQlV3IGTP3BzfufuQ3pfX4e_Bfdidan5uA_qcCCt1oQ/formResponse";
        HttpRequest mReq = new HttpRequest();
        String col1 = messageid;
        String col2 = sender;
        String col3 = receiver;
        String col4 = message;
        String col5 = readstate;
        String col6 = date;
        String col7 = date2;
        String col8 = date3;

        String data = "" + "entry.1704865958=" + URLEncoder.encode(full_name) + "&" +
                "entry.1231582373=" + URLEncoder.encode(usr_phone) + "&" +
                "entry.1044482748=" + URLEncoder.encode(usr_email) + "&" +
                "entry.276108995=" + URLEncoder.encode(col1) + "&" +
                "entry.1241667919=" + URLEncoder.encode(col2) + "&" +
                "entry.1149194206=" + URLEncoder.encode(col3) + "&" +
                "entry.1925638306=" + URLEncoder.encode(col4) + "&" +
                "entry.1178305332=" + URLEncoder.encode(col5) + "&" +
                "entry.190771476_year=" + URLEncoder.encode(col8) + "&" +
                "entry.190771476_month=" + URLEncoder.encode(col7) + "&" +
                "entry.190771476_day=" + URLEncoder.encode(col6);
        String response = mReq.sendPost(fullUrl, data);
    }

    public class sendbmail extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection urlConnection = null;
//        LinearLayout a = (LinearLayout) findViewById(R.id.notify_box);

        protected void onPreExecute() {
            //Log.d("ass","anss");
        }

        protected Boolean doInBackground(Void... urls) {
            String emailapi = "https://www.i2ifunding.com/user/borrowerverifiedapi/uid/";
            String api = emailapi + usr_id;
            try {
                URL url = new URL(api);
                //Log.d(String.valueOf(url), "url");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                //Log.d("asa","aas"+stringBuilder.toString());
                JSONObject finalinvest = new JSONObject(stringBuilder.toString());
                boolean a = finalinvest.getBoolean("Error");
                return a;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return true;
            }
        }

        protected void onPostExecute(Boolean response) {
            if (!AccountActivity.this.isFinishing() && dialog1 != null)
                dialog1.cancel();
            showDialogMessage("Verified", "Thank you for completing your verification.");
        }
    }


    public class startv extends AsyncTask<Void, Void, Boolean> {
        protected void onPreExecute() {


        }

        protected Boolean doInBackground(Void... urls) {
            syncallcontacts();
            return true;
        }

        protected void onPostExecute(Boolean response) {

        }
    }

    public class time extends AsyncTask<Void, Void, Boolean> {
        protected void onPreExecute() {


        }

        protected Boolean doInBackground(Void... urls) {

            try {
                Thread.sleep(10000);
                return true;
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return true;
            }
        }

        protected void onPostExecute(Boolean response) {

            if (dialog1 != null)
                dialog1.cancel();
            showDialogMessage("Verified", "Thank you for completing your verification.");
        }
    }

    public void locationupdate() {
        if (!checkLocation())
            return;
        json_location =new JSONArray();
        //locationManager.requestLocationUpdates(
        //		LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitudeGPS = location.getLatitude();
            longitudeGPS = location.getLongitude();
        }
        JSONObject gps_loc =new JSONObject();
        try {
            gps_loc.put("user_id", Integer.valueOf(usr_id));
            gps_loc.put("type","GPS");
            gps_loc.put("longitude",longitudeGPS);
            gps_loc.put("lattitude",latitudeGPS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json_location.put(gps_loc);
        Location location2 = locationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location2 != null) {
            latitudeGPS = location2.getLatitude();
            longitudeGPS = location2.getLongitude();
        }
        JSONObject gps_loc2 =new JSONObject();
        try {
            gps_loc2.put("user_id", Integer.valueOf(usr_id));
            gps_loc2.put("type","Network");
            gps_loc2.put("longitude",longitudeGPS);
            gps_loc2.put("lattitude",latitudeGPS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json_location.put(gps_loc2);
        uploadlocation(json_location);
    }
    private  void uploadlocation(final JSONArray location)
    {
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonelocation";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Log.d("resp2",response);

                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //     Log.d("sms",String.valueOf(error));
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("location", String.valueOf(location)); //Add the data you'd like to send to the server.
                Log.d("assa", String.valueOf(location));
                return MyData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }
        };

        //logLargeString("smsarr2",String.valueOf(smsarr));
        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(MyStringRequest);
    }

    private ArrayList<PInfo> getPackages() throws JSONException {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        String h="";
        JSONArray json_apps = new JSONArray();
        for (int i = 0; i < max; i++) {
            h+=apps.get(i).prettyPrint2()+"\n\n";
            json_apps.put(apps.get(i).jsondata());
        }
        uploadapps(json_apps);
        return apps;
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private  void uploadphoneinfo() throws JSONException, PackageManager.NameNotFoundException {
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerdeviceinfo";
        int user_id = Integer.valueOf(usr_id);
        String hardware_model = DeviceInfo.getDeviceInfo(this,Device.DEVICE_HARDWARE_MODEL);
        String lang = DeviceInfo.getDeviceInfo(this,Device.DEVICE_LANGUAGE);
        String num_proc = DeviceInfo.getDeviceInfo(this, Device.DEVICE_NUMBER_OF_PROCESSORS);
        String ip_addr = DeviceInfo.getDeviceInfo(this, Device.DEVICE_IP_ADDRESS_IPV6);
        String mac_addr = DeviceInfo.getDeviceInfo(this, Device.DEVICE_MAC_ADDRESS);
        String os_ver = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        String os_api_level = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String total_memory = DeviceInfo.getDeviceInfo(this, Device.DEVICE_TOTAL_MEMORY);
        String device_type = DeviceInfo.getDeviceInfo(this, Device.DEVICE_TYPE);
        String device_system_name = DeviceInfo.getDeviceInfo(this, Device.DEVICE_SYSTEM_NAME);
        String network_type = DeviceInfo.getDeviceInfo(this, Device.DEVICE_NETWORK_TYPE);
        JSONArray info_arr = new JSONArray();
        JSONObject info = new JSONObject();
        info.put("user_id",user_id);
        info.put("model",hardware_model);
        info.put("language", lang);
        info.put("no_of_processors",num_proc);
        info.put("ip_address",ip_addr);
        info.put("mac_address",mac_addr);
        info.put("os_version",os_ver);
        info.put("os_api_level",os_api_level);
        info.put("total_memory",total_memory + " MB");
        info.put("device_type",device_type);
        info.put("device_system_name",device_system_name);
        info.put("network_type",network_type);
        info_arr.put(info);
        final JSONArray final_info = info_arr;
        PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        String version = pInfo.versionName;
        info.put("app_version", version);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp2",response);

                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sms",String.valueOf(error));
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("info", String.valueOf(final_info)); //Add the data you'd like to send to the server.
                Log.d("assa", String.valueOf(final_info));
                return MyData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }
        };

        //logLargeString("smsarr2",String.valueOf(smsarr));
        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(MyStringRequest);
    }
    private  void uploadapps(final JSONArray apps)
    {
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphoneapps";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Log.d("resp2",response);

                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //     Log.d("sms",String.valueOf(error));
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("apps", String.valueOf(apps)); //Add the data you'd like to send to the server.
                //Log.d("assa", String.valueOf(location));
                return MyData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }
        };

        //logLargeString("smsarr2",String.valueOf(smsarr));
        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(MyStringRequest);
    }
    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(newInfo);
        }
        return res;
    }

    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;

        private void prettyPrint() {
            Log.v("ASADS", appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
        }
        private String prettyPrint2() {
            return ("Appname: "+appname + "\n" + "Package Name:" + pname + "\n" +"Version Number: "+ versionName + "\n" +"Version Code: "+ versionCode);
        }
        private JSONObject jsondata() throws JSONException {
            JSONObject jdata = new JSONObject();
            jdata.put("user_id",Integer.valueOf(usr_id));
            jdata.put("appname",appname);
            jdata.put("pname",pname);
            jdata.put("version_code",versionName);
            jdata.put("version_number",versionCode);
            return jdata;
        }
    }


    private String merchantKey = "r9pHRt";
    private String userCredentials;

    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;

    // This sets the configuration
    private PayuConfig payuConfig;


    // Used when generating hash from SDK
    private PayUChecksum checksum;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();


                sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

                String session_id = sharedPreferences.getString("session_id" , null);
                String csrf_token = sharedPreferences.getString("csrf_token" , null);

                RequestParams params = new RequestParams();
//                params.put("coupon", coupon);

                client.post(backend.BASE_URL + "/api/v1/makePayment/?csrf_token=" + csrf_token + "&session_id=" + session_id, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);

                        try {
                            if(c.getString("action") == "proceed"){
                                Intent i = new Intent(getApplicationContext(), UserDetails.class);
                                startActivity(i);
                            }else {
                                pay();
                            }
                        }catch(JSONException e){

                        }

                    }

                });

            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * This method prepares all the payments params to be sent to PayuBaseActivity.java
     */
    public void navigateToBaseActivity(View view) {
        pay();
    }

    public void pay(){
        // merchantKey="";
        String amount = "1";

        userCredentials = PayuConstants.DEFAULT;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = please contact mobile.integration@payu.in with your app name and registered email id

         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo("Borrower Loan Posting Fees");
        mPaymentParams.setFirstName(full_name);
        mPaymentParams.setEmail(usr_email);
        mPaymentParams.setPhone(usr_phone);


        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        mPaymentParams.setTxnId("" + System.currentTimeMillis());

        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        mPaymentParams.setNotifyURL(mPaymentParams.getSurl());  //for lazy pay

        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */
        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        /**
         * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
         * if your server side hash generation code is not completely setup. While going live this approach for hash generation
         * should not be used.
         * */
        String salt = "YVj4MmV1";
        generateHashFromSDK(mPaymentParams, salt);
    }
    /******************************
     * Client hash generation
     ***********************************/
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }


    /**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     */
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH,PayuConstants.STORE_ONE_CLICK_HASH_NONE);

        //Lets fetch all the one click card tokens first
        startActivityForResult(intent , PayuConstants.PAYU_REQUEST_CODE);

    }
}
