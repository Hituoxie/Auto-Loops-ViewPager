package com.lostli.loopviewpager;

import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

/**
 * Created by Li.z on 2016/9/25.
 * 控制viewpager自动滚动
 */
public class AutoLoopControl {
    private static final int WHAT_SHOW_NEXT = 1;

    private Handler mHandler;
    private CustomDurationScroller scroller = null;
    private ViewPager mViewPager;

    /**
     * 轮播间隔时间
     */
    private long interval;

    public AutoLoopControl(ViewPager viewPager) {
        mViewPager = viewPager;
        initScroller();
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
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlerDispatchTouchEvent(MotionEvent ev) {
        //触摸时停止自动滚动
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                stopScrollToNext();
                break;
            case MotionEvent.ACTION_UP:
                sendMessageScrollToNext();
                break;
            case MotionEvent.ACTION_CANCEL:
                sendMessageScrollToNext();
                break;
            default:
                break;
        }
    }

    /**
     * 开始滚动
     * @param interval 间隔时间
     */
    public void startAutoLoop(long interval) {
        this.interval = interval;
        if(mHandler == null){
            mHandler = new ScrollHandler();
        }
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
