package com.example.album.glideimageview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.album.glideimageview.progress.OnGlideImageViewListener;
import com.example.album.glideimageview.progress.OnProgressListener;
import com.example.album.glideimageview.progress.ProgressManager;

import java.lang.ref.WeakReference;

/**
 * Created by sunfusheng on 2017/6/6.
 */
public class GlideImageLoader {

    private static final String ANDROID_RESOURCE = "android.resource://";
    private static final String FILE             = "file://";
    private static final String SEPARATOR        = "/";
    private static final String HTTP             = "http";

    private WeakReference<ImageView> mImageView;
    private Object mImageUrlObj;
    private long    mTotalBytes    = 0;
    private long    mLastBytesRead = 0;
    private boolean mLastStatus    = false;
    private Handler mMainThreadHandler;

    private OnProgressListener internalProgressListener;
    private OnGlideImageViewListener onGlideImageViewListener;
    private OnProgressListener       onProgressListener;

    public static GlideImageLoader create(ImageView imageView) {
        return new GlideImageLoader(imageView);
    }

    private GlideImageLoader(ImageView imageView) {
        mImageView = new WeakReference<>(imageView);
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public ImageView getImageView() {
        if (mImageView != null) {
            return mImageView.get();
        }
        return null;
    }

    public Context getContext() {
        if (getImageView() != null) {
            return getImageView().getContext();
        }
        return null;
    }

    public String getImageUrl() {
        if (mImageUrlObj == null) return null;
        if (!(mImageUrlObj instanceof String)) return null;
        return (String) mImageUrlObj;
    }

    public Uri resId2Uri(int resourceId) {
        if (getContext() == null) return null;
        return Uri.parse(ANDROID_RESOURCE + getContext().getPackageName() + SEPARATOR + resourceId);
    }

    public void load(int resId, RequestOptions options) {
        load(resId2Uri(resId), options);
    }

    public void load(Uri uri, RequestOptions options) {
        if (uri == null || getContext() == null) return;
        requestBuilder(uri, options).into(getImageView());
    }

    public void load(String url, RequestOptions options) {
        if (url == null || getContext() == null) return;
        requestBuilder(url, options).into(getImageView());
    }

    private RequestBuilder<Drawable> requestBuilder(Object obj, RequestOptions options) {
        this.mImageUrlObj = obj;
        return Glide.with(getContext())
                .load(obj)
                .apply(options.skipMemoryCache(true).dontAnimate())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mainThreadCallback(mLastBytesRead, mTotalBytes, true, e);
                        ProgressManager.removeProgressListener(internalProgressListener);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mainThreadCallback(mLastBytesRead, mTotalBytes, true, null);
                        ProgressManager.removeProgressListener(internalProgressListener);
                        return false;
                    }
                });
    }

    public RequestOptions requestOptions(int placeholderResId) {
        return requestOptions(placeholderResId, placeholderResId);
    }


    private RequestOptions requestOptions(int placeholderResId, int errorResId) {
        if (mImageView.get() != null && mImageView.get().getLayoutParams() != null) {
            if (mImageView.get().getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT
                    || mImageView.get().getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT){
                return new RequestOptions()
                        .placeholder(placeholderResId)
                        .error(errorResId);
            }
        }
        return new RequestOptions()
                .placeholder(placeholderResId)
                .error(errorResId)
                .centerCrop();
    }

    private RequestOptions requestOptions(int placeholderResId, int width, int height) {
        return new RequestOptions()
                .override(width, height)
                .placeholder(placeholderResId)
                .error(placeholderResId)
                .centerCrop();
    }

    public RequestOptions circleRequestOptions(int placeholderResId) {
        return circleRequestOptions(placeholderResId, placeholderResId);
    }

    private RequestOptions circleRequestOptions(int placeholderResId, int errorResId) {
        return requestOptions(placeholderResId, errorResId)
                .circleCrop();
//                .transform(new GlideCircleTransformation()).centerCrop();
    }

    public void loadImage(String urlOrPath, int placeholderResId) {
        if (TextUtils.isEmpty(urlOrPath)) {
            load(placeholderResId,requestOptions(placeholderResId));
            return;
        }
        if (isNetImage(urlOrPath)) {
            load(urlOrPath, requestOptions(placeholderResId));
        } else {
            load(FILE + urlOrPath, requestOptions(placeholderResId));
        }
    }

    public void loadImage(@DrawableRes int resId, int placeholderResId) {
        load(resId, requestOptions(placeholderResId));
    }

    public void loadImage(@DrawableRes int resId, int placeholderResId, int width, int height) {
        load(resId, requestOptions(placeholderResId, width, height));
    }

    public void loadImage(String urlOrPath, int placeholderResId, int width, int height) {
        if (TextUtils.isEmpty(urlOrPath)) {
            load(placeholderResId, requestOptions(placeholderResId, width, height));
            return;
        }
        if (isNetImage(urlOrPath)) {
            load(urlOrPath, requestOptions(placeholderResId, width, height));
        } else {
            load(FILE + urlOrPath, requestOptions(placeholderResId, width, height));
        }
    }


    public void loadCircleImage(String urlOrPath, int placeholderResId) {
        load(urlOrPath, circleRequestOptions(placeholderResId));
    }

    public void loadCircleImage(int resId, int placeholderResId) {
        load(resId, circleRequestOptions(placeholderResId));
    }

    private void addProgressListener() {
        if (getImageUrl() == null) return;
        final String url = getImageUrl();
        if (!url.startsWith(HTTP)) return;

        internalProgressListener = new OnProgressListener() {
            @Override
            public void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone, GlideException exception) {
                if (totalBytes == 0) return;
                if (!url.equals(imageUrl)) return;
                if (mLastBytesRead == bytesRead && mLastStatus == isDone) return;

                mLastBytesRead = bytesRead;
                mTotalBytes = totalBytes;
                mLastStatus = isDone;
                mainThreadCallback(bytesRead, totalBytes, isDone, exception);

                if (isDone) {
                    ProgressManager.removeProgressListener(this);
                }
            }
        };
        ProgressManager.addProgressListener(internalProgressListener);
    }

    private void mainThreadCallback(final long bytesRead, final long totalBytes, final boolean isDone, final GlideException exception) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                final int percent = (int) ((bytesRead * 1.0f / totalBytes) * 100.0f);
                if (onProgressListener != null) {
                    onProgressListener.onProgress((String) mImageUrlObj, bytesRead, totalBytes, isDone, exception);
                }

                if (onGlideImageViewListener != null) {
                    onGlideImageViewListener.onProgress(percent, isDone, exception);
                }
            }
        });
    }

    public void setOnGlideImageViewListener(String imageUrl, OnGlideImageViewListener onGlideImageViewListener) {
        this.mImageUrlObj = imageUrl;
        this.onGlideImageViewListener = onGlideImageViewListener;
        addProgressListener();
    }

    public void setOnProgressListener(String imageUrl, OnProgressListener onProgressListener) {
        this.mImageUrlObj = imageUrl;
        this.onProgressListener = onProgressListener;
        addProgressListener();
    }

    private boolean isNetImage(String urlOrPath) {
        return urlOrPath.startsWith("https") || urlOrPath.startsWith("http");
    }
}
