package com.lostli;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lostli.indicator.CircleIndicator;
import com.lostli.loopviewpager.LoopPagerAdapter;
import com.lostli.loopviewpager.LoopViewPager;
import com.lostli.loopviewpager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LoopViewPager loopViewPager;
    private CircleIndicator indicator;

    private static final String TAG = "MainActivity";

    private String[] img1 = new String[]{"http://img5.imgtn.bdimg.com/it/u=1086421488,1409978417&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3844331837,3157143639&fm=21&gp=0.jpg"};

    private String[] img2 = new String[]{"http://img3.imgtn.bdimg.com/it/u=536877195,2546372538&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=748730780,762499706&fm=21&gp=0.jpg",
            "http://pic.35pic.com/10/60/48/55b1OOOPIC31.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1086421488,1409978417&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3844331837,3157143639&fm=21&gp=0.jpg"};

    private String[] img3 = new String[]{"http://img4.imgtn.bdimg.com/it/u=3844331837,3157143639&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1086421488,1409978417&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=748730780,762499706&fm=21&gp=0.jpg"};

    private String[] img4 = new String[]{
            "http://pic.35pic.com/10/60/48/55b1OOOPIC31.jpg"};

    private List<String> data1 = new ArrayList<>();
    private List<String> data2 = new ArrayList<>();
    private List<String> data3 = new ArrayList<>();
    private List<String> data4 = new ArrayList<>();

    LoopPagerAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        loadData();
    }

    private void loadData() {
        mAdapter = new MyAdapter(this,data1);
        loopViewPager.setIsLoop(false);
        loopViewPager.setIsVertical(true);
        loopViewPager.setAdapter(mAdapter);
        indicator.setViewPager(loopViewPager);
        loopViewPager.startAutoLoop();
    }

    private void initView() {
        loopViewPager = (LoopViewPager) findViewById(R.id.alvp_pager);
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
        for(String url:img4){
            data4.add(url);
        }

        ImageLoadProxy.initImageLoader(this);
    }

    Random r = new Random();

    public void change(View view){
        int i = r.nextInt(4);
        if(i == 1){
            mAdapter = new MyAdapter(this,data1);
            loopViewPager.setAdapter(mAdapter);
            indicator.setViewPager(loopViewPager);
        }else if(i == 2){
            mAdapter = new MyAdapter(this,data2);
            loopViewPager.setAdapter(mAdapter);
            indicator.setViewPager(loopViewPager);
        }else if(i == 3){
            mAdapter = new MyAdapter(this,data3);
            loopViewPager.setAdapter(mAdapter);
            indicator.setViewPager(loopViewPager);
        }else{
            mAdapter = new MyAdapter(this,data4);
            loopViewPager.setAdapter(mAdapter);
            indicator.setViewPager(loopViewPager);
        }
    }

    class MyAdapter extends LoopPagerAdapter<String>{

        public MyAdapter(Context context,List<String> data){
            super(context,R.layout.viewpager_image,data);
        }

        @Override
        public void bindView(View view, String data, final int position) {
            ImageLoadProxy.displayImageWithLoadingPicture(data,(ImageView)view);
            view.setTag(position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int)view.getTag();
                    Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,getData().get(position));
                }
            });
        }
    }

}
