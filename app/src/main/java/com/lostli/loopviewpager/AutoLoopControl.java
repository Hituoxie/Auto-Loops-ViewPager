package com.lostli.loopviewpager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Li.z on 2016/9/25.
 * 控制viewpager自动滚动
 */
public class AutoLoopControl {
    private static final int WHAT_SHOW_NEXT = 1;

    private Handler mHandler;

    private ViewPager mViewPager;

    private CustomDurationScroller scroller = null;

    private static final long DEFAULT_INTERVAL = 3000;
    private long mInterval = DEFAULT_INTERVAL;

    /**
     * 是否自动滚动
     */
    private boolean isAutoLoop = false;

    public AutoLoopControl(ViewPager viewPager) {
        mViewPager = viewPager;
        initScroller();
        mHandler = new ScrollHandler();
    }

    /**
     * 利用反射修改viewpager默认滚动速度，让切换更平滑
     */
    private void initScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            scroller = new CustomDurationScroller(mViewPager.getContext(), (Interpolator) interpolatorField.get(null));
            scrollerField.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlerDispatchTouchEvent(MotionEvent ev) {
        //触摸时停止自动滚动
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoLoop();
                break;
            case MotionEvent.ACTION_UP:
                resumeAutoLoop();
                break;
            case MotionEvent.ACTION_CANCEL:
                resumeAutoLoop();
                break;
            default:
                break;
        }
    }

    /**
     * 开始滚动
     *
     * @param interval 间隔时间
     */
    public void startAutoLoop(long interval) {
        mInterval = interval;
        isAutoLoop = true;
        sendMessageScrollToNext();
    }

    public void startAutoLoop() {
        startAutoLoop(mInterval);
    }

    public void pauseAutoLoop() {
        stopScrollToNext();
    }

    public void resumeAutoLoop() {
        if (isAutoLoop) {
            sendMessageScrollToNext();
        }
    }

    public void stopAutoLoop() {
        isAutoLoop = false;
        stopScrollToNext();
    }

    public boolean isAutoLoop() {
        return isAutoLoop;
    }

    private void scrollToNext() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

    private void stopScrollToNext() {
        mHandler.removeMessages(WHAT_SHOW_NEXT);
    }

    private void sendMessageScrollToNext() {
        mHandler.removeMessages(WHAT_SHOW_NEXT);
        mHandler.sendEmptyMessageDelayed(WHAT_SHOW_NEXT, mInterval);
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

    /**
     * @author Li.z
     * @類說明
     **/
    public class CustomDurationScroller extends Scroller {

        private double scrollFactor = 1;

        public CustomDurationScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        /**
         * Set the factor by which the duration will change
         */
        public void setScrollDurationFactor(double scrollFactor) {
            this.scrollFactor = scrollFactor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) (duration * scrollFactor));
        }
    }
}
