/*
 * Copyright (C) 2013 Leszek Mzyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lostli.loopviewpager;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class LoopPagerAdapterWrapper extends PagerAdapter {

    private PagerAdapter mAdapter;

    /**
     * 单独缓存第一页和最后一页<br/>
     * 为了解决最后一页切换到第1页时有些动画闪烁的问题
     */
    private SparseArray<View> mFLItems = new SparseArray<>();

    public LoopPagerAdapterWrapper(PagerAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = (position - 1) % realCount;
        if (realPosition < 0)
            realPosition += realCount;

        return realPosition;
    }

    public int toInnerPosition(int realPosition) {
        int position = (realPosition + 1);
        return position;
    }

    private int getRealFirstPosition() {
        return 1;
    }

    private int getRealLastPosition() {
        return getRealFirstPosition() + getRealCount() - 1;
    }

    @Override
    public int getCount() {
        if (mAdapter.getCount() == 0) {
            return 0;
        }
        return mAdapter.getCount() + 2;
    }

    public int getRealCount() {
        return mAdapter.getCount();
    }

    public PagerAdapter getRealAdapter() {
        return mAdapter;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (getRealCount() == 0) {
            return null;
        }

        int realPosition = toRealPosition(position);

        View view = mFLItems.get(position);
        if (view != null) {
            mFLItems.remove(position);
            return view;
        }
        return mAdapter.instantiateItem(container, realPosition);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (getRealCount() == 0) {
            return;
        }
        int realFirst = getRealFirstPosition();
        int realLast = getRealLastPosition();
        int realPosition = toRealPosition(position);

        if (position == realFirst || position == realLast) {
            mFLItems.put(position, (View) object);
        } else {
            mAdapter.destroyItem(container, realPosition, object);
        }
    }


    @Override
    public void finishUpdate(ViewGroup container) {
        mAdapter.finishUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return mAdapter.isViewFromObject(view, object);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        mAdapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return mAdapter.saveState();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        mAdapter.startUpdate(container);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mAdapter.setPrimaryItem(container, position, object);
    }
}