package com.cw.showcasedemo.showcaseview.shape;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.cw.showcasedemo.showcaseview.target.Target;


public class RectangleShape implements Shape {

    private static boolean fullWidth = false;

    private Rect rect;

    public static void setFullWidth(boolean fullWidth) {
        RectangleShape.fullWidth = fullWidth;
    }

    //    public RectangleShape(int width, int height) {
    //        this.width = width;
    //        this.height = height;
    //        init();
    //    }
    //
    //    public RectangleShape(Rect bounds) {
    //        this(bounds, false);
    //    }
    //
    //    public RectangleShape(Rect bounds, boolean fullWidth) {
    //        this.fullWidth = fullWidth;
    //        height = bounds.height();
    //        if (fullWidth)
    //            width = Integer.MAX_VALUE;
    //        else
    //            width = bounds.width();
    //        init();
    //    }
    //
    //    public RectangleShape() {
    //    }
    //
    //    private void init() {
    //        rect = new Rect(-width / 2, -height / 2, width / 2, height / 2);
    //    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {
        if (!rect.isEmpty()) {
            canvas.drawRect(
                    rect.left + x - padding,
                    rect.top + y - padding,
                    rect.right + x + padding,
                    rect.bottom + y + padding,
                    paint
            );
        }
    }

    @Override
    public void updateTarget(Target target) {
        Rect bounds = target.getBounds();
        int height = bounds.height();
        int width;
        if (fullWidth)
            width = Integer.MAX_VALUE;
        else
            width = bounds.width();
        rect = new Rect(-width / 2, -height / 2, width / 2, height / 2);
    }

}