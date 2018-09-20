package in.co.cioc.i2i.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import in.co.cioc.i2i.SmsListener;

/**
 * Created by admin on 17/09/18.
 */

public class BackgroundReceiver extends BroadcastReceiver {

    private static final String TAG =
            BackgroundReceiver.class.getSimpleName();
    SessionManager sessionManager;
    Context ctx;
    public static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        sessionManager = new SessionManager(context);
//        Bundle data = intent.getExtras();
//        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
//            String smsSender = "";
//            String smsBody = "";

//        if (data != null) {
//
//            Object[] pdus = (Object[]) data.get("pdus");
//
//            for(int i=0;i<pdus.length;i++){
//                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//
//                String sender = smsMessage.getDisplayOriginatingAddress();
//                //You must check here if the sender is your provider and not another one with same text.
//
//                String messageBody = smsMessage.getMessageBody();
//
//                //Pass on the text to our listener.
//                if (mListener == null){
//                    return;
//                }
//                mListener.messageReceived(messageBody);
//            }
//        }
//                    Object[] pdus = (Object[]) data.get("pdus");
//
//                    for (int i = 0; i < pdus.length; i++) {
//                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//
//                        String sender = smsMessage.getDisplayOriginatingAddress();
//
//                        String messageBody = smsMessage.getMessageBody();
//
//                        //Pass on the text to our listener.
//
////                    Object[] pdus = (Object[]) intent.getExtras().get("pdus");
//////                    for (int i = 0; i < pdus.length; i++) {
//////                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//////                        String sender = smsMessage.getDisplayOriginatingAddress();
//////                        String messageBody = smsMessage.getMessageBody();
//////                        Toast.makeText(context, "sender " + sender + "\nmessageBody: " + messageBody, Toast.LENGTH_SHORT).show();
//////                    }
////                            if (pdus == null) {
////                                // Display some error to the user
////                                Log.e("", "SmsBundle had no pdus key");
////                                return;
////                            }
////                            SmsMessage[] messages = new SmsMessage[pdus.length];
////                            for (int i = 0; i < messages.length; i++) {
////                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
////                                smsBody += messages[i].getMessageBody();
////                            }
////                            smsSender = messages[0].getOriginatingAddress();
//                        if (mListener == null) {
//                            return;
//                        }
//                        mListener.messageReceived("smsSender " + sender + "\nsmsBody: " + messageBody);
//                        context.startService(new Intent(context, BackgroundService.class));
//                    }
//                }

//            }

//                    SmsMessage[] msgs;
//                    String strMessage = "";
//                    String format = data.getString("format");
//                    // Retrieve the SMS message received.
//                    Object[] pdus = (Object[]) data.get("data");
//                    if (pdus != null) {
//                        // Check the Android version.
//                        boolean isVersionM =
//                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
//                        // Fill the msgs array.
//                        msgs = new SmsMessage[pdus.length];
//                        for (int i = 0; i < msgs.length; i++) {
//                            // Check Android version and use appropriate createFromPdu.
//                            if (isVersionM) {
//                                // If Android version M or newer:
//                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                            } else {
//                                // If Android version L or older:
//                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                            }
//                            // Build the message to show.
//                            strMessage += "SMS from " + msgs[i].getOriginatingAddress();
//                            strMessage += " :" + msgs[i].getMessageBody() + "\n";
//                            // Log and display the SMS message.
//                            Log.d(TAG, "onReceive: " + strMessage);
//
//                            if (mListener == null) {
//                                return;
//                            }
//                            mListener.messageReceived(strMessage);
//                        }
//                    }
//                }

        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return;
        } else {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNo = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)||state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (incomingNo != null) {
                    String mobNo = incomingNo.replace("+91", "");
                    Toast.makeText(context, "Incoming Call State " + mobNo, Toast.LENGTH_SHORT).show();
//                    context.startService(new Intent(context, BackgroundService.class).putExtra("mob", mobNo));
                }
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context,"Call Idle State - "+number,Toast.LENGTH_SHORT).show();
            }
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, BackgroundService.class));
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener  = listener;
    }
}