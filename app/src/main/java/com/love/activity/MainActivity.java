package com.love.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.love.R;
import com.love.factory.ImageNameFactory;
import com.love.util.ClickUtils;
import com.love.util.DeviceInfo;
import com.love.util.ImageUtils;
import com.love.util.MediaManager;
import com.love.view.bluesnow.FlowerView;
import com.love.view.heart.HeartLayout;
import com.love.view.typewriter.TypeTextView;
import com.love.view.whitesnow.SnowView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {
    public static final String TAG = "mainActivity:";
    private static final String JPG = ".jpg";
    private static final String LOVE = "幼时初见月,影从树上掠。光于院里落，汝向光中来。 \n 如果可以我想和你一辈子 - by ljx  \n 我会成为你的那束光 - by yxf";
    private static final int SNOW_BLOCK = 1;
    public static final String URL = "file:///android_asset/index.html";
    private Canvas mCanvas;
    private int mCounter;
    private FlowerView mBlueSnowView;//蓝色的雪花
    private Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            MainActivity.this.mBlueSnowView.inva();
        }
    };
    private HeartLayout mHeartLayout;//垂直方向的漂浮的红心
    private ImageView mImageView;//图片
    private Bitmap mManyBitmapSuperposition;
    private ProgressBar mProgressBar;
    private Random mRandom = new Random();
    private Random mRandom2 = new Random();
    private TimerTask mTask = null;
    private TypeTextView mTypeTextView;//打字机
    private WebSettings mWebSettings;
    private WebView mWebView;
    private SnowView mWhiteSnowView;//白色的雪花
    private Timer myTimer = null;

    private CountDownTimer countDownTimer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceInfo.getInstance().initializeScreenInfo(this);
        setContentView(R.layout.main);
        initView();
        initWebView();
        bindListener();
//        delayShowAll(5000L);
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(3 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txtCountLeft.setVisibility(View.VISIBLE);
                    txtCountLeft.setText(String.format("欣赏雪景倒计时%s秒", millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    stopCountDownTimer();
                    txtCountLeft.setVisibility(View.GONE);
                    gotoNext();
                }
            };
        }
        countDownTimer.start();
    }


    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private FrameLayout mWebViwFrameLayout = null;
    private FrameLayout root_fragment_layout = null;
    private TextView txtCountLeft;
    private LinearLayout idGoWebView;

    private void initWebView() {

        this.mWebSettings = this.mWebView.getSettings();
        this.mWebSettings.setJavaScriptEnabled(true);
        this.mWebSettings.setBuiltInZoomControls(false);
        this.mWebSettings.setLightTouchEnabled(false);
        this.mWebSettings.setSupportZoom(false);
        this.mWebView.setHapticFeedbackEnabled(false);
    }

    private void initView() {
        mWebViwFrameLayout = (FrameLayout) findViewById(R.id.fl_webView_layout);
        root_fragment_layout = (FrameLayout) findViewById(R.id.root_fragment_layout);
        idGoWebView=(LinearLayout) findViewById(R.id.id_go_web);
        txtCountLeft = (TextView) findViewById(R.id.txt_count_left);
        this.mWebView = new WebView(getApplicationContext());
        this.mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.mWebView.setVisibility(View.GONE);
        //scrollbars

        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fp.gravity = Gravity.CENTER;

        mWebViwFrameLayout.addView(mWebView);

        this.mHeartLayout = (HeartLayout) findViewById(R.id.heart_o_red_layout);
        this.mTypeTextView = (TypeTextView) findViewById(R.id.typeTextView);
        this.mWhiteSnowView = (SnowView) findViewById(R.id.whiteSnowView);
        this.mImageView = (ImageView) findViewById(R.id.image);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        this.mBlueSnowView = (FlowerView) findViewById(R.id.flowerview);
        this.mBlueSnowView.setWH(DeviceInfo.mScreenWidthForPortrait, DeviceInfo.mScreenHeightForPortrait, DeviceInfo.mDensity);
        this.mBlueSnowView.loadFlower();
        this.mBlueSnowView.addRect();
        this.myTimer = new Timer();
        this.mTask = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = MainActivity.SNOW_BLOCK;
                MainActivity.this.mHandler.sendMessage(msg);
            }
        };
        rxJavaSolveMiZhiSuoJinAndNestedLoopAndCallbackHell();
        this.myTimer.schedule(this.mTask, 1000, 10);
