package com.cw.showcase.showcaseview.shape;


import android.graphics.Canvas;
import android.graphics.Paint;

import com.cw.showcase.showcaseview.target.ViewTarget;


public class RectangleShape implements IShape {

    @Override
    public void draw(Canvas canvas, Paint paint, ViewTarget target, int padding) {
        canvas.drawRect(
                target.getBounds().left - padding,
                target.getBounds().top - padding,
                target.getBounds().right + padding,
                target.getBounds().bottom + padding,
                paint
        );
    }
}