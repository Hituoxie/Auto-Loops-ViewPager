package com.lostli.loopviewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Li.z on 2016/9/26.
 */

public class AutoLoopViewPager extends LoopViewPager{
    private AutoLoopControl mAutoLoopControl;

    private static final long DEFAULT_INTERVAL = 3000;

    public AutoLoopViewPager(Context context) {
        super(context);
    }

    public AutoLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //触摸时停止自动滚动
        mAutoLoopControl.handlerDispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开始无限循环，间隔时间,默认3000ms
     */
    public void startAutoLoop() {
        startAutoLoop(DEFAULT_INTERVAL);
    }

    /**
     * 开始自动滚动
     * @param interval 滚动间隔时间
     */
    public void startAutoLoop(long interval) {
        if(mAutoLoopControl == null){
            mAutoLoopControl = new AutoLoopControl(this);
        }
        mAutoLoopControl.startAutoLoop(interval);
    }

}
