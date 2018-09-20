package in.co.cioc.i2i.backend;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import in.co.cioc.i2i.Backend;
import in.co.cioc.i2i.R;
import in.co.cioc.i2i.SmsListener;

/**
 * Created by Ashish on 17/9/18.
 */

public class CallBarring extends BroadcastReceiver {
    Context cxt;
    String phNumber, callDuration, dateString, timeString, dir, date;
    int tot_seconds;
    private static SmsListener mListener;

    @Override
    public void onReceive(final Context context, Intent intent) {
        cxt = context;
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.

            String messageBody = smsMessage.getMessageBody();

            //Pass on the text to our listener.
            if (mListener == null){
                return;
            }
            mListener.messageReceived(sender+"\n "+messageBody);
        }

//        serverUrl = new ServerUrl(context);
//        asyncHttpClient = serverUrl.getHTTPClient();
        // If, the received action is not a type of "Phone_State", ignore it
//        intent1 = new Intent(context, CallLogDetailsActivity.class);

//        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
//            return;
//        } else {
//            try {
//                System.out.println("Receiver start");
//                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//                final String incomingNo = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)||state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//                    Toast.makeText(context, "Incoming Call State"+ incomingNo, Toast.LENGTH_SHORT).show();
//                    intent1.putExtra("cno", incomingNo);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
////                    getCalldetailsNow();
//                    if (incomingNo != null) {
//                        String mobNo = incomingNo.replace("+91", "");
////                        final AlertDialog.Builder abd = new AlertDialog.Builder(context);
////                        abd.setIcon(R.drawable.phone_circle).setMessage("Mob:"+incomingNo);
////                        final AlertDialog ad = abd.create();
////                        ad.show();
////                        asyncHttpClient.get(ServerUrl.url + "api/clientRelationships/contactLite/?format=json&mobile=" + mobNo, new JsonHttpResponseHandler() {
////                            @Override
////                            public void onSuccess(int statusCode, Header[] headers, final JSONArray response) {
////                                super.onSuccess(statusCode, headers, response);
////                                for (int i = 0; i < response.length(); i++) {
////                                    JSONObject obj = null;
////                                    try {
////                                        obj = response.getJSONObject(i);
////                                        final String removePk = obj.getString("pk");
////                                        ContactLite lite = new ContactLite(obj);
//                        context.startService(new Intent(context, BackgroundService.class));
////                                    } catch (JSONException e) {
////                                        e.printStackTrace();
////                                    }
////                                }
////                            }
////
////                            @Override
////                            public void onFinish() {
////                                System.out.println("finished 001");
////
////                            }
////                            @Override
////                            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
////                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
////                                System.out.println("finished failed 001");
////                            }
////                        });
//                    }
//                }
//                if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                    Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show();
//                }
//                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                    String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                    Toast.makeText(context,"Call Idle State - "+number,Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }

//        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//
//        // Adds a view on top of the dialer app when it launches.
//        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//
//            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
//                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSPARENT);
//
//            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
//            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
//            params.format = PixelFormat.TRANSLUCENT;
//
//            params.gravity = Gravity.TOP;
//
//            ly = new LinearLayout(context);
//            ly.setBackgroundColor(Color.RED);
//            ly.setOrientation(LinearLayout.VERTICAL);
//
//            wm.addView(ly, params);
//        }
//
//        // To remove the view once the dialer app is closed.
//        if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
//            String state1 = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            if(state1.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//                if(ly!=null) {
//                    wm.removeView(ly);
//                    ly = null;
//                }
//            }
//        }

    }

    private void getCalldetailsNow() {
        // TODO Auto-generated method stub
        if (ActivityCompat.checkSelfPermission(cxt, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = cxt.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " ASC");

        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        int type1 = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date1 = managedCursor.getColumnIndex(CallLog.Calls.DATE);

        if(managedCursor.moveToLast() == true) {
            phNumber = managedCursor.getString(number);
            String callDuration1 = managedCursor.getString(duration1);

            String type = managedCursor.getString(type1);
            date = managedCursor.getString(date1);
            int dircode = Integer.parseInt(type);
            dir=null;
            switch (dircode)
            {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                default:
                    dir = "MISSED";
                    break;
            }

            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");
//            SimpleDateFormat sdf_dur = new SimpleDateFormat("KK:mm:ss");

            tot_seconds = Integer.parseInt(callDuration1);
            int hours = tot_seconds / 3600;
            int minutes = (tot_seconds % 3600) / 60;
            int seconds = tot_seconds % 60;

            callDuration = String.format("%02d : %02d : %02d ", hours, minutes, seconds);

            dateString = sdf_date.format(new Date(Long.parseLong(date)));
            timeString = sdf_time.format(new Date(Long.parseLong(date)));
            //  String duration_new=sdf_dur.format(new Date(Long.parseLong(callDuration)));
        }
        managedCursor.close();
    }

    public static void bindListener(SmsListener smsListener) {
        mListener = smsListener;
    }

//    private void addOverlayView() {
//
//        final WindowManager.LayoutParams params =
//                new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.MATCH_PARENT,
//                        WindowManager.LayoutParams.WRAP_CONTENT,
//                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
//                                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                        PixelFormat.TRANSLUCENT);
//
//        params.gravity = Gravity.CENTER | Gravity.START;
//        params.x = LinearLayout.LayoutParams.MATCH_PARENT;
//        params.y = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout = new LinearLayout(this);
//        layout.setBackgroundColor(Color.BLACK);
//        layout.setOrientation(LinearLayout.VERTICAL);

//        FrameLayout interceptorLayout = new FrameLayout(this) {
//            @Override
//            public boolean dispatchKeyEvent(KeyEvent event) {
//                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    // Check if the HOME button is pressed
//                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//                        Log.v("Receiver", "BACK Button Pressed");
//                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
//                        return true;
//                    }
//                }
//                // Otherwise don't intercept the event
//                return super.dispatchKeyEvent(event);
//            }
//        };
//
//        floatyView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_call_barring, interceptorLayout);
//        floatyView.setOnTouchListener(this);

//        windowManager.addView(layout, params);
//    }
}
