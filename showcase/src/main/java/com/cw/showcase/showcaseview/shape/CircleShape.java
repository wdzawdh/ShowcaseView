package com.cw.showcase.showcaseview.shape;


import android.graphics.Canvas;
import android.graphics.Paint;

import com.cw.showcase.showcaseview.target.ViewTarget;


public class CircleShape implements IShape {

    @Override
    public void draw(Canvas canvas, Paint paint, ViewTarget target, int padding) {
        if (canvas != null && paint != null && target != null) {
            int radius = Math.max(target.getBounds().width(), target.getBounds().height()) / 2;
            canvas.drawCircle(target.getPoint().x, target.getPoint().y, radius + padding, paint);
        }
    }
}
