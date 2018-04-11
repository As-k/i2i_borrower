package in.co.cioc.i2i;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by SatyamMittal on 25-07-2016.
 */
public class MyBoldTextView extends android.support.v7.widget.AppCompatTextView {
    public  MyBoldTextView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Calibri.ttf"),Typeface.BOLD);
    }
}