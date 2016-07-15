package com.cw.showcasedemo.showcaseview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cw.showcasedemo.R;
import com.cw.showcasedemo.showcaseview.animation.AnimationFactory;
import com.cw.showcasedemo.showcaseview.animation.IAnimationFactory;
import com.cw.showcasedemo.showcaseview.shape.CircleShape;
import com.cw.showcasedemo.showcaseview.shape.NoShape;
import com.cw.showcasedemo.showcaseview.shape.OvalShape;
import com.cw.showcasedemo.showcaseview.shape.RectangleShape;
import com.cw.showcasedemo.showcaseview.shape.Shape;
import com.cw.showcasedemo.showcaseview.squence.DetachedListener;
import com.cw.showcasedemo.showcaseview.squence.MaterialShowcaseSequence;
import com.cw.showcasedemo.showcaseview.target.ViewTarget;

import java.util.ArrayList;
import java.util.List;


/**
 * 展示ShowcaseView队列的帮助类
 */
public class ShowcaseView extends FrameLayout implements View.OnTouchListener, View.OnClickListener {

    private int mMaskColour = Color.parseColor(ConfigManager.DEFAULT_MASK_COLOUR);
    private int mShapePadding = ConfigManager.DEFAULT_SHAPE_PADDING;
    private long mFadeDurationInMillis = ConfigManager.DEFAULT_FADE_TIME;
    private long mDelayInMillis = ConfigManager.DEFAULT_DELAY;
    private boolean mWasDismissed = false;
    private boolean mDismissOnTouch = false;
    private boolean mShouldRender = false;
    private boolean mRenderOverNav = false;
    private boolean mShouldAnimate = true;
    private boolean mSingleUse = false;
    private int mOldWidth;
    private int mOldHeight;
    private ImageView mMainImageView;
    private ImageView mViceImageView;
    private ImageView mDismissImageView;
    private Context mContext;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mEraser;
    private Handler mHandler;
    private ArrayList<Shape> mShapes;
    private PrefsManager mPrefsManager;
    private AnimationFactory mAnimationFactory;
    private List<ShowcaseListener> mListeners;
    private DetachedListener mDetachedListener;
    private UpdateOnGlobalLayout mLayoutListener;
    private static List<ViewTarget> mTargets;

