package in.co.cioc.i2i;

/**
 * Created by SatyamMittal on 15-04-2017.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyCustomTextView extends android.support.v7.widget.AppCompatTextView {
    public  MyCustomTextView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Calibri.ttf"));
    }
}
