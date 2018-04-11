package in.co.cioc.i2i;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by SatyamMittal on 15-04-2017.
 */
public class MyCheckbox extends android.support.v7.widget.AppCompatCheckBox {
    public MyCheckbox(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Calibri.ttf"));
    }
}