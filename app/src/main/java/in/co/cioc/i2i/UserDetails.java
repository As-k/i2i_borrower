package in.co.cioc.i2i;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.githang.stepview.StepView;

import java.util.Arrays;
import java.util.List;

public class UserDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        StepView mStepView = (StepView) findViewById(R.id.step_view);
        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);


        mStepView.selectedStep(2);
    }
}
