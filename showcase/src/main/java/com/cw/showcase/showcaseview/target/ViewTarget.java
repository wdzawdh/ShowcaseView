package com.cw.showcase.showcaseview.target;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;


public class ViewTarget {

    private View mView;
    private ViewGroup.LayoutParams mOriginalLayoutParams;
    private int mOriginalIndex = -1;

    public ViewTarget(View view) {
        this(view, -1);
    }

    public ViewTarget(View view, int index) {
        mView = view;
        mOriginalLayoutParams = view.getLayoutParams();
        mOriginalIndex = index;
    }

    public View getView() {
        return mView;
    }

    public ViewGroup.LayoutParams getOriginalLayoutParams() {
        return mOriginalLayoutParams;
    }

    public int getOriginalIndex() {
        return mOriginalIndex;
    }

    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 2;
        int y = location[1] + mView.getHeight() / 2;
        return new Point(x, y);
    }

    public Rect getBounds() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        return new Rect(
                location[0],
                location[1],
                location[0] + mView.getMeasuredWidth(),
                location[1] + mView.getMeasuredHeight()
        );
    }
}
