package com.cw.showcasedemo.showcaseview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cw.showcasedemo.showcaseview.target.Target;


public class NoShape implements Shape {

    @Override
    public void updateTarget(Target target) {
        // do nothing
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {
        // do nothing
    }

}
