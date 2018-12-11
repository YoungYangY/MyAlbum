package com.example.album.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;


import com.example.album.R;
import com.example.album.glideimageview.GlideImageLoader;
import com.example.album.recycleview.adapter.CommonRecyclerViewAdapter;
import com.example.album.recycleview.adapter.viewholder.ViewRecycleHolder;

import java.util.ArrayList;
import java.util.List;


public class PhotoSelectedAdapter extends CommonRecyclerViewAdapter<String> {
    private int maxCount = 1;
    private Activity mContext;
    private final String PHOTO_ICON = "photo";
    private boolean PHOTO_ICON_NOT_VISIBLE;

    private void addPhotoIcon(){
        if (mDatas != null && !mDatas.contains(PHOTO_ICON)&&!PHOTO_ICON_NOT_VISIBLE) {
                mDatas.add(PHOTO_ICON);
        }
    }

    public PhotoSelectedAdapter(Activity context, int maxCount) {
        super(context, R.layout.item_photoss_selected);
        this.maxCount = maxCount;
        mContext = context;
        addPhotoIcon();
    }

    public PhotoSelectedAdapter(Activity context, int maxCount, boolean photo_icon_visible) {
        super(context, R.layout.item_photoss_selected);
        this.maxCount = maxCount;
        this.PHOTO_ICON_NOT_VISIBLE = photo_icon_visible;
        mContext = context;
        addPhotoIcon();
    }

    @Override
    public void setPhotoSelectedData(List<String> list) {
        if (list != null) {
            mDatas = list;
        }
        if (!PHOTO_ICON_NOT_VISIBLE)
            mDatas.add(PHOTO_ICON);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDatas.size() < maxCount) {
            return mDatas.size();
        }
        return maxCount;
    }

    @Override
    protected void convert(ViewRecycleHolder holder, final String s, final int position) {
        ImageView ivPhoto = holder.getView(R.id.iv_photo);
        ImageView ivDelete = holder.getView(R.id.iv_delete);

        if (!s.equals(PHOTO_ICON)) {
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivDelete.setVisibility(View.VISIBLE);
            ivPhoto.setBackgroundResource(R.color.transparent);
                GlideImageLoader.create(ivPhoto).loadImage(s, R.drawable.album__picker_ic_photo_black_48dp);

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(position);
                    if(delImage!=null){
                        delImage.delImage(position);
                    }
                    notifyDataSetChanged();
                }
            });

            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoScanConfig.builder()
                            .setPhotoList((ArrayList<String>) getDatas())
                            .setCurPosition(position)
                            .start(mContext);
                }
            });

        } else if (!PHOTO_ICON_NOT_VISIBLE) {
            ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            ivDelete.setVisibility(View.GONE);
            ivPhoto.setImageResource(R.drawable.album_icon_camera);
            ivPhoto.setBackgroundResource(R.color.service_cl_F6F6F6);

            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoPickerConfig.builder()
                            .setShowCamera(true)
                            .setPreviewEnabled(true)
                            .setGridColumnCount(4)
                            .setSelected((ArrayList<String>) getDatas())
                            .setPhotoCount(maxCount)
                            .start(mContext);
                }
            });

        }
    }


    @Override
    public List<String> getDatas() {
        ArrayList<String> list = new ArrayList<>();
        if (!PHOTO_ICON_NOT_VISIBLE) {
            if (mDatas != null && mDatas.size() > 0) {
                for (int i = 0; i < mDatas.size() - 1; i++) {
                    list.add(mDatas.get(i));
                }
            }
        } else {
            if (mDatas != null && mDatas.size() > 0) {
                for (int i = 0; i < mDatas.size(); i++) {
                    list.add(mDatas.get(i));
                }
            }
        }
        return list;
    }

    @Override
    public void doPhotoIconClick() {
        PhotoPickerConfig.builder()
                .setShowCamera(true)
                .setPreviewEnabled(true)
                .setGridColumnCount(4)
                .setSelected((ArrayList<String>) getDatas())
                .setPhotoCount(maxCount)
                .start(mContext);
    }

}
