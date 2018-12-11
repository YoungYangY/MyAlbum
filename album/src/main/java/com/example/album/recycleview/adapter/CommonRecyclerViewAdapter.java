package com.example.album.recycleview.adapter;

import android.content.Context;
import android.view.LayoutInflater;


import com.example.album.recycleview.adapter.viewholder.ItemViewDelegate;
import com.example.album.recycleview.adapter.viewholder.ViewRecycleHolder;

import java.util.List;

public abstract class CommonRecyclerViewAdapter<T> extends MultiItemTypeRecyclerAdapter<T> {
    protected Context mContext;
    protected int            mLayoutId;
    protected LayoutInflater mInflater;
    protected int headCount = 0;
    public    DelImage delImage;

    public void setHeadCount(int headCount) {
        this.headCount = headCount;
    }

    public CommonRecyclerViewAdapter(final Context context, final int layoutId) {
        super(context);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId()
            {
                return layoutId;
            }

            @Override
            public boolean isForViewType( T item, int position)
            {
                return true;
            }

            @Override
            public void convert(ViewRecycleHolder holder, T t, int position) {
                CommonRecyclerViewAdapter.this.convert(holder, t, position);
            }
        });
    }


    public CommonRecyclerViewAdapter(final Context context, final int layoutId, List<T> datas) {
        super(context, datas);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId()
            {
                return layoutId;
            }

            @Override
            public boolean isForViewType( T item, int position)
            {
                return true;
            }

            @Override
            public void convert(ViewRecycleHolder holder, T t, int position) {
                CommonRecyclerViewAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ViewRecycleHolder holder, T t, int position);
    public void doPhotoIconClick(){};
    public void setPhotoSelectedData(List<String> list){};
    public interface DelImage{
        void delImage(int positon);
    }

    public void setDelImageInterface(CommonRecyclerViewAdapter.DelImage delImage){
        this.delImage = delImage;
    }

}
