package com.picamerica.findmydrunkfriends.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.picamerica.findmydrunkfriends.R;

/**
 * Created by ErRk on 5/27/2015.
 */
public class CustomRadioButton extends RadioButton{
    public CustomRadioButton(Context context) {
        super(context);
        init(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        XmlResourceParser xrp = context.getResources().getXml(R.xml.radio_text_color_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(context.getResources(), xrp);
            this.setTextColor(csl);
        } catch (Exception e) {  }
    }
}
