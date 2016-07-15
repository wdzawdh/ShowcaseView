package com.cw.showcasedemo.showcaseview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cw.showcasedemo.showcaseview.target.Target;


public interface Shape {

    void draw(Canvas canvas, Paint paint, int x, int y, int padding);

    void updateTarget(Target target);

}
