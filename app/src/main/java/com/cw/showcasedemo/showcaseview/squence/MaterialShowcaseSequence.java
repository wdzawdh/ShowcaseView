package com.cw.showcasedemo.showcaseview.squence;

import android.app.Activity;

import com.cw.showcasedemo.showcaseview.PrefsManager;
import com.cw.showcasedemo.showcaseview.ShowcaseView;

import java.util.LinkedList;
import java.util.Queue;


/**
 * ShowcaseView队列,当有多个ShowcaseView需要队列展示时使用
 */
public class MaterialShowcaseSequence implements DetachedListener {

    private Queue<ShowcaseView> mShowcaseQueue;
    private Activity mActivity;
    private boolean mSingleUse;
    private int mSequencePosition;
    private PrefsManager mPrefsManager;
    private OnSequenceItemShownListener mOnItemShownListener;
    private OnSequenceItemDismissedListener mOnItemDismissedListener;

    public MaterialShowcaseSequence(Activity activity) {
        mActivity = activity;
        mShowcaseQueue = new LinkedList<>();
    }

    public MaterialShowcaseSequence(Activity activity, String sequenceID) {
        this(activity);
        this.singleUse(sequenceID);
    }

    public MaterialShowcaseSequence singleUse(String sequenceID) {
        mSingleUse = true;
        mPrefsManager = new PrefsManager(mActivity, sequenceID);
        return this;
    }

    public MaterialShowcaseSequence addSequenceItem(ShowcaseView sequenceItem) {
        mShowcaseQueue.add(sequenceItem);
        return this;
    }

    public interface OnSequenceItemShownListener {
        void onShow(ShowcaseView itemView, int position);
    }

    public interface OnSequenceItemDismissedListener {
        void onDismiss(ShowcaseView itemView, int position);
    }

    public void setOnItemShownListener(OnSequenceItemShownListener listener) {
        this.mOnItemShownListener = listener;
    }

    public void setOnItemDismissedListener(OnSequenceItemDismissedListener listener) {
        this.mOnItemDismissedListener = listener;
    }

    public boolean hasFired() {
        if (mPrefsManager.getSequenceStatus() == PrefsManager.SEQUENCE_FINISHED) {
            return true;
        }
        return false;
    }

    public void start() {
        if (mSingleUse) {
            if (hasFired()) {
                return;
            }
            mSequencePosition = mPrefsManager.getSequenceStatus();

            if (mSequencePosition > 0) {
                for (int i = 0; i < mSequencePosition; i++) {
                    mShowcaseQueue.poll();
                }
            }
        }
        if (mShowcaseQueue.size() > 0)
            showNextItem();
    }

    @Override
    public void onShowcaseDetached(ShowcaseView showcaseView, boolean wasDismissed) {

        showcaseView.setDetachedListener(null);
        if (wasDismissed) {

            if (mOnItemDismissedListener != null) {
                mOnItemDismissedListener.onDismiss(showcaseView, mSequencePosition);
            }
            if (mPrefsManager != null) {
                mSequencePosition++;
                mPrefsManager.setSequenceStatus(mSequencePosition);
            }

            showNextItem();
        }
    }


    /**
     * 展示下一个ShowcaseView
     */
    private void showNextItem() {
        if (mShowcaseQueue.size() > 0 && !mActivity.isFinishing()) {
            ShowcaseView sequenceItem = mShowcaseQueue.remove();
            sequenceItem.setDetachedListener(this);
            sequenceItem.show(mActivity);
            if (mOnItemShownListener != null) {
                mOnItemShownListener.onShow(sequenceItem, mSequencePosition);
            }
        } else {
            if (mSingleUse) {
                mPrefsManager.setFired();
            }
        }
    }

}
