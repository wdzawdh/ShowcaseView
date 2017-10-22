package com.cw.showcaseview.showcaseview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cw.showcaseview.showcaseview.target.ViewTarget;


public interface IShape {

    void draw(Canvas canvas, Paint paint, ViewTarget target, int padding);

}
