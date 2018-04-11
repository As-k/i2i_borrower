package in.co.cioc.i2i;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by SatyamMittal on 15-04-2017.
 */
public class MyButton extends Button {
    public MyButton(Context context, AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Calibri.ttf"));
    }
}