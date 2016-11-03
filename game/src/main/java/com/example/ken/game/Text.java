package com.example.ken.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by ken on 2015/11/20.
 */
public class Text {
    private float mTextX;
    private float mTextY;
    private String mText;
    public Text(String text){
        mText = text;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(40);
        canvas.drawText(mText, mTextX,mTextY, paint);
    }

    public void setTextX (float x){
        mTextX = x;
    }
    public void setTextY (float y){
        mTextY = y;
    }
    public void setText(String text){
        mText = text;
    }

}
