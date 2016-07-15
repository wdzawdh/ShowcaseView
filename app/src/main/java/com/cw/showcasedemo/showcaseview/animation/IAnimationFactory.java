package com.cw.showcasedemo.showcaseview.animation;

import android.graphics.Point;
import android.view.View;

import com.cw.showcasedemo.showcaseview.ShowcaseView;


public interface IAnimationFactory {

    void fadeInView(View target, long duration, AnimationStartListener listener);

    void fadeOutView(View target, long duration, AnimationEndListener listener);

    void animateTargetToPoint(ShowcaseView showcaseView, Point point);

    interface AnimationStartListener {
        void onAnimationStart();
    }

    interface AnimationEndListener {
        void onAnimationEnd();
    }
}

