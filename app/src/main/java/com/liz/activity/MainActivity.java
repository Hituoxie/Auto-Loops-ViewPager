package com.liz.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.imbryk.viewPager.LoopViewPager;
import com.liz.loopsviewpager.CircleIndicator;
import com.liz.loopsviewpager.LoopPagerAdapter;
import com.liz.loopsviewpager.AutoLoopViewPager;
import com.liz.loopsviewpager.R;
import com.utils.ImageLoadProxy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LoopViewPager lvpPager;

    private AutoLoopViewPager alvpPager;
    private CircleIndicator indicator;

    private static final String TAG = "MainActivity";

    private String[] img1 = new String[]{"https://cache.hinabian.com/images/release/0/d/02a25e28edcd90154c457939e973c95d.jpg"};

    private String[] img2 = new String[]{"https://cache.hinabian.com/images/release/0/d/02a25e28edcd90154c457939e973c95d.jpg",
            "https://cache.hinabian.com/images/release/1/2/1983130c55d9d36aed35e631037b4e52.jpg",
            "https://cache.hinabian.com/images/release/2/5/2c4a346109468371ce8ebaaf7a157535.jpg",
            "https://cache.hinabian.com/images/release/d/5/d7e438b6c52284a1adbf2f92f432a225.jpg",
            "https://cache.hinabian.com/images/release/2/c/2e6c24d41c64294f04bc8828f607afdc.jpg",
            "https://cache.hinabian.com/images/release/f/6/f3f423b25d79297dc1b48585ad6ad8f6.jpg"};

    private String[] img3 = new String[]{"https://cache.hinabian.com/images/release/0/d/02a25e28edcd90154c457939e973c95d.jpg",
            "https://cache.hinabian.com/images/release/1/2/1983130c55d9d36aed35e631037b4e52.jpg",
            "https://cache.hinabian.com/images/release/2/5/2c4a346109468371ce8ebaaf7a157535.jpg"};

    private List<String> data1 = new ArrayList<>();
    private List<String> data2 = new ArrayList<>();
    private List<String> data3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        loadData();
    }

    private void loadData() {
        lvpPager.setAdapter(new MyAdapter(data1));

        alvpPager.setLoop(false);

        alvpPager.setAdapter(new LoopPagerAdapter<String>(this,R.layout.viewpager_image,data3){

            @Override
            public void bindView(View view, String data, final int position) {
                ImageLoadProxy.displayImageWithLoadingPicture(data,(ImageView)view,android.R.drawable.ic_menu_report_image);
                view.setTag(position);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this,view.getTag().toString(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        indicator.setViewPager(alvpPager);

        alvpPager.startAutoLoop();



    }


    private void initView() {
        lvpPager = (LoopViewPager)findViewById(R.id.lvp_pager);
        alvpPager = (AutoLoopViewPager) findViewById(R.id.alvp_pager);
        indicator = (CircleIndicator)findViewById(R.id.indicator);

    }

    private void initData() {
        for(String url:img1){
            data1.add(url);
        }

        for(String url:img2){
            data2.add(url);
        }
        for(String url:img3){
            data3.add(url);
        }


        ImageLoadProxy.initImageLoader(this);
    }

    class MyAdapter extends PagerAdapter{

        List<String> data;
        List<View> views = new ArrayList<>();

        public MyAdapter(List<String> images){
            data = images;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e(TAG,"instantiateItem:"+position);
            ImageView iv;
            if(views.size() == 0){
                Log.e(TAG,"instantiateItem:new");
                iv = new ImageView(MainActivity.this);
                iv.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }else{
                Log.e(TAG,"instantiateItem:old");
                iv = (ImageView) views.remove(0);
            }
            ImageLoadProxy.displayImageWithLoadingPicture(data.get(position),iv,android.R.drawable.ic_menu_report_image);
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e(TAG,"destroyItem:"+position);
            container.removeView((View) object);
            views.add((View) object);
        }
    }
}