//        clickEvent();
        this.mTypeTextView.setOnTypeViewListener(new TypeTextView.OnTypeViewListener() {
            public void onTypeStart() {
                MediaManager.getInstance().playSound(true);
            }

            public void onTypeOver() {
                MediaManager.getInstance().stop();
                delayShowTheSnow();
            }
        });
        this.mTypeTextView.setText("");
    }

    private void bindListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        if (this.mWebView != null) {
            if (mWebViwFrameLayout != null) {
                this.mWebViwFrameLayout.removeAllViewsInLayout();
                this.mWebViwFrameLayout.removeAllViews();
            }
            this.mWebView.removeAllViews();
            this.mWebView.destroy();
            this.mWebView = null;
        }
        unBindDrawables(findViewById(R.id.root_fragment_layout));
        System.gc();
    }

    private void cancelTimer() {
        if (this.myTimer != null) {
            this.myTimer.cancel();
            this.myTimer = null;
        }
        if (this.mTask != null) {
            this.mTask.cancel();
            this.mTask = null;
        }
    }

    private void createSingleImageFromMultipleImages(Bitmap bitmap, int mCounter) {
        if (mCounter == 0) {
            //TODO:Caused by: java.lang.OutOfMemoryError
            try {
                this.mManyBitmapSuperposition = Bitmap.createBitmap(DeviceInfo.mScreenWidthForPortrait, DeviceInfo.mScreenHeightForPortrait, bitmap.getConfig());
                this.mCanvas = new Canvas(this.mManyBitmapSuperposition);
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
                System.gc();
            } finally {

            }
        }
        if (this.mCanvas != null) {
            int number = DeviceInfo.mScreenHeightForPortrait / 64;
            if (mCounter >= (mCounter / number) * number && mCounter < ((mCounter / number) + SNOW_BLOCK) * number) {
                this.mCanvas.drawBitmap(bitmap, (float) ((mCounter / number) * 64), (float) ((mCounter % number) * 64), null);
            }
        }
    }

    private void rxJavaSolveMiZhiSuoJinAndNestedLoopAndCallbackHell() {
        Observable.from(ImageNameFactory.getAssetImageFolderName())
                .flatMap(new Func1<String, Observable<String>>() {
                    public Observable<String> call(String folderName) {
                        return Observable.from(ImageUtils.getAssetsImageNamePathList(MainActivity.this.getApplicationContext(), folderName));
                    }
                }).filter(new Func1<String, Boolean>() {
            public Boolean call(String imagePathNameAll) {
                return Boolean.valueOf(imagePathNameAll.endsWith(MainActivity.JPG));
            }
        }).map(new Func1<String, Bitmap>() {
            public Bitmap call(String imagePathName) {
                return ImageUtils.getImageBitmapFromAssetsFolderThroughImagePathName(MainActivity.this.getApplicationContext(), imagePathName, DeviceInfo.mScreenWidthForPortrait, DeviceInfo.mScreenHeightForPortrait);
            }
        }).map(new Func1<Bitmap, Void>() {
            public Void call(Bitmap bitmap) {
                MainActivity.this.createSingleImageFromMultipleImages(bitmap, MainActivity.this.mCounter);
                MainActivity.this.mCounter = MainActivity.this.mCounter++;
                return null;
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    public void call() {
                        MainActivity.this.mProgressBar.setVisibility(View.VISIBLE);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    public void onCompleted() {
                        MainActivity.this.mImageView.setImageBitmap(MainActivity.this.mManyBitmapSuperposition);
                        MainActivity.this.mProgressBar.setVisibility(View.GONE);
                        MainActivity.this.showAllViews();
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Void aVoid) {
                    }
                });
    }

    private void showAllViews() {
        this.mImageView.setVisibility(View.VISIBLE);
        this.mWhiteSnowView.setVisibility(View.VISIBLE);
    }

    private void clickEvent() {

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtils.isFastDoubleClick()) {
                    delayShowAll(0L);
                }
            }
        });
    }

    private void gotoNext() {
        this.mWebView.loadUrl(URL);

        delayDo();
    }

    private void delayShow(long time) {
        Observable.timer(time, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    public void onCompleted() {
                        MainActivity.this.mTypeTextView.start(MainActivity.LOVE);
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Long aLong) {
                    }
                });
    }


    private void delayShowTheSnow() {
        Observable.timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    public void onCompleted() {
                        mBlueSnowView.setVisibility(View.VISIBLE);
                        MainActivity.this.showRedHeartLayout();
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Long aLong) {
                    }
                });

    }

    private void delayDo() {
        Observable.timer(0, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    public void onCompleted() {
                        MainActivity.this.mWhiteSnowView.setVisibility(View.GONE);
                        MainActivity.this.mWebView.setVisibility(View.VISIBLE);
                        MainActivity.this.idGoWebView.setVisibility(View.VISIBLE);
                        MainActivity.this.delayShow(5100);//延时显示显示打印机
                        MainActivity.this.mWebView.loadUrl(MainActivity.URL);
                        MainActivity.this.idGoWebView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, BrowserActivity.class));
                            }
                        });
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Long aLong) {
                    }
                });
    }

    private void delayShowAll(long time) {
        Observable.timer(time, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    public void onCompleted() {
                        MainActivity.this.gotoNext();
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Long aLong) {
                        Log.w(TAG, "along is : " + aLong);
                    }

                });
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    private void showRedHeartLayout() {
        Observable.timer(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    public void onCompleted() {
                        mHeartLayout.setVisibility(View.VISIBLE);
                        MainActivity.this.delayDo2();
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Long aLong) {
                    }
                });
    }

    private void delayDo2() {
        Observable.timer((long) this.mRandom2.nextInt(200), TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    public void onCompleted() {
                        MainActivity.this.mHeartLayout.addHeart(MainActivity.this.randomColor());
                        MainActivity.this.delayDo2();
                    }

                    public void onError(Throwable e) {
                    }

                    public void onNext(Long aLong) {

                    }
                });
    }


    /**
     * remove View Drawables
     *
     * @param view
     */
    private void unBindDrawables(View view) {
        if (view != null) {
            try {
                Drawable drawable = view.getBackground();
                if (drawable != null) {
                    drawable.setCallback(null);
                } else {
                }
                if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    int viewGroupChildCount = viewGroup.getChildCount();
                    for (int j = 0; j < viewGroupChildCount; j++) {
                        unBindDrawables(viewGroup.getChildAt(j));
                    }
                    viewGroup.removeAllViews();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG,"onPause");
    }
}
