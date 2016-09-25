package com.liz.loopsviewpager;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by Li.z on 2016/9/25.
 */
public class AutoLoopControl {
    private static final int WHAT_SHOW_NEXT = 1;
    private Handler mHandler;

    private CustomDurationScroller scroller = null;

    private ViewPager mViewPager;

    /**
     * 轮播间隔时间
     */
    private long interval = 3000;

    public AutoLoopControl(ViewPager viewPager) {
        mViewPager = viewPager;
        initScroller();
    }

    private void initScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            scroller = new CustomDurationScroller(mViewPager.getContext(), (Interpolator) interpolatorField.get(null));
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始滚动,自动支持无限循环
     *
     * @param interval 滚动间隔时间,默认3000ms
     */
    public void startAutoLoop(long interval) {
        this.interval = interval;

        mHandler = new ScrollHandler();

        sendMessageScrollToNext();
    }


    private void scrollToNext() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    private void stopScrollToNext() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_SHOW_NEXT);
        }

    }

    private void sendMessageScrollToNext() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_SHOW_NEXT);
            mHandler.sendEmptyMessageDelayed(WHAT_SHOW_NEXT, interval);
        }
    }

    private class ScrollHandler extends Handler {

        public ScrollHandler() {
        }

        public void handleMessage(android.os.Message msg) {

            if (WHAT_SHOW_NEXT == msg.what) {
                scroller.setScrollDurationFactor(4.5f);
                scrollToNext();
                scroller.setScrollDurationFactor(1f);
                sendMessageScrollToNext();
            }

        }
    }
}
