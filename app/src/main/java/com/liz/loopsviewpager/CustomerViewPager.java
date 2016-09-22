package com.liz.loopsviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li.zhen
 * @類說明  自定义ViewPager，可以自动循环滑动
 **/
public class CustomerViewPager extends ViewPager {
	private static final String TAG = "CustomerViewPager";



	private ViewPagerAdapter mAdapter;

	private OnItemClickListener mOnItemClickListener;

	private int currentPosition = 0;

	/**
	 * 是否正在触摸
	 */
	private boolean isTouching = false;

	private boolean isLoopering = false;

	/** 是否自动轮播  */
	private boolean isLooper = false;

	private final int WHAT_SHOW_NEXT = 1;

	private Handler myHandler = new Handler(Looper.getMainLooper()){

		public void handleMessage(android.os.Message msg) {

			if(WHAT_SHOW_NEXT == msg.what){
				if(!isLoopering){
					return;
				}
				//if(!isTouching){
				currentPosition = getCurrentItem()+1;
				setCurrentItem(currentPosition,true);
				//}
				showNextView();
			}

		};
	};


	public CustomerViewPager(Context context) {
		super(context);
		init();
	}

	public CustomerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {

	}

	public void startLoop(){
		stopLoop();
		isLooper = true;
		showNextView();
	}

	private void showNextView(){
		if(mAdapter.getCount()>2){
			isLoopering = true;
			myHandler.removeMessages(WHAT_SHOW_NEXT);
			myHandler.sendEmptyMessageDelayed(WHAT_SHOW_NEXT, 3000);
		}
	}

	public void stopLoop(){
		isLooper = false;
		stopShowNextView();
	}

	private void stopShowNextView(){
		isLoopering = false;
		myHandler.removeMessages(WHAT_SHOW_NEXT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private long downTime = 0;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		switch (MotionEventCompat.getActionMasked(arg0)) {
			case MotionEvent.ACTION_DOWN:
				downTime = System.currentTimeMillis();
				break;

			case MotionEvent.ACTION_UP:
				if((System.currentTimeMillis() - downTime)>600){
					return true;
				}
				break;
		}
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (MotionEventCompat.getActionMasked(ev)) {
			case MotionEvent.ACTION_DOWN:
				getParent().requestDisallowInterceptTouchEvent(true);
				isTouching = true;
				stopShowNextView();
				break;
			case MotionEvent.ACTION_UP:
				getParent().requestDisallowInterceptTouchEvent(false);
				isTouching = false;
				showNextView();
				break;
			case MotionEvent.ACTION_CANCEL:
				getParent().requestDisallowInterceptTouchEvent(false);
				isTouching = false;
				showNextView();
				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return super.onTouchEvent(arg0);
	}

	/**
	 * @Author: li.zhen
	 * @CreteDate: 2015年7月6日 下午3:43:37
	 * @Title:
	 * @Description: 设置Viewpager显示的Views
	 * @ModifiedBy:
	 * @param views
	 */
	public void showViews(List<View> views){
		/*if(views == null || views.size()==0){
			return;
		}

		mViews = views;
		if(mAdapter == null){
			mAdapter = new ViewPagerAdapter();
			setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
		//初始化第几页
		setCurrentItem(1, false);*/

	}

	int i = 0;
	public void setOnItemClickListener(OnItemClickListener listener){
		/*mOnItemClickListener = listener;
		for (int len = mViews.size() ; i < len; i++) {
			mViews.get(i).setTag(R.id.viewpager_position, i);
			mViews.get(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mOnItemClickListener!=null){
						mOnItemClickListener.onItemClick(v, (Integer) v.getTag(R.id.viewpager_position));
					}
				}
			});
		}*/
	}

	/*public void removeOnItemClickListener(){
		mOnItemClickListener = null;
		for (int i = 0,len = mViews.size() ; i < len; i++) {
			mViews.get(i).setOnClickListener(null);
		}
	}*/

	public abstract class ViewPagerAdapter<T> extends PagerAdapter
	{
		/**
		 * 缓存View
		 */
		private List<View> mViews = new ArrayList<View>();

		private Context mContext;
		private List<T> mData;
		private int mLayoutId;


		public ViewPagerAdapter(Context context, int layoutId, List<T> data){
			mContext = context;
			mLayoutId = layoutId;
			mData = data;

		}

		@Override
		public int getCount()
		{
			return Integer.MAX_VALUE/2;
		}

		@Override
		public boolean isViewFromObject(View view, Object object)
		{
			return view == object;
		}

		View tempView;
		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			tempView = getViewFromCache(position);
			container.addView(tempView);
			return tempView;
		}
		/**
		 * 适配器移除container容器中的视图
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((View) object);
			mViews.add((View) object);
		}

		private View getViewFromCache(int position){
			View view = null;
			if(mViews.size() == 0){
				view = View.inflate(mContext,mLayoutId,null);
			}else{
				view = mViews.remove(0);
			}

			bindView(view,mData.get(position),position);

			return view;
		}

		public abstract void bindView(View view, T data, int position);
	}

	public int getRealPosition(int position){
		/*if(mViews.size()>1){
			return position-1;
		}*/
		return position;
	}


	public interface OnItemClickListener{
		void onItemClick(View view, int position);
	}

	public boolean isLoopering() {
		return isLoopering;
	}

	public int getCount(){
		/*if(mViews!=null){
			return mViews.size();
		}*/
		return 0;
	}




}
