package in.co.cioc.i2i;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.githang.stepview.StepView;

import java.util.Arrays;
import java.util.List;

public class EducationalDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_details);
        StepView mStepView = (StepView) findViewById(R.id.step_view);

        List<String> steps = Arrays.asList(new String[]{"Basic", "User", "Employment", "Educational" , "Documents"});
        mStepView.setSteps(steps);
        mStepView.selectedStep(4);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        String val = sharedPreferences.getString("test" , null);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("test", "value");
        editor.commit();
    }
}
