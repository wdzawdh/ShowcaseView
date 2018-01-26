package com.cw.showcase.showcaseview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cw.showcase.showcaseview.animation.AlphaAnimationFactory;
import com.cw.showcase.showcaseview.animation.IAnimationFactory;
import com.cw.showcase.showcaseview.queue.ShowcaseQueue;
import com.cw.showcase.showcaseview.shape.CircleShape;
import com.cw.showcase.showcaseview.shape.IShape;
import com.cw.showcase.showcaseview.shape.OvalShape;
import com.cw.showcase.showcaseview.shape.RectangleShape;
import com.cw.showcase.showcaseview.target.ViewTarget;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Cw
 * @date 2017/7/24
 */
public class ShowcaseView extends FrameLayout implements View.OnClickListener {

    public static final String PREFERENCE_NAME = ShowcaseView.class.getSimpleName();

    public static final int CIRCLE_SHAPE = 0;
    public static final int RECTANGLE_SHAPE = 1;
    public static final int OVAL_SHAPE = 2;

    public static int sShowIndex = 0;//展示的个数

    private String mMaskColor = "#BB000000";//蒙版的背景颜色
    private boolean mDismissOnTouch;//是否触摸任意地方消失
    private int mTargetPadding;//透明块的内边距
    private long mShowDuration = -1;//show的渐显时间
    private long mMissDuration = -1;//miss的渐隐时间
    private long mDismissDuration;//设置自动消失时间
    private String mOnlyOneTag;//只展示一次的标示

    private Activity mActivity;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Canvas mCanvas;
    private ViewGroup mDecorView;
    private AbsoluteLayout mContentView;
    private Map<ViewTarget, IShape> mTargets = new HashMap<>();
    private Map<ViewGroup, ViewTarget> mOriginals = new HashMap<>();
    private AlphaAnimationFactory mAnimationFactory = new AlphaAnimationFactory();

    public ShowcaseView(@NonNull Activity act) {
        this(act, null);
    }

    public ShowcaseView(@NonNull Activity act, @Nullable AttributeSet attrs) {
        this(act, attrs, 0);
    }

    public ShowcaseView(@NonNull Activity act, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(act, attrs, defStyleAttr);
        init(act);
    }

    private void init(Activity act) {
        this.mActivity = act;
        //ViewGroup重写onDraw，需要调用setWillNotDraw(false)
        setWillNotDraw(false);
        setOnClickListener(this);
        mContentView = new AbsoluteLayout(act);
        this.addView(mContentView);
        mDecorView = (ViewGroup) mActivity.getWindow().getDecorView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        if (mCanvas == null) {
            mCanvas = new Canvas(mBitmap);
        }
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.TRANSPARENT);
            //将canvas置为透明的重要方法
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mCanvas.drawColor(Color.parseColor(mMaskColor));

        for (Map.Entry<ViewTarget, IShape> entry : mTargets.entrySet()) {
            ViewTarget target = entry.getKey();
            IShape shape = entry.getValue();
            shape.draw(mCanvas, mPaint, target, mTargetPadding);
        }
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public void onClick(View v) {
        if (mDismissOnTouch) {
            removeFromWindow();
        }
    }


    //---------------------------------------Builder------------------------------------------------


    /**
     * Builder创建类,对showcaseView创建并进行一些配置
     */
    public static class Builder {

        private ShowcaseView showcaseView;

        public Builder(Activity activity) {
            showcaseView = new ShowcaseView(activity);
        }

        /**
         * 设置蒙版颜色
         */
        public Builder setMaskColor(String color) {
            showcaseView.setMaskColor(color);
            return this;
        }

        /**
         * 设置渐显时间
         *
         * @param showDur show的渐显时间 (-1 没有动画)
         * @param missDur miss的渐隐时间
         */
        public Builder setDuration(long showDur, long missDur) {
            showcaseView.setDuration(showDur, missDur);
            return this;
        }

        /**
         * 设置自动消失时间
         *
         * @param missDur 自动消失时间时间
         */
        public Builder setDismissDuration(long missDur) {
            showcaseView.setDismissDuration(missDur);
            return this;
        }

        /**
         * 触摸任意地方消失
         */
        public Builder setDismissOnTouch(boolean dismiss) {
            showcaseView.setDismissOnTouch(dismiss);
            return this;
        }

        /**
         * 设置透明块的内边距 dp
         */
        public Builder setTargetPadding(int padding) {
            showcaseView.setTargetPadding(padding);
            return this;
        }

        /**
         * 设置点击蒙版消失的View
         */
        public Builder setDismissView(View view) {
            showcaseView.setDismissView(view);
            return this;
        }

        /**
         * 设置只展示一次的标示
         */
        public Builder setOnlyOneTag(String tag) {
            showcaseView.setOnlyOneTag(tag);
            return this;
        }

        /**
         * 设置透明块的样式
         * <p>
         * 默认 CIRCLE_SHAPE 圆形
         */
        public Builder addTarget(View view) {
            showcaseView.addTarget(view);
            return this;
        }

        /**
         * 设置透明块的样式
         *
         * @param view      view
         * @param shapeMode CIRCLE_SHAPE 圆形 RECTANGLE_SHAPE 矩形 OVAL_SHAPE 椭圆
         */
        public Builder addTarget(View view, int shapeMode) {
            showcaseView.addTarget(view, shapeMode);
            return this;
        }

        /**
         * 增加展示的图片
         *
         * @param resId   resId
         * @param xWeight x坐标-权重（总共10.0f，例如5.0f就在屏幕中间）
         * @param yWeight y坐标-权重（总共10.0f）
         * @param scale   缩放比例
         * @param miss    是否点击蒙版消失
         */
        public Builder addImage(int resId, float xWeight, float yWeight, float scale, boolean miss) {
            showcaseView.addImage(resId, xWeight, yWeight, scale, miss);
            return this;
        }

        /**
         * 增加展示的图片
         *
         * @param resId     resId
         * @param xWeight   x坐标-权重（总共10.0f，例如5.0f就在屏幕中间）
         * @param yWeight   y坐标-权重（总共10.0f）
         * @param scale     缩放比例
         * @param miss      是否点击蒙版消失
         * @param animation 动画
         */
        public Builder addImage(int resId, float xWeight, float yWeight, float scale, boolean miss, Animation animation) {
            showcaseView.addImage(resId, xWeight, yWeight, scale, miss, animation);
            return this;
        }

        /**
         * 增加展示的View
         *
         * @param view    view
         * @param width   view width
         * @param height  view height
         * @param xWeight x坐标-权重（总共10.0f）
         * @param yWeight y坐标-权重（总共10.0f）
         */
        public Builder addShowView(View view, int width, int height, float xWeight, float yWeight) {
            showcaseView.addShowView(view, width, height, xWeight, yWeight);
            return this;
        }

        /**
         * 将下层View浮动到蒙层上来
         *
         * @param view view
         */
        public Builder addFloatView(View view) {
            showcaseView.addFloatView(view);
            return this;
        }

        /**
         * 监听show和dismiss的事件
         */
        public Builder addShowcaseListener(ShowcaseListener listener) {
            showcaseView.addShowcaseListener(listener);
            return this;
        }

        /**
         * 添加到展示队列
         */
        public Builder addShowcaseQueue() {
            if (!showcaseView.hasTag()) {
                showcaseView.addShowQueue();
            }
            String maskColor = showcaseView.mMaskColor;
            boolean dismissOnTouch = showcaseView.mDismissOnTouch;
            int targetPadding = showcaseView.mTargetPadding;
            long showDuration = showcaseView.mShowDuration;
            long missDuration = showcaseView.mMissDuration;
            long dismissDuration = showcaseView.mDismissDuration;
            String onlyOneTag = showcaseView.mOnlyOneTag;
            //重建ShowcaseView，保留set系列的属性
            showcaseView = new ShowcaseView(showcaseView.mActivity);
            showcaseView.mMaskColor = maskColor;
            showcaseView.mDismissOnTouch = dismissOnTouch;
            showcaseView.mTargetPadding = targetPadding;
            showcaseView.mShowDuration = showDuration;
            showcaseView.mMissDuration = missDuration;
            showcaseView.mDismissDuration = dismissDuration;
            showcaseView.mOnlyOneTag = onlyOneTag;
            return this;
        }

        public ShowcaseView build() {
            return showcaseView;
        }
    }


    //---------------------------------------Method-------------------------------------------------


    public static boolean hasShow() {
        return sShowIndex != 0;
    }

