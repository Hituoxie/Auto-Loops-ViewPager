package com.liz.loopsviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * @author li.zhen
 * @類說明
 **/
public abstract class LoopPagerAdapter<T> extends PagerAdapter{

    /**
     * 缓存View
     */
    private LinkedList<Object> mViews = new LinkedList<>();

    private SparseArray<Object> mViewItems = new SparseArray<>();

    private Context mContext;
    private List<T> mData;
    private int mLayoutId;

    /**
     * 是否无限滚动
     */
    private boolean isLoop = true;

    public LoopPagerAdapter(Context context, int layoutId, List<T> data){

        if(data == null){
            throw new IllegalArgumentException("data can't be null!");
        }

        mContext = context;
        mLayoutId = layoutId;
        mData = data;
    }

    public void setLoop(boolean isLoop){
        this.isLoop = isLoop;
    }


    @Override
    public int getCount() {
        if(isLoop){
            return mData.size()+2;
        }else{
            return mData.size();
        }
    }

    public int getRealCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    int getRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = (position-1) % realCount;
        if (realPosition < 0)
            realPosition += realCount;

        return realPosition;
    }

    public int getInnerPosition(int realPosition) {
        if(isLoop){
            return realPosition + 1;
        }else{
            return realPosition;
        }
    }

    private int getRealFirstPosition() {
        return 1;
    }

    private int getRealLastPosition() {
        return getRealFirstPosition() + getRealCount() - 1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition =  isLoop ? getRealPosition(position) : position;

        View view = getViewFromCache(container,position);

        bindView(view,mData.get(realPosition),realPosition);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        //mViews.addLast(object);
        //mViewItems.put(position,object);
    }

    private View getViewFromCache(ViewGroup container,int position){
        View view = null;

        /*if(mViews.size() > 1){
            view = (View) mViews.removeFirst();
        }*/

        view = (View)mViewItems.get(position);

        if(view == null){
            view = View.inflate(mContext,mLayoutId,null);
            mViewItems.put(position,view);
        }

        return view;
    }

    abstract public void bindView(View view, T data, int position);

}
