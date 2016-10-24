package com.lostli.loopviewpager;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author li.zhen
 * @類說明 无限轮播的viewpager, 基于LoopingViewpager修改<br/>
 * 如果修改Data需要重新设置adapter
 **/
public class LoopViewPager extends ViewPager {
    private AutoLoopControl mAutoLoopControl;
    private OnPageChangeListener mOuterPageChangeListener;
    private LoopPagerAdapterWrapper mLoopAdapter;

    private LoopPagerAdapter mNoLoopAdapter;

    private MyOnPageChangeListener onPageChangeListener;

    /**
     * 是否无限循环
     */
    private boolean isLoop = true;

    /**
     * 是否垂直
     */
    private boolean isVertical = false;

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        //super.addOnPageChangeListener(onPageChangeListener);
        mAutoLoopControl = new AutoLoopControl(this);
    }

    protected void initVertical() {
        if(isVertical){
            // The majority of the magic happens here
            setPageTransformer(true, new VerticalPageTransformer());
            // The easiest way to get rid of the overscroll drawing that happens on the left and right
            setOverScrollMode(OVER_SCROLL_NEVER);
        }
    }



    float mDownX;
    float mDownY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //触摸时停止自动滚动
        mAutoLoopControl.handlerDispatchTouchEvent(ev);

        //解决父listview拦截问题
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (scrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                }
                if (Math.abs(ev.getX() - mDownX) > Math.abs(ev.getY() - mDownX)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
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
        if(isLoop){
            mAutoLoopControl.startAutoLoop();
        }

        Log.d("viewpager", "startAutoLoop");
    }

    /**
     * 开始自动滚动
     *
     * @param interval 滚动间隔时间 ms
     */
    public void startAutoLoop(long interval) {
        if(isLoop){
            mAutoLoopControl.startAutoLoop(interval);
        }
    }

    public void stopAutoLoop() {
        if(isLoop){
            mAutoLoopControl.stopAutoLoop();
        }
    }

    /**设置是否无限滑动，在setAdapter之前调用
     * @param isLoop
     */
    public void setIsLoop(boolean isLoop){
        this.isLoop = isLoop;
    }

    /**设置是否垂直，在setAdapter之前调用
     * @param isVertical
     */
    public void setIsVertical(boolean isVertical){
        this.isVertical = isVertical;
        initVertical();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof LoopPagerAdapter)) {
            throw new IllegalArgumentException("adapter must be LoopPagerAdapter instance!");
        }

        final LoopPagerAdapter loopPagerAdapter = (LoopPagerAdapter) adapter;

        if(isLoop){
            if(onPageChangeListener == null){
                onPageChangeListener = new MyOnPageChangeListener();
                super.addOnPageChangeListener(onPageChangeListener);
            }

            mLoopAdapter = new LoopPagerAdapterWrapper(loopPagerAdapter);
            super.setAdapter(mLoopAdapter);
            mLoopAdapter.notifyDataSetChanged();
            //重置滚动时间
            if (mAutoLoopControl.isAutoLoop()) {
                mAutoLoopControl.startAutoLoop();
            }
        }else{
            mNoLoopAdapter = loopPagerAdapter;
            super.setAdapter(mNoLoopAdapter);
            mNoLoopAdapter.notifyDataSetChanged();
        }

        setCurrentItem(0, false);
    }

    @Override
    public PagerAdapter getAdapter() {
        if(isLoop){
            return mLoopAdapter != null ? mLoopAdapter.getRealAdapter() : mLoopAdapter;
        }else{
            return mNoLoopAdapter;
        }

    }

    @Override
    public int getCurrentItem() {
        if(isLoop){
            return mLoopAdapter != null ? mLoopAdapter.toRealPosition(super.getCurrentItem()) : 0;
        }else{
            return super.getCurrentItem();
        }

    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        if(isLoop){
            int realItem = mLoopAdapter.toInnerPosition(item);
            super.setCurrentItem(realItem, smoothScroll);
        }else{
            super.setCurrentItem(item, smoothScroll);
        }
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        if(isLoop){
            mOuterPageChangeListener = listener;
        }else{
            super.setOnPageChangeListener(listener);
        }

    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if(isLoop){
            mOuterPageChangeListener = listener;
        }else{
            super.addOnPageChangeListener(listener);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(isLoop){
            if (visibility == View.VISIBLE) {
                mAutoLoopControl.resumeAutoLoop();
                awakenScrollBars();
            } else if (visibility == INVISIBLE || visibility == GONE) {
                mAutoLoopControl.pauseAutoLoop();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if(isLoop){
            super.onVisibilityChanged(changedView, visibility);
            if (visibility == View.VISIBLE) {
                mAutoLoopControl.resumeAutoLoop();
            } else if (visibility == INVISIBLE || visibility == GONE) {
                mAutoLoopControl.pauseAutoLoop();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        //super.onAttachedToWindow();
        Log.d("viewpager","onAttachedToWindow");
    }

    int scrollState;
    private class MyOnPageChangeListener implements OnPageChangeListener{
        private float mPreviousOffset = -1;
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {

            int realPosition = mLoopAdapter.toRealPosition(position);
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
            if (mLoopAdapter != null) {
                realPosition = mLoopAdapter.toRealPosition(position);

                if (positionOffset == 0
                        && mPreviousOffset == 0
                        && (position == 0 || position == mLoopAdapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }

            mPreviousOffset = positionOffset;
            if (mOuterPageChangeListener != null) {
                if (realPosition != mLoopAdapter.getRealCount() - 1) {
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
            if (mLoopAdapter != null) {
                int position = LoopViewPager.super.getCurrentItem();
                int realPosition = mLoopAdapter.toRealPosition(position);

                if (state == ViewPager.SCROLL_STATE_IDLE
                        && (position == 0 || position == mLoopAdapter.getCount() - 1)) {
                    setCurrentItem(realPosition, false);
                }
            }
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

    private class VerticalPageTransformer implements PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);

                //set Y position to swipe in from top
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if(isVertical){
            boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
            swapXY(ev); // return touch coordinates to original reference frame for any child views
            return intercepted;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isVertical){
            return super.onTouchEvent(swapXY(ev));
        }else{
            return super.onTouchEvent(ev);
        }
    }
}
