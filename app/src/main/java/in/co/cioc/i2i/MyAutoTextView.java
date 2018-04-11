package in.co.cioc.i2i;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by SatyamMittal on 15-04-2017.
 */
public class MyAutoTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public  MyAutoTextView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Calibri.ttf"));
    }
}
