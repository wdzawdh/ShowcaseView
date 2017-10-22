package com.cw.showcase.showcaseview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cw.showcase.showcaseview.target.ViewTarget;


public interface IShape {

    void draw(Canvas canvas, Paint paint, ViewTarget target, int padding);

}
