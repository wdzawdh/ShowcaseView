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

package com.cw.showcase.showcaseview.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.cw.showcase.showcaseview.target.ViewTarget;


/**
 * @author Cw
 * @date 16/7/15
 */
public class OvalShape implements IShape {

    @Override
    public void draw(Canvas canvas, Paint paint, ViewTarget target, int padding) {
        RectF rectF = new RectF(target.getBounds().left - padding
                , target.getBounds().top - padding
                , target.getBounds().right + padding
                , target.getBounds().bottom + padding);
        canvas.drawOval(rectF, paint);
    }
}