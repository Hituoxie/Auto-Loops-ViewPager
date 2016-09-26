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
     * 缓存View，当list的数据很多时也不会内存溢出
     */
    private LinkedList<View> mViews = new LinkedList<>();

    /**
     * 单独缓存第一页和最后一页<br/>
     * 为了解决最后一页切换到第1页时有些动画闪烁的问题
     */
    private SparseArray<View> mFLItems = new SparseArray<>();

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
        int realFirst = getRealFirstPosition();
        int realLast = getRealLastPosition();

        View view;

        if (position == realFirst || position == realLast) {
            view = getFristOrLastView(position);
            if(view.getParent() == null){
                container.addView(view);
                bindView(view,mData.get(realPosition),realPosition);
            }
        } else {
            view = getViewFromCache();
            container.addView(view);
            bindView(view,mData.get(realPosition),realPosition);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int realFirst = getRealFirstPosition();
        int realLast = getRealLastPosition();

        if (position != realFirst && position != realLast) {
            container.removeView((View) object);
            mViews.addLast((View) object);
        }
    }

    private View getFristOrLastView(int position){
        View view = mFLItems.get(position);

        if(view == null){
            view = View.inflate(mContext,mLayoutId,null);
            mFLItems.put(position,view);
        }

        return view;
    }

    private View getViewFromCache(){
        View view;

        if(mViews.size() > 1){
            view = mViews.removeFirst();
        }else{
            view = View.inflate(mContext,mLayoutId,null);
        }

        return view;

    }

    abstract public void bindView(View view, T data, int position);

}
