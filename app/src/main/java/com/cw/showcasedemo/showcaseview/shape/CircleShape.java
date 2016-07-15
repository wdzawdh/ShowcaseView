package com.cw.showcasedemo.showcaseview.shape;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.cw.showcasedemo.showcaseview.target.Target;


/**
 * Circular shape for target.
 */
public class CircleShape implements Shape {

    private int radius = 200;

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {
        if (radius > 0 && canvas != null) {
            canvas.drawCircle(x, y, radius + padding, paint);
        }
    }

    @Override
    public void updateTarget(Target target) {
        radius = getPreferredRadius(target.getBounds());
    }


    public static int getPreferredRadius(Rect bounds) {
        return Math.max(bounds.width(), bounds.height()) / 2;
    }
}
