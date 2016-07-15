package com.cw.showcasedemo.showcaseview;

import android.graphics.Color;

import com.cw.showcasedemo.showcaseview.shape.CircleShape;
import com.cw.showcasedemo.showcaseview.shape.Shape;


/**
 * 关于showcaseview的默认配置类
 */
public class ConfigManager {

    public static final String DEFAULT_MASK_COLOUR = "#99000000";
    public static final long DEFAULT_FADE_TIME = 300;
    public static final long DEFAULT_DELAY = 0;
    public static final Shape DEFAULT_SHAPE = new CircleShape();
    public static final int DEFAULT_SHAPE_PADDING = 10;

    private long mDelay = DEFAULT_DELAY;
    private int mMaskColour = Color.parseColor(ConfigManager.DEFAULT_MASK_COLOUR);
    private long mFadeDuration = DEFAULT_FADE_TIME;
    private Shape mShape = DEFAULT_SHAPE;
    private int mShapePadding = DEFAULT_SHAPE_PADDING;
    //是否在覆盖在虚拟按键的上面,false代表会计算虚拟按键的高度并作为bottomMargin设置
    private boolean renderOverNav = false;

    public long getDelay() {
        return mDelay;
    }

    public void setDelay(long delay) {
        this.mDelay = delay;
    }

    public int getMaskColor() {
        return mMaskColour;
    }

    public void setMaskColor(int maskColor) {
        mMaskColour = maskColor;
    }

    public long getFadeDuration() {
        return mFadeDuration;
    }

    public void setFadeDuration(long fadeDuration) {
        this.mFadeDuration = fadeDuration;
    }

    public Shape getShape() {
        return mShape;
    }

    public void setShape(Shape shape) {
        this.mShape = shape;
    }

    public int getShapePadding() {
        return mShapePadding;
    }

    public void setShapePadding(int padding) {
        this.mShapePadding = padding;
    }

    public boolean getRenderOverNavigationBar() {
        return renderOverNav;
    }

    public void setRenderOverNavigationBar(boolean renderOverNav) {
        this.renderOverNav = renderOverNav;
    }
}
