package com.cw.showcase.showcaseview.animation;

import android.view.View;


public interface IAnimationFactory {

    void fadeInView(View target, long duration, AnimationStartListener listener);

    void fadeOutView(View target, long duration, AnimationEndListener listener);

    interface AnimationStartListener {
        void onAnimationStart();
    }

    interface AnimationEndListener {
        void onAnimationEnd();
    }
}

