package com.example.album.recycleview.itemdecorator;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
    SpaceLayoutDecoration.LINEAR_LAYOUT_VERTICAL, SpaceLayoutDecoration.LINEAR_LAYOUT_HORIZONTAL,
    SpaceLayoutDecoration.GRID_LAYOUT
}) @Retention(RetentionPolicy.SOURCE) public @interface Orientation {
}
