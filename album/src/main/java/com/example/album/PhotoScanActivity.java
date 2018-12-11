package com.example.album;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.album.adapter.PhotoPagerAdapter;

import java.util.ArrayList;

public class PhotoScanActivity extends Activity {

    private ArrayList<String> paths;
    private ViewPager mViewPager;
    private PhotoPagerAdapter mPagerAdapter;

    private int currentItem = 0;

    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivDownLoad;


    @Override
    public int layoutId() {
        return R.layout.activity_photo_scan_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        paths = new ArrayList<>();
        Intent bundle = getIntent();
        if (bundle != null) {
            ArrayList<String> pathArr = bundle.getStringArrayListExtra(PhotoScanConfig.EXTRA_PHOTO_LIST);
            paths.clear();
            if (pathArr != null) {
                paths.addAll(pathArr);
            }
            currentItem = bundle.getIntExtra(PhotoScanConfig.EXTRA_CURRENT_POSITION, PhotoScanConfig.DEFAULT_POSITION);
        }

        ivBack = findViewById(R.id.iv_close);
        tvTitle = findViewById(R.id.tv_title);
        ivDownLoad = findViewById(R.id.iv_download);

        mViewPager = findViewById(R.id.vp_photos);
        mPagerAdapter = new PhotoPagerAdapter(Glide.with(this), paths);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        updateTitle();
        checkDownLoadVisiable();
    }

    @Override
    public void initData() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                updateTitle();
                checkDownLoadVisiable();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPagerAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               savePicture(PhotoScanActivity.this,paths.get(currentItem));
            }
        });
    }

    private void checkDownLoadVisiable() {
        if(paths.get(currentItem).startsWith("http")) {
            ivDownLoad.setVisibility(View.VISIBLE);
        } else {
            ivDownLoad.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTitle() {
        tvTitle.setText(getString(R.string.__picker_preview_title, currentItem + 1, paths.size()));
    }
}
