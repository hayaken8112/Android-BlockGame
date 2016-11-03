package com.example.ken.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by ken on 2015/11/17.
 */
public class Pad implements DrawableItem {
    private final float mTop;
    private float mLeft;
    private final float mBottom;
    private float mRight;

    public Pad (float top, float bottom){
        mTop = top;
        mBottom = bottom;
    }

    public float getTop(){
        return mTop;
    }

    public void setLeftRight(float left, float right){
        mLeft = left;
        mRight = right;
    }

    /**
     *
     * @param canvas
     * @param paint
     */

    public void draw(Canvas canvas, Paint paint){
        //塗りつぶし部分を描画。
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
    }
}
