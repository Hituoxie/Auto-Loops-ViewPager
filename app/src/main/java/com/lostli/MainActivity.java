package com.lostli;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lostli.loopviewpager.CircleIndicator;
import com.lostli.loopviewpager.LoopPagerAdapter;
import com.lostli.loopviewpager.LoopViewPager;
import com.lostli.loopviewpager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LoopViewPager alvpPager;
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
        mAdapter = new LoopPagerAdapter<String>(this,R.layout.viewpager_image,data1){

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
        };
        alvpPager.setAdapter(mAdapter);

        indicator.setViewPager(alvpPager);
        mAdapter.registerDataSetObserver(indicator.getDataSetObserver());
        alvpPager.startAutoLoop();
    }


    private void initView() {
        alvpPager = (LoopViewPager) findViewById(R.id.alvp_pager);
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

    Random r = new Random();

    public void change(View view){
        int i = r.nextInt(3);
        if(i == 1){
            mAdapter.setData(data1);
        }else if(i == 2){
            mAdapter.setData(data2);
        }else{
            mAdapter.setData(data3);
        }
    }

}
