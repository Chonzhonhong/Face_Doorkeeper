package com.lyn.face_doorkeeper.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.lyn.face_doorkeeper.R;


public class SinkView extends RelativeLayout {
    public SinkView(Context context) {
        super(context);
    }

    public SinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SinkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SinkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        if (action==MotionEvent.ACTION_DOWN){
            this.setBackground(getResources().getDrawable(R.color.colorGray));
        }

        if (action==MotionEvent.ACTION_MOVE){
            this.setBackground(getResources().getDrawable(R.color.colorGray));
        }

        if (action==MotionEvent.ACTION_UP){
            this.setBackground(getResources().getDrawable(R.color.colorTransparent));
        }
        return super.onTouchEvent(event);
    }
}
