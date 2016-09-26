package com.liz.loopsviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
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

    private Context mContext;
    private List<T> mData;
    private int mLayoutId;

    public LoopPagerAdapter(Context context, int layoutId, List<T> data){

        if(data == null){
            throw new IllegalArgumentException("data can't be null!");
        }

        mContext = context;
        mLayoutId = layoutId;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;

        if(mViews.size() > 1){
            view = mViews.removeFirst();
        }else{
            view = View.inflate(mContext,mLayoutId,null);
        }

        bindView(view,mData.get(position),position);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.addLast((View) object);
    }

    abstract public void bindView(View view, T data, int position);

}
