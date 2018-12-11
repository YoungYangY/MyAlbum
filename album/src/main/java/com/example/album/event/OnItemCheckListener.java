package com.example.album.event;


import com.example.album.entity.Photo;

public interface OnItemCheckListener {

    /***
     *
     * @param position 所选图片的位置
     * @param photo     所选的图片
     * @param selectedItemCount  已选数量
     * @return enable check
     */
    boolean onItemCheck(int position, Photo photo, int selectedItemCount);
}