    public ShowcaseView(Context context) {
        super(context);
        init(context);
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShowcaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setWillNotDraw(false);
        // create our animation factory
        mAnimationFactory = new AnimationFactory();
        mListeners = new ArrayList<>();
        //添加一个全局布局侦听器，以便我们能够适应变化
        UpdateOnGlobalLayout mLayoutListener = new UpdateOnGlobalLayout();
        getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
        // consume touch events
        setOnTouchListener(this);
        setVisibility(INVISIBLE);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.showcase_content, this, true);
        mMainImageView = (ImageView) contentView.findViewById(R.id.iv_main);
        mViceImageView = (ImageView) contentView.findViewById(R.id.iv_vice);
        mDismissImageView = (ImageView) contentView.findViewById(R.id.iv_dismiss);
        mDismissImageView.setOnClickListener(this);
    }

    /**
     * 设置透明块
     */
    public void setTarget(List<ViewTarget> target) {
        mTargets = target;

        // update dismiss button state
        updateDismissButton();

        if (mTargets != null) {
            /**
             * 版本大于lollipop21才可以获取虚拟按键的高度
             */
            if (!mRenderOverNav && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int mBottomMargin = getSoftButtonsBarSizePort((Activity) getContext());
                FrameLayout.LayoutParams contentLP = (LayoutParams) getLayoutParams();

                if (contentLP != null && contentLP.bottomMargin != mBottomMargin)
                    contentLP.bottomMargin = mBottomMargin;
            }

            if (mTargets != null && mShapes != null && mTargets.size() == mShapes.size()) {
                for (int i = 0; i < mTargets.size(); i++) {
                    mShapes.get(i).updateTarget(mTargets.get(i));
                }
            }
        }
    }

    /**
     * 显示ShowcaseView
     */
    public boolean show(final Activity activity) {
        if (mSingleUse) {
            if (mPrefsManager.hasFired()) {
                return false;
            } else {
                mPrefsManager.setFired();
            }
        }
        ((ViewGroup) activity.getWindow().getDecorView()).addView(this);
        setShouldRender(true);
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mShouldAnimate) {
                    fadeIn();
                } else {
                    setVisibility(VISIBLE);
                    notifyOnDisplayed();
                }
            }
        }, mDelayInMillis);
        updateDismissButton();
        return true;
    }

    /**
     * 隐藏ShowcaseView
     */
    public void hide() {
        mWasDismissed = true;
        if (mShouldAnimate) {
            fadeOut();
        } else {
            removeFromWindow();
        }
    }

    /**
     * 逐渐显示
     */
    public void fadeIn() {
        setVisibility(INVISIBLE);

        mAnimationFactory.fadeInView(this, mFadeDurationInMillis,
                new IAnimationFactory.AnimationStartListener() {
                    @Override
                    public void onAnimationStart() {
                        setVisibility(View.VISIBLE);
                        notifyOnDisplayed();
                    }
                }
        );
    }

    /**
     * 逐渐移除
     */
    public void fadeOut() {

        mAnimationFactory.fadeOutView(this, mFadeDurationInMillis, new IAnimationFactory.AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(INVISIBLE);
                removeFromWindow();
            }
        });
    }

    /**
     * 添加ShowcaseView展示时的监听
     */
    public void addShowcaseListener(ShowcaseListener showcaseListener) {

        if (mListeners != null)
            mListeners.add(showcaseListener);
    }

    /**
     * 添加ShowcaseView移除时的监听
     */
    public void removeShowcaseListener(MaterialShowcaseSequence showcaseListener) {

        if ((mListeners != null) && mListeners.contains(showcaseListener)) {
            mListeners.remove(showcaseListener);
        }
    }

    /**
     * 设施队列监听
     */
    public void setDetachedListener(DetachedListener detachedListener) {
        mDetachedListener = detachedListener;
    }

    /**
     * 重置SharedPreferences的存储
     */
    public void resetSingleUse() {
        if (mSingleUse && mPrefsManager != null)
            mPrefsManager.resetShowcase();
    }

    /**
     * 通过showcaseID重置ShowcaseView只打开一次的tager
     */
    public static void resetSingleUse(Context context, String showcaseID) {
        PrefsManager.resetShowcase(context, showcaseID);
    }

    /**
     * 重置所有ShowcaseView只打开一次的tager
     */
    public static void resetAll(Context context) {
        PrefsManager.resetAll(context);
    }

    /**
     * 是否tager已经是已显示状态
     */
    public boolean hasFired() {
        return mPrefsManager.hasFired();
    }

    /**
     * 设置默认config object
     */
    public void setConfig(ConfigManager config) {
        setDelay(config.getDelay());
        setFadeDuration(config.getFadeDuration());
        setMaskColour(config.getMaskColor());
        addShape(config.getShape());
        setShapePadding(config.getShapePadding());
        setRenderOverNavigationBar(config.getRenderOverNavigationBar());
    }

    /**
     * Builder工具类,对showcaseView创建并进行一些配置
     */
    public static class Builder {

        private static final int NO_SHAPE = 0;
        private static final int OVAL_SHAPE = 3;
        private static final int CIRCLE_SHAPE = 1;
        private static final int RECTANGLE_SHAPE = 2;
        private int shapeType = CIRCLE_SHAPE;
        private ShowcaseView showcaseView;
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
            showcaseView = new ShowcaseView(activity);
        }

        /**
         * 设置 背景的颜色
         */
        public Builder setMaskColour(int maskColour) {
            showcaseView.setMaskColour(maskColour);
            return this;
        }

        /**
         * 设置透明块在哪个View上
         */
        public Builder setTarget(View... target) {
            ArrayList<ViewTarget> viewTargets = new ArrayList<>();
            for (View aTarget : target) {
                viewTargets.add(new ViewTarget(aTarget));
            }
            showcaseView.setTarget(viewTargets);
            return this;
        }

        /**
         * 设置点击消失图片在ShowcaseView上
         */
        public Builder setDismissImage(int imageResourceId) {
            showcaseView.setDismissImage(imageResourceId);
            return this;
        }

        public Builder setDismissImage(int imageResourceId, int width, int height) {
            showcaseView.setDismissImage(imageResourceId);
            setDismissImageSize(width, height);
            return this;
        }

        public Builder setDismissImage(int imageResourceId, int width, int height, int x, int y) {
            showcaseView.setDismissImage(imageResourceId);
            setDismissImageSize(width, height);
            setDismissImagePosition(x, y);
            return this;
        }

        /**
         * 设置副图片在ShowcaseView上
         */
        public Builder setViceImage(int imageResourceId) {
            showcaseView.setViceImage(imageResourceId);
            return this;
        }

        public Builder setViceImage(int imageResourceId, int width, int height) {
            showcaseView.setViceImage(imageResourceId);
            setViceImageSize(width, height);
            return this;
        }

        public Builder setViceImage(int imageResourceId, int width, int height, int x, int y) {
            showcaseView.setViceImage(imageResourceId);
            setViceImageSize(width, height);
            setViceImagePosition(x, y);
            return this;
        }

        /**
         * 设置主图片在ShowcaseView上
         */
        public Builder setMainImage(int imageResourceId) {
            showcaseView.setMainImage(imageResourceId);
            return this;
        }

        public Builder setMainImage(int imageResourceId, int width, int height) {
            showcaseView.setMainImage(imageResourceId);
            setMainImageSize(width, height);
            return this;
        }

        public Builder setMainImage(int imageResourceId, int width, int height, int x, int y) {
            showcaseView.setMainImage(imageResourceId);
            setMainImageSize(width, height);
            setMainImagePosition(x, y);
            return this;
        }

        /**
         * 设置图片大小(dp)
         */
        public Builder setMainImageSize(int width, int height) {
            showcaseView.setMainImageSize(width, height);
            return this;
        }

        public Builder setViceImageSize(int width, int height) {
            showcaseView.setViceImageSize(width, height);
            return this;
        }

        public Builder setDismissImageSize(int width, int height) {
            showcaseView.setDismissImageSize(width, height);
            return this;
        }

        /**
         * 设置图片的坐标(dp)
         */
        public Builder setMainImagePosition(int x, int y) {
            showcaseView.setMainImagePosition(x, y);
            return this;
        }

        public Builder setViceImagePosition(int x, int y) {
            showcaseView.setViceImagePosition(x, y);
            return this;
        }

        public Builder setDismissImagePosition(int x, int y) {
            showcaseView.setDismissImagePosition(x, y);
            return this;
        }

        /**
         * 设置推迟显示的时间(ms)
         */
        public Builder setDelay(int delayInMillis) {
            showcaseView.setDelay(delayInMillis);
            return this;
        }

        /**
         * 设置逐渐显示和消失的时间(ms)
         */
        public Builder setFadeDuration(int fadeDurationInMillis) {
            showcaseView.setFadeDuration(fadeDurationInMillis);
            return this;
        }

        /**
         * 设置在SharedPreferences中存状态的key
         */
        public Builder singleUse(String showcaseID) {
            showcaseView.singleUse(showcaseID);
            return this;
        }

        /**
         * 设置自定义透明块
         */
        public Builder setShapes(Shape... shape) {
            for (Shape aShape : shape) {
                showcaseView.addShape(aShape);
            }
            return this;
        }

        /**
         * 设置圆形透明块
         */
        public Builder withCircleShape() {
            shapeType = CIRCLE_SHAPE;
            return this;
        }

        /**
         * 设置圆形透明块
         */
        public Builder withOvalShape() {
            shapeType = OVAL_SHAPE;
            return this;
        }

        /**
         * 设置没有透明块
         */
        public Builder withoutShape() {
            shapeType = NO_SHAPE;
            return this;
        }

        /**
         * 设置矩形透明块
         */
        public Builder withRectangleShape() {
            return withRectangleShape(false);
        }

        public Builder withRectangleShape(boolean fullWidth) {
            this.shapeType = RECTANGLE_SHAPE;
            RectangleShape.setFullWidth(fullWidth);
            return this;
        }

        /**
         * 设置透明块的padding值
         */
        public Builder setShapePadding(int padding) {
            showcaseView.setShapePadding(padding);
            return this;
        }

        /**
         * 使ShowcaseView覆盖在虚拟按键上
         */
        public Builder renderOverNavigationBar() {
            // Note: This only has an effect in Lollipop or above.
            showcaseView.setRenderOverNavigationBar(true);
            return this;
        }

        /**
         * 添加ShowcaseView展示时的监听
         */
        public Builder setListener(ShowcaseListener listener) {
            showcaseView.addShowcaseListener(listener);
            return this;
        }

        /**
         * 设置是否点击任意地方都dismiss
         */
        public Builder setDismissOnTouch(boolean dismissOnTouch) {
            showcaseView.setDismissOnTouch(dismissOnTouch);
            return this;
        }

        /**
         * 设置点击跳转的activity
         */
        public Builder setActOnDissmiss(final Class activityClass){
            this.setListener(new ShowcaseListener() {
                @Override
                public void onShowcaseDisplayed(ShowcaseView showcaseView) {

                }

                @Override
                public void onShowcaseDismissed(ShowcaseView showcaseView) {
                    activity.startActivity(new Intent(activity,activityClass));
                }
            });
            return this;
        }

        public ShowcaseView build() {
            for (ViewTarget aTarget : mTargets) {
                if (aTarget != null) {
                    switch (shapeType) {
                        //矩形透明块
                        case RECTANGLE_SHAPE: {
                            showcaseView.addShape(new RectangleShape());
                            break;
                        }
                        //圆形透明块
                        case CIRCLE_SHAPE: {
                            showcaseView.addShape(new CircleShape());
                            break;
                        }
                        //椭圆透明块
                        case OVAL_SHAPE: {
                            showcaseView.addShape(new OvalShape(aTarget));
                            break;
                        }
                        //无透明块
                        case NO_SHAPE: {
                            showcaseView.addShape(new NoShape());
                            break;
                        }
                        default:
                            throw new IllegalArgumentException("Unsupported shape type: " + shapeType);
                    }
                }
            }
            return showcaseView;
        }

        public ShowcaseView show() {
            build().show(activity);
            return showcaseView;
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mShouldRender)
            return;
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (width <= 0 || height <= 0)
            return;
        if (mBitmap == null || mCanvas == null || mOldHeight != height || mOldWidth != width) {
            if (mBitmap != null)
                mBitmap.recycle();
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        mOldWidth = width;
        mOldHeight = height;
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawColor(mMaskColour);
        if (mEraser == null) {
            mEraser = new Paint();
            mEraser.setColor(0xFFFFFFFF);
            //将canvas置为透明的重要方法
            mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
        if (mShapes != null && mTargets != null && mShapes.size() == mTargets.size()) {
            for (int i = 0; i < mShapes.size(); i++) {
                mShapes.get(i).draw(mCanvas, mEraser
                        , mTargets.get(i).getPoint().x
                        , mTargets.get(i).getPoint().y
                        , mShapePadding);
            }
        }
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //视图从窗体上分离时调用
        if (!mWasDismissed && mSingleUse && mPrefsManager != null) {
            mPrefsManager.resetShowcase();
        }
        notifyOnDismissed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mDismissOnTouch) {
            hide();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        hide();
    }


    //----------------------------------------------------------------------------------------------


    /**
     * 将ShowcaseView从Window
     */
    private void removeFromWindow() {
        if (getParent() != null && getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mEraser = null;
        mAnimationFactory = null;
        mCanvas = null;
        mHandler = null;
        getViewTreeObserver().removeGlobalOnLayoutListener(mLayoutListener);
        mLayoutListener = null;
        if (mPrefsManager != null)
            mPrefsManager.close();
        mPrefsManager = null;
    }

    /**
     * 获取虚拟按键的高度
     */
    private static int getSoftButtonsBarSizePort(Activity activity) {
        //getMetrics获取的像素宽高不包含虚拟键所占空间
        //getRealMetrics->API 17之后使用，获取的像素宽高包含虚拟键所占空间，在API 17之前通过反射获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    /**
     * 设置是否将ShowcaseView覆盖在虚拟按键上
     */
    private void setRenderOverNavigationBar(boolean mRenderOverNav) {
        this.mRenderOverNav = mRenderOverNav;
    }

    /**
     * 通知监听器
     */
    private void notifyOnDisplayed() {

        if (mListeners != null) {
            for (ShowcaseListener listener : mListeners) {
                listener.onShowcaseDisplayed(this);
            }
        }
    }

    /**
     * 将视图从窗体上分离的时候调用该方法
     */
    private void notifyOnDismissed() {
        if (mListeners != null) {
            for (ShowcaseListener listener : mListeners) {
                listener.onShowcaseDismissed(this);
            }
            mListeners.clear();
            mListeners = null;
        }
        //队列监听
        if (mDetachedListener != null) {
            mDetachedListener.onShowcaseDetached(this, mWasDismissed);
        }
    }

    /**
     * 设置自定义Shape
     */
    private void addShape(Shape mShape) {
        if (mShapes == null) {
            mShapes = new ArrayList<>();
        }
        this.mShapes.add(mShape);
    }

    private void setShapePadding(int padding) {
        mShapePadding = padding;
    }

    private void setDismissOnTouch(boolean dismissOnTouch) {
        mDismissOnTouch = dismissOnTouch;
    }

    private void setShouldRender(boolean shouldRender) {
        mShouldRender = shouldRender;
    }

    private void setMaskColour(int maskColour) {
        mMaskColour = maskColour;
    }

    private void setDelay(long delayInMillis) {
        mDelayInMillis = delayInMillis;
    }

    private void setFadeDuration(long fadeDurationInMillis) {
        mFadeDurationInMillis = fadeDurationInMillis;
    }

    private void setMainImage(int imageResourceId) {
        if (mMainImageView != null) {
            mMainImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mMainImageView.setImageResource(imageResourceId);
        }
    }

    private void setViceImage(int imageResourceId) {
        if (mViceImageView != null) {
            mViceImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mViceImageView.setImageResource(imageResourceId);
        }
    }

    private void setDismissImage(int imageResourceId) {
        if (mDismissImageView != null) {
            mDismissImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mDismissImageView.setImageResource(imageResourceId);
            updateDismissButton();
        }
    }

    private void setMainImagePosition(int x, int y) {
        if (mMainImageView != null) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mMainImageView.getLayoutParams();
            layoutParams.x = dip2px(x);
            layoutParams.y = dip2px(y);
            mMainImageView.setLayoutParams(layoutParams);
        }
    }

    private void setViceImagePosition(int x, int y) {
        if (mViceImageView != null) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mViceImageView.getLayoutParams();
            layoutParams.x = dip2px(x);
            layoutParams.y = dip2px(y);
            mViceImageView.setLayoutParams(layoutParams);
        }
    }

    private void setDismissImagePosition(int x, int y) {
        if (mDismissImageView != null) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mDismissImageView.getLayoutParams();
            layoutParams.x = dip2px(x);
            layoutParams.y = dip2px(y);
            mDismissImageView.setLayoutParams(layoutParams);
        }
    }

    private void setMainImageSize(int width, int height) {
        if (mMainImageView != null) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mMainImageView.getLayoutParams();
            layoutParams.width = dip2px(width);
            layoutParams.height = dip2px(height);
            mMainImageView.setLayoutParams(layoutParams);
        }
    }

    private void setViceImageSize(int width, int height) {
        if (mViceImageView != null) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mViceImageView.getLayoutParams();
            layoutParams.width = dip2px(width);
            layoutParams.height = dip2px(height);
            mViceImageView.setLayoutParams(layoutParams);
        }
    }

    private void setDismissImageSize(int width, int height) {
        if (mDismissImageView != null) {
            AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) mDismissImageView.getLayoutParams();
            layoutParams.width = dip2px(width);
            layoutParams.height = dip2px(height);
            mDismissImageView.setLayoutParams(layoutParams);
        }
    }

    private void singleUse(String showcaseID) {
        mSingleUse = true;
        mPrefsManager = new PrefsManager(getContext(), showcaseID);
    }

    /**
     * DismissButton是否显示
     */
    private void updateDismissButton() {
        // hide or show button
        if (mDismissImageView != null) {
            if (mDismissImageView.getDrawable() == null) {
                mDismissImageView.setVisibility(GONE);
            } else {
                mDismissImageView.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 每次完成activity时调用
     */
    private class UpdateOnGlobalLayout implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            setTarget(mTargets);
        }
    }

    /**
     * dip转换px
     */
    private int dip2px(int dip) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */
    private int px2dip(int px) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

}
