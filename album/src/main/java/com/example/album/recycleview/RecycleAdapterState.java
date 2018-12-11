package com.example.album.recycleview;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.album.recycleview.RecycleAdapterState.State.STATE_EMPTY;
import static com.example.album.recycleview.RecycleAdapterState.State.STATE_NORMAL;

@IntDef({STATE_NORMAL, STATE_EMPTY})
@Retention(RetentionPolicy.SOURCE)
public @interface RecycleAdapterState {

    public static interface State {
        int STATE_NORMAL = 1;
        int STATE_EMPTY = 2;
    }

}
