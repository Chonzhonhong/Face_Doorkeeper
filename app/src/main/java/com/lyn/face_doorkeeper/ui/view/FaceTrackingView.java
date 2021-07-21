package com.lyn.face_doorkeeper.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class FaceTrackingView extends View {
    private Paint paint;
    private RectF rectF;
    private String text;

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
        postInvalidate();
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FaceTrackingView(Context context) {
        super(context);
        init();
    }

    public FaceTrackingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceTrackingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FaceTrackingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rectF!=null){
            if (!TextUtils.isEmpty(text)){
                paint.setColor(Color.YELLOW);
                int x= (int) ((rectF.right-rectF.left)/2+rectF.left);
                int y= (int) (rectF.top-30);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(text,x,y,paint);
            }
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rectF,paint);
        }
    }
}
