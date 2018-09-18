package in.co.cioc.i2i.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by admin on 17/09/18.
 */

public class BackgroundReceiver extends BroadcastReceiver {
    SessionManager sessionManager;
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        sessionManager = new SessionManager(context);
//        if (sessionManager.getCsrfId() != "" && sessionManager.getSessionId() != "") {
//            context.startService(new Intent(context, BackgroundService.class));
//        }
        context.startService(new Intent(context, BackgroundService.class));
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, BackgroundService.class));
        }

        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return;
        } else {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNo = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)||state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Toast.makeText(context, "Incoming Call State" + incomingNo, Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context,"Call Idle State - "+number,Toast.LENGTH_SHORT).show();
            }
        }
    }
}