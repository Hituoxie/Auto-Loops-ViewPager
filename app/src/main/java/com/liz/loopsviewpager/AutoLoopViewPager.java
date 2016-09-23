package com.liz.loopsviewpager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * @author li.zhen
 * @類說明 可以自动无限轮播的viewpager<br/>
 * 默认无限循环
 **/
public class AutoLoopViewPager extends ViewPager {
    private static final String TAG = "AutoLoopsViewPager";

    /**
     * 是否自动轮播
     */
    private boolean isAutoScroll = false;

    /**
     * 是否无限循环
     */
    private boolean isLoop = true;

    private static final int WHAT_SHOW_NEXT = 1;

    /**
     * 轮播间隔时间
     */
    private long interval = 3000;

    /**
     * 是否正在触摸
     */
    private boolean isTouching = false;

    private OnPageChangeListener mOuterPageChangeListener;

    private LoopPagerAdapter mAdapter;

    private Handler mHandler;

    private CustomDurationScroller scroller = null;

    public AutoLoopViewPager(Context context) {
        super(context);
        init();
    }

    public AutoLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        super.addOnPageChangeListener(onPageChangeListener);

        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);

            scroller = new CustomDurationScroller(getContext(), (Interpolator)interpolatorField.get(null));
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long downTime = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        //长按时拦截点击事件
        switch (MotionEventCompat.getActionMasked(arg0)) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_UP:
                if ((System.currentTimeMillis() - downTime) > 600) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //触摸时停止自动滚动
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                if (isAutoScroll) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    stopScrollToNext();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isAutoScroll) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    sendMessageScrollToNext();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isAutoScroll) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    sendMessageScrollToNext();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof LoopPagerAdapter)) {
            throw new IllegalArgumentException("adapter must be LoopPagerAdapter instance!");
        }

        mAdapter = (LoopPagerAdapter) adapter;
        mAdapter.setLoop(isLoop);
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);
    }


    /**
     * 设置是否无限循环
     *
     * @param isLoop
     */
    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.getRealPosition(super.getCurrentItem()) : 0;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = mAdapter.getInnerPosition(item);
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    private void scrollToNext() {
        setCurrentItem(getCurrentItem() + 1, true);
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

    /**
     * 开始滚动,自动支持无限循环
     *
     * @param interval 滚动间隔时间,默认3000ms
     */
    public void startAutoLoop(long interval) {
        this.interval = interval;

        isLoop = true;
        isAutoScroll = true;

        mAdapter.setLoop(isLoop);

        setCurrentItem(0, false);

        mAdapter.notifyDataSetChanged();

        //setPageTransformer(true, new ZoomOutPageTransformer());

        mHandler = new ScrollHandler(this);

        sendMessageScrollToNext();
    }

    /**
     * 开始无限循环，间隔时间,默认3000ms
     */
    public void startAutoLoop() {
        startAutoLoop(interval);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;

        //super.addOnPageChangeListener(mOuterPageChangeListener);
    }


    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {

            int realPosition = mAdapter.getRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            Log.d(TAG, "positionOffset=" + positionOffset + "==>positionOffsetPixels=" + positionOffsetPixels);

            int realPosition = position;

            if (isLoop) {
                if (mAdapter != null) {
                    realPosition = mAdapter.getRealPosition(position);

                    if (positionOffset == 0
                            && mPreviousOffset == 0
                            && (position == 0 || position == mAdapter.getCount() - 1)) {
                        setCurrentItem(realPosition, false);
                    }
                }
            }

            mPreviousOffset = positionOffset;
            if (mOuterPageChangeListener != null) {
                if (realPosition != mAdapter.getRealCount() - 1) {
                    mOuterPageChangeListener.onPageScrolled(realPosition,
                            positionOffset, positionOffsetPixels);
                } else {
                    if (positionOffset > .5) {
                        mOuterPageChangeListener.onPageScrolled(0, 0, 0);
                    } else {
                        mOuterPageChangeListener.onPageScrolled(realPosition,
                                0, 0);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mAdapter != null) {
                int position = AutoLoopViewPager.super.getCurrentItem();
                int realPosition = mAdapter.getRealPosition(position);

                if (isLoop) {
                    if (state == ViewPager.SCROLL_STATE_IDLE
                            && (position == 0 || position == mAdapter.getCount() - 1)) {
                        setCurrentItem(realPosition, false);
                    }
                }

            }
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };


    private class ScrollHandler extends Handler {
        private WeakReference<AutoLoopViewPager> pagerHolder;

        public ScrollHandler(AutoLoopViewPager pager) {
            pagerHolder = new WeakReference<>(pager);
        }

        public void handleMessage(android.os.Message msg) {

            if (WHAT_SHOW_NEXT == msg.what) {
                AutoLoopViewPager pager = pagerHolder.get();
                if (pager != null) {
                    scroller.setScrollDurationFactor(4.5f);
                    pager.scrollToNext();
                    scroller.setScrollDurationFactor(1f);
                    pager.sendMessageScrollToNext();
                }
            }

        }
    }
}
