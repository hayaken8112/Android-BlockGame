package com.example.ken.game;

/**
 * Created by ken on 2015/11/17.
 */
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by ken on 2015/11/17.
 */
public class Block implements DrawableItem {
    private final float mTop;
    private final float mLeft;
    private final float mRight;
    private final float mBottom;
    private int mHard;

    public Block(float top, float left, float bottom, float right) {
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mHard = 1;
    }

    private boolean mIsCollision = false; //衝突状態を記録するフラグ

    /**
     * ボールが衝突した時の処理
     */
    public  void collision(){
        mIsCollision = true; // 衝突したことだけを記録し、破壊はdraw()で行う。
    }

    private boolean mIsExist = true; //ブロックが破壊されていないか

    /**
     *ブロックが破壊されていないか
     * @return 破壊されていない場合true
     */
    public boolean isExist(){
        return mIsExist;
    }

    /**
     *
     * @param canvas
     * @param paint
     */

    public void draw(Canvas canvas, Paint paint) {
        if (mIsExist) {
            //耐久力が0以上の場合のみ
            if (mIsCollision){
                mHard--;
                mIsCollision = false;
                if(mHard <= 0){
                    mIsExist = false;
                    return;
                }
            }

            //塗りつぶし部分を描画
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
            //枠線部分を描画
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4f);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
        }
    }
}
