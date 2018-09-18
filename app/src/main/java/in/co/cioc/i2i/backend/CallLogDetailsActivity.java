package in.co.cioc.i2i.backend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import in.co.cioc.i2i.R;

public class CallLogDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setFinishOnTouchOutside(false);
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        getWindow().setFlags(flags, flags);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.activity_call_log_details);

        String mob = getIntent().getStringExtra("cno");
//        TextView mobNo = findViewById(R.id.mobile_text1);
//        mobNo.setText(mob);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Alerts.register(this);
    }

    @Override
    protected void onPause() {
        Alerts.unregister(this);
        super.onPause();
    }
}
