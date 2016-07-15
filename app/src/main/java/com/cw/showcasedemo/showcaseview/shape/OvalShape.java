/**
 *   @function:$
 *   @description: $
 *   @param:$
 *   @return:$
 *   @history:
 * 1.date:$ $
 *           author:$
 *           modification:
 */

package com.cw.showcasedemo.showcaseview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.cw.showcasedemo.showcaseview.target.Target;
import com.cw.showcasedemo.showcaseview.target.ViewTarget;

/**
 * @author Cw
 * @date 16/7/15
 */
public class OvalShape implements Shape {

    private Rect mRect;

    public OvalShape(ViewTarget aTarget) {
        mRect = aTarget.getBounds();
    }

    @Override
    public void draw(Canvas canvas, Paint paint, int x, int y, int padding) {
        RectF rectF = new RectF(mRect.left - padding
                , mRect.top - padding
                , mRect.right + padding
                , mRect.bottom + padding);
        canvas.drawOval(rectF, paint);
    }


    @Override
    public void updateTarget(Target target) {
        mRect = target.getBounds();

    }
}