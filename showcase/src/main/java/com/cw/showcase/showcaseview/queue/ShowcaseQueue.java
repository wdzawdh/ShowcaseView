package com.cw.showcase.showcaseview.queue;


import com.cw.showcase.showcaseview.ShowcaseView;

import java.util.LinkedList;

/**
 * @author Cw
 * @date 2017/7/26
 */
public class ShowcaseQueue {

    private static ShowcaseQueue sQueue;

    private LinkedList<ShowcaseView> mList = new LinkedList<>();

    public static ShowcaseQueue getInstance() {
        if (sQueue == null) {
            synchronized (ShowcaseQueue.class) {
                if (sQueue == null) {
                    sQueue = new ShowcaseQueue();
                }
            }
        }
        return sQueue;
    }

    public boolean add(ShowcaseView showcaseView) {
        if (showcaseView == null) {
            throw new IllegalArgumentException("showcaseView == null");
        }
        return mList.add(showcaseView);
    }

    public int getSize() {
        return mList.size();
    }

    public synchronized void showQueue() {
        if (mList.size() == 0) {
            return;
        }
        ShowcaseView showcaseView = mList.getFirst();
        mList.remove(showcaseView);
        showcaseView.addQueueListener(new ShowcaseView.QueueListener() {
            @Override
            public void onDismiss() {
                showQueue();
            }
        });
        showcaseView.show();
    }
}