    public static boolean hasTag(Context context, String tag) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(tag, false);
    }

    /**
     * 显示ShowcaseView
     */
    public void show() {
        if (hasTag()) {
            return;
        }
        mAnimationFactory.fadeInView(this, mShowDuration, new IAnimationFactory.AnimationStartListener() {
            @Override
            public void onAnimationStart() {
                mDecorView.post(new Runnable() {
                    @Override
                    public void run() {
                        sShowIndex++;
                        //浮动View
                        for (Map.Entry<ViewGroup, ViewTarget> entry : mOriginals.entrySet()) {
                            ViewGroup parent = entry.getKey();
                            ViewTarget viewTarget = entry.getValue();
                            Rect bounds = viewTarget.getBounds();
                            Point point = viewTarget.getPoint();
                            parent.removeView(viewTarget.getView());
                            addShowView(viewTarget.getView(), bounds.width(), bounds.height(), point.x, point.y);
                        }
                        mDecorView.addView(ShowcaseView.this);
                        if (mListener != null) {
                            mListener.onDisplay(ShowcaseView.this);
                        }
                        //自动消失
                        if (mDismissDuration > 0) {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    removeFromWindow();
                                }
                            }, mDismissDuration);
                        }
                    }
                });
            }
        });
    }

    /**
     * 将ShowcaseView从Window移除
     */
    public void removeFromWindow() {
        mAnimationFactory.fadeOutView(this, mMissDuration, new IAnimationFactory.AnimationEndListener() {

            @Override
            public void onAnimationEnd() {
                sShowIndex--;
                //队列为空时putTag(要在QueueListener前调用)
                if (ShowcaseQueue.getInstance().getSize() == 0) {
                    putTag();
                }

                mDecorView.post(new Runnable() {
                    @Override
                    public void run() {
                        mContentView.removeAllViews();
                        //移除ShowcaseView
                        mDecorView.removeView(ShowcaseView.this);
                        //将浮动的View还给原始父布局
                        for (Map.Entry<ViewGroup, ViewTarget> entry : mOriginals.entrySet()) {
                            ViewTarget viewTarget = entry.getValue();
                            entry.getKey().addView(viewTarget.getView(), viewTarget.getOriginalIndex(), viewTarget.getOriginalLayoutParams());
                        }
                        if (mBitmap != null) {
                            mBitmap.recycle();
                            mBitmap = null;
                        }
                        mTargets = null;
                        mCanvas = null;
                        mPaint = null;
                    }
                });

                if (mListener != null) {
                    mListener.onDismiss(ShowcaseView.this);
                }
                if (mQueueListener != null) {
                    mQueueListener.onDismiss();
                }
            }
        });
    }

    /**
     * 添加到显示队列
     */
    public ShowcaseQueue addShowQueue() {
        ShowcaseQueue showcaseQueue = ShowcaseQueue.getInstance();
        showcaseQueue.add(this);
        return showcaseQueue;
    }

    /**
     * 依次展示队列里的showcaseView
     */
    public void showQueue() {
        ShowcaseQueue.getInstance().showQueue();
    }

    /**
     * 设置蒙版颜色
     */
    public void setMaskColor(String color) {
        mMaskColor = color;
    }

    /**
     * 设置渐显时间
     *
     * @param showDur show的渐显时间 (-1 没有动画)
     * @param missDur miss的渐隐时间
     */
    public void setDuration(long showDur, long missDur) {
        mShowDuration = showDur;
        mMissDuration = missDur;
    }

    /**
     * 设置自动消失时间
     *
     * @param missDur 自动消失时间时间
     */
    public void setDismissDuration(long missDur) {
        mDismissDuration = missDur;
    }

    /**
     * 触摸任意地方消失
     */
    public void setDismissOnTouch(boolean dismiss) {
        mDismissOnTouch = dismiss;
    }

    /**
     * 设置透明块的内边距 dp
     */
    public void setTargetPadding(int padding) {
        //dip转换px
        float scale = mActivity.getResources().getDisplayMetrics().density;
        mTargetPadding = (int) (padding * scale + 0.5f);
    }

    /**
     * 设置点击蒙版消失的View
     */
    public void setDismissView(final View view) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ShowcaseView.this.getVisibility() == View.VISIBLE && event.getAction() == MotionEvent.ACTION_DOWN) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //延迟500ms保证View的onClick先执行
                            removeFromWindow();
                            view.setOnTouchListener(null);
                        }
                    }, 500);
                }
                return false;
            }
        });
    }

    /**
     * 设置只展示一次的标示
     */
    public void setOnlyOneTag(String tag) {
        mOnlyOneTag = tag;
    }

    /**
     * 设置透明块的样式
     * <p>
     * 默认 CIRCLE_SHAPE 圆形
     */
    public void addTarget(View view) {
        addTarget(view, CIRCLE_SHAPE);
    }

    /**
     * 设置透明块的样式
     *
     * @param view      view
     * @param shapeMode CIRCLE_SHAPE 圆形 RECTANGLE_SHAPE 矩形 OVAL_SHAPE 椭圆
     */
    public void addTarget(View view, int shapeMode) {
        if (view == null || hasTag()) {
            return;
        }
        switch (shapeMode) {
            case CIRCLE_SHAPE:
                mTargets.put(new ViewTarget(view), new CircleShape());
                break;
            case RECTANGLE_SHAPE:
                mTargets.put(new ViewTarget(view), new RectangleShape());
                break;
            case OVAL_SHAPE:
                mTargets.put(new ViewTarget(view), new OvalShape());
                break;
            default:
                mTargets.put(new ViewTarget(view), new CircleShape());
                break;
        }
    }

    /**
     * 增加展示的图片
     *
     * @param resId   resId
     * @param xWeight x坐标-权重（总共10.0f，例如5.0f就在屏幕中间）
     * @param yWeight y坐标-权重（总共10.0f）
     * @param scale   缩放比例  （1.0f时图片宽为屏幕的一半，高度等比例缩放）
     * @param miss    是否点击蒙版消失
     */
    public void addImage(int resId, float xWeight, float yWeight, float scale, boolean miss) {
        addImage(resId, xWeight, yWeight, scale, miss, null);
    }

    /**
     * 增加展示的图片
     *
     * @param resId     resId
     * @param xWeight   x坐标-权重（总共10.0f，例如5.0f就在屏幕中间）
     * @param yWeight   y坐标-权重（总共10.0f）
     * @param scale     缩放比例  （1.0f时图片宽为屏幕的一半，高度等比例缩放）
     * @param miss      是否点击蒙版消失
     * @param animation 动画
     */
    public void addImage(int resId, float xWeight, float yWeight, float scale, boolean miss, Animation animation) {
        ImageView imageView = new ImageView(mActivity.getApplicationContext());
        imageView.setImageResource(resId);
        if (miss) setDismissView(imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        options.inJustDecodeBounds = false;
        float proportion = (float) options.outHeight / options.outWidth;
        WindowManager wm = mActivity.getWindowManager();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        float width = windowWidth / 2 * scale;
        float height = width * proportion * scale;
        addShowView(imageView, (int) width, (int) height, xWeight, yWeight);
        if (animation != null) imageView.startAnimation(animation);
    }

    /**
     * 增加展示的View
     *
     * @param view    view
     * @param width   view width
     * @param height  view height
     * @param xWeight x坐标-权重（总共10.0f）
     * @param yWeight y坐标-权重（总共10.0f）
     */
    public void addShowView(View view, int width, int height, float xWeight, float yWeight) {
        WindowManager wm = mActivity.getWindowManager();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();
        float x = (float) windowWidth / 10 * xWeight;
        float y = (float) windowHeight / 10 * yWeight;
        addShowView(view, width, height, (int) x, (int) y);
    }

    /**
     * 将下层View浮动上来
     *
     * @param view view
     */
    public void addFloatView(final View view) {
        if (view == null || view.getParent() == null || hasTag()) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        int indexOfChild = parent.indexOfChild(view);
        ViewTarget viewTarget = new ViewTarget(view, indexOfChild);
        mOriginals.put(parent, viewTarget);
    }

    /**
     * 增加展示的View（不建议使用，px不利于屏幕适配）
     *
     * @param view   view
     * @param width  view width
     * @param height view height
     * @param x      展示位置的x坐标（px）
     * @param y      展示位置的y坐标（px）
     */
    @Deprecated
    public void addShowView(View view, int width, int height, int x, int y) {
        if (view == null || hasTag()) {
            return;
        }
        AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(
                width, height, x - width / 2, y - height / 2);
        mContentView.addView(view, layoutParams);
    }

    private boolean hasTag() {
        if (mOnlyOneTag == null) {
            return false;
        }
        SharedPreferences settings = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(mOnlyOneTag, false);
    }

    private boolean putTag() {
        if (mOnlyOneTag == null) {
            return false;
        }
        SharedPreferences settings = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(mOnlyOneTag, true);
        return editor.commit();
    }


    //---------------------------------------Listener-----------------------------------------------


    /**
     * 监听show和dismiss的事件
     */
    public void addShowcaseListener(ShowcaseListener listener) {
        mListener = listener;
    }

    private ShowcaseListener mListener;

    public interface ShowcaseListener {
        void onDisplay(ShowcaseView showcaseView);

        void onDismiss(ShowcaseView showcaseView);
    }

    /**
     * 不建议外部使用，只用于给ShowcaseQueue内部监听onDismiss。
     */
    @Deprecated
    public void addQueueListener(QueueListener queueListener) {
        mQueueListener = queueListener;
    }

    private QueueListener mQueueListener;

    public interface QueueListener {
        void onDismiss();
    }

}
