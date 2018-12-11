package com.example.album.recycleview.adapter.viewholder;


public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewRecycleHolder holder, T t, int position);

}
