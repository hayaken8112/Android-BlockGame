package com.example.ken.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by ken on 15/11/16.
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener {
    volatile private boolean mIsRunnable;
    private Thread mThread;
    private Pad mPad;
    private float mPadHalfWidth;
    private Ball mBall;
    private float mBallRadius;
    volatile private float mTouchedX;
    volatile private float mTouchedY;
    private ArrayList<DrawableItem> mItemList;
    private int mLife;
    private Text mText;
    private String mTextContents;



    public GameView(Context context) {
        super(context); //親クラスのコンストラクターを引数 contextで呼び出す。
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }

    public void start(){
        mThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                while (true) {
                    long startTime = System.currentTimeMillis();
                    synchronized (GameView.this) {
                        if (!mIsRunnable) {
                            break;
                        }
                        //アプリ実行中繰り返し呼ばれる
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        canvas.drawColor(Color.BLACK);
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;

                        if (ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >=getWidth() && mBall.getSpeedX() > 0) {
                            mBall.setSpeedX(-mBall.getSpeedX());
                        }
                        if (ballTop < 0) {
                            mBall.setSpeedY(-mBall.getSpeedY());
                        }
                        if (ballTop > getHeight()) {
                            if(mLife>0){
                                mLife--;
                                mBall.reset();
                            }
                        }

                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block topBlock  = getBlock(mBall.getX(), ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);
                        if(leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                        }
                        if(topBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                        }
                        if (rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                        }
                        if (bottomBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                        }
                        for (DrawableItem item : mItemList) {
                            // mItemListの内容が一つずつitemに渡される。
                            item.draw(canvas, paint);
                        }

                        //パッドとボールの衝突判定処理
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();
                        if (ballBottom > padTop && ballBottom - ballSpeedY < padTop && padLeft < ballRight && padRight > ballLeft){
                            if (ballSpeedY < mBlockHeight / 3) {
                                ballSpeedY *= -1.05f;
                            } else {
                                ballSpeedY = -ballSpeedY;
                            }

                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX) / 10;
                            if (ballSpeedX > mBlockWidth / 5) {
                                ballSpeedX = mBlockWidth / 5;
                            }
                            mBall.setSpeedX(ballSpeedX);
                            mBall.setSpeedY(ballSpeedY);
                        }

                        //テキスト表示
                        mTextContents = "Life:"+ mLife;
                        mText.setText(mTextContents);
                        mText.draw(canvas, paint);
                    /*// mTouchXとmTouchYを中心とする。
                    canvas.drawCircle(mTouchedX, mTouchedY, 50, paint);*/
                        unlockCanvasAndPost(canvas);
                        long sleepTime = 16 - (System.currentTimeMillis() - startTime);
                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }
    public void stop(){
        mIsRunnable = false;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);

    }

    private float mBlockWidth;
    private float mBlockHeight;


    public void readyObjects(int width, int height) {
        mBlockWidth = width / 10;
        mBlockHeight = height / 30;
        mItemList =  new ArrayList<DrawableItem>();
        for (int i = 0; i < BLOCK_COUNT; i++) {
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            float blockRight = blockLeft + mBlockWidth;
            mItemList.add(new Block(blockTop, blockLeft, blockBottom, blockRight));
        }
        mPad = new Pad(height * 0.8f, height * 0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;

        mBallRadius = width < height ? width / 40 : height / 40;
        mBall = new Ball(mBallRadius, width /2, height / 2);
        mItemList.add(mBall);
        mLife = 5;
        mTextContents = "Life:"+ mLife;
        mText = new Text(mTextContents);
        mText.setTextX(width * 0.8f);
        mText.setTextY(height* 0.95f);

    }
    static final int BLOCK_COUNT = 100;
    private Block getBlock(float x, float y) {

        int index = (int)(x / mBlockWidth) + (int)(y / mBlockHeight) * 10;
        if (0 <= index && index < BLOCK_COUNT) {
            Block block = (Block)mItemList.get(index);
            if(block.isExist()){
                return block;
            }
        }
        return null;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        synchronized (this) {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }
}
