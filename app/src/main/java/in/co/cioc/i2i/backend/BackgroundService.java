package in.co.cioc.i2i.backend;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import in.co.cioc.i2i.R;
import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

/**
 * Created by Ashish on 17/09/18.
 */

public class BackgroundService extends Service implements View.OnTouchListener {
    public static final String ACTION = "in.co.cioc.i2i.backend.backendreceiver";
    public static final String TAG = "BackgroundService";
    Session session;
    SessionManager sessionManager;
    public static final long INTERVAL = 1000 * 5;//variable to execute services every 10 second
    private Handler mHandler = new Handler(); // run on another Thread to avoid crash
    private Timer mTimer = null;// timer handling
    TimerTask timerTask;
    private View floatyView;
    private WindowManager windowManager;
    //    boolean internetAvailable;
    Client client;
    CompletableFuture<ExitInfo> exitInfoCompletableFuture;
    LinearLayout layout;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    @Override
    public void onCreate() {
        sessionManager = new SessionManager(this);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL); // schedule task
//        addOverlayView();

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.v(TAG, "onTouch...");

        // Kill service
        onDestroy();
        return true;
    }


    //inner class of TimeDisplayTimerTask
    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast at every 10 second
                    Boolean internetAvailable = false;
//                    Toast.makeText(getApplicationContext(), "service running" + client.toString() + exitInfoCompletableFuture.isDone() , Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(), "Service running", Toast.LENGTH_SHORT).show();
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                    if (netInfo != null) {
                        internetAvailable = true;
                    }

                    if ((exitInfoCompletableFuture == null || exitInfoCompletableFuture.isDone()) && internetAvailable) {
                        session = new Session();
                        session.addOnJoinListener(this::demonstrateSubscribe);
                        client = new Client(session, "ws://wamp.cioc.in:8090/ws", "default");
                        exitInfoCompletableFuture = client.connect();
                    }
                }


                public void demonstrateSubscribe(Session session, SessionDetails details) {

                    String usrname = sessionManager.getUsername();

                    CompletableFuture<Subscription> subFuture = session.subscribe("service.self." + "admin",
                            this::onEvent);
                    subFuture.whenComplete((subscription, throwable) -> {
                        if (throwable == null) {
                            System.out.println("Subscribed to topic " + subscription.topic);
                            Toast.makeText(getApplicationContext(), "wapm server Connected", Toast.LENGTH_SHORT).show();
                        } else {
                            throwable.printStackTrace();
                        }
                    });
                }

                private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
                    System.out.println(String.format("Got event: %s", args.get(0)));

                    Toast.makeText(BackgroundService.this, args.toString(), Toast.LENGTH_SHORT).show();

                    // add a notification strip here

                }
            });
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //create a intent that you want to start again..
        String manufacturer = "xiaomi";
        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        }
        super.onTaskRemoved(rootIntent);
    }

    private void addOverlayView() {

        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = LinearLayout.LayoutParams.MATCH_PARENT;
        params.y = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout = new LinearLayout(this);
        layout.setBackgroundColor(Color.BLACK);
        layout.setOrientation(LinearLayout.VERTICAL);

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

        windowManager.addView(layout, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
        if (layout != null) {
            windowManager.removeView(layout);
            layout = null;
        }
        Intent intent = new Intent(ACTION);
        sendBroadcast(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        intent = new Intent(ACTION);
        sendBroadcast(intent);
        return START_STICKY;
    }
}
