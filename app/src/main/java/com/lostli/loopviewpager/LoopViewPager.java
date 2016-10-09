package com.lostli.loopviewpager;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author li.zhen
 * @類說明 无限轮播的viewpager,基于LoopingViewpager修改<br/>
 *         如果修改Data需要重新设置adapter
 **/
public class  LoopViewPager extends ViewPager {
    private AutoLoopControl mAutoLoopControl;
    private OnPageChangeListener mOuterPageChangeListener;
    private LoopPagerAdapterWrapper mAdapter;

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        super.addOnPageChangeListener(onPageChangeListener);
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

    float mDownX;
    float mDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //触摸时停止自动滚动
        if(mAutoLoopControl!=null){
            mAutoLoopControl.handlerDispatchTouchEvent(ev);
        }

        //解决父listview拦截问题
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if(scrollState == ViewPager.SCROLL_STATE_DRAGGING){
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                }
                if(Math.abs(ev.getX()-mDownX) > Math.abs(ev.getY()-mDownX)){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else{
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开始自动滚动，间隔时间,默认3000m或者上一次设置的时间
     */
    public void startAutoLoop() {
        if(mAutoLoopControl == null){
            mAutoLoopControl = new AutoLoopControl(this);
        }
        mAutoLoopControl.startAutoLoop();
    }

    /**
     * 开始自动滚动
     * @param interval 滚动间隔时间 ms
     */
    public void startAutoLoop(long interval) {
        if(mAutoLoopControl == null){
            mAutoLoopControl = new AutoLoopControl(this);
        }
        mAutoLoopControl.startAutoLoop(interval);
    }

    public void stopAutoLoop(){
        if(mAutoLoopControl != null){
            mAutoLoopControl.stopAutoLoop();
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof LoopPagerAdapter)) {
            throw new IllegalArgumentException("adapter must be LoopPagerAdapter instance!");
        }

        final LoopPagerAdapter loopPagerAdapter = (LoopPagerAdapter)adapter;

        mAdapter = new LoopPagerAdapterWrapper(loopPagerAdapter);

        super.setAdapter(mAdapter);
        //有些系统不notify一下会报错
        mAdapter.notifyDataSetChanged();

        setCurrentItem(0, false);

        //重置滚动时间
        if(mAutoLoopControl !=null && mAutoLoopControl.isAutoLoop()){
            mAutoLoopControl.startAutoLoop();
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = mAdapter.toInnerPosition(item);
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
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



    int scrollState;

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {

            Log.d("viewpager","onPageSelected:"+position);

            int realPosition = mAdapter.toRealPosition(position);
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
            int realPosition = position;
            if (mAdapter != null) {
                realPosition = mAdapter.toRealPosition(position);

                if (positionOffset == 0
                        && mPreviousOffset == 0
                        && (position == 0 || position == mAdapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
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
            scrollState = state;
            if (mAdapter != null) {
                int position = LoopViewPager.super.getCurrentItem();
                int realPosition = mAdapter.toRealPosition(position);

                if (state == ViewPager.SCROLL_STATE_IDLE
                        && (position == 0 || position == mAdapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };
}
