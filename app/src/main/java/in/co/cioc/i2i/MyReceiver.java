package in.co.cioc.i2i;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by SatyamMittal on 22-06-2017.
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, BackServicce.class);
        context.startService(myIntent);

    }
}
