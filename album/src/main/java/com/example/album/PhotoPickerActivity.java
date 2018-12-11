package com.example.album;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.album.adapter.PhotoGridAdapter;
import com.example.album.entity.Photo;
import com.example.album.entity.PhotoDirectory;
import com.example.album.event.CheckSelectedNumberListener;
import com.example.album.event.OnItemCheckListener;
import com.example.album.event.OnPhotoClickListener;
import com.example.album.utils.AbPreconditions;
import com.example.album.utils.ImageCaptureManager;
import com.example.album.utils.MediaStoreHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerActivity extends Activity {

    private ImageView ivClose;
    private TextView tvDone;

    private ImageCaptureManager captureManager;
    private RecyclerView recyclerView;
    private PhotoGridAdapter photoGridAdapter;

    Bundle mediaStoreArgs;

    private boolean showGif = false;
    private boolean showCamera = false;
    private boolean previewEnabled = false;

    private int maxSelectedCount;
    private int columnNumber;

    private ArrayList<String> originalPhotos = null;
    private List<PhotoDirectory> directories;

    private RequestManager mGlideRequestManager;


    @Override
    public int layoutId() {
        return R.layout.activity_pick_layout;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        previewEnabled = getIntent().getBooleanExtra(EXTRA_PREVIEW_ENABLED, true);

        maxSelectedCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
        columnNumber = getIntent().getIntExtra(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);

        originalPhotos = getIntent().getStringArrayListExtra(EXTRA_ORIGINAL_PHOTOS);


        ivClose = findViewById(R.id.tv_close);
        tvDone = findViewById(R.id.tv_done);

        captureManager = new ImageCaptureManager(this);
        mGlideRequestManager = Glide.with(this);

        recyclerView = findViewById(R.id.rv_photos);
        RecyclerBuild recyclerBuild = new RecyclerBuild(recyclerView);
        recyclerBuild.setGridLayout(columnNumber).setItemSpace(AbDisplayUtil.dip2px(3), AbDisplayUtil.dip2px(3), AbDisplayUtil.dip2px(3));


        directories = new ArrayList<>();
        photoGridAdapter = new PhotoGridAdapter(this, mGlideRequestManager, directories, originalPhotos, columnNumber, maxSelectedCount);
        photoGridAdapter.setShowCamera(showCamera);
        photoGridAdapter.setPreviewEnable(previewEnabled);

        recyclerView.setAdapter(photoGridAdapter);
        updateDone();

        addListener();
    }


    private void addListener() {
        photoGridAdapter.setOnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbRxPermission.checkCameraPermission(PhotoPickerActivity.this, new RxCallBack() {
                    @Override
                    public void onOk() {
                        openCamera();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onNeverAsk(Activity aty, String permission) {

                    }
                });
            }
        });

        photoGridAdapter.setOnItemCheckListener(new OnItemCheckListener() {
            @Override
            public boolean onItemCheck(int position, Photo photo, int selectedItemCount) {
                if (maxSelectedCount <= 1) {
                    List<String> photos = photoGridAdapter.getSelectedPhotos();
                    if (!photos.contains(photo.getPath())) {
                        photos.clear();
                        photoGridAdapter.notifyDataSetChanged();
                    }
                    return true;
                }

                if (selectedItemCount > maxSelectedCount) {
                    return false;
                }
                return true;
            }
        });

        photoGridAdapter.setCheckSelectedNumberListener(new CheckSelectedNumberListener() {
            @Override
            public void notify(int num) {
                updateDone();
            }
        });

        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                List<String> photoPaths = directories.get(0).getPhotoPaths();
                AbSharedPreferencesUtil.putString(PhotoPreview.EXTRA_PHOTOS, AbJsonParseUtils.getJsonString(photoPaths));

                PhotoPreview.builder()
//                        .setPhotos((ArrayList<String>) directories.get(0).getPhotoPaths())
                        .setSelectedPhotos((ArrayList<String>) photoGridAdapter.getSelectedPhotos())
                        .setPhotoMaxCount(maxSelectedCount)
                        .setCurrentItem(showCamera ? position - 1 : position).start(PhotoPickerActivity.this);
            }
        });
    }


    private void openCamera() {
        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            // TODO No Activity Found to handle Intent
            e.printStackTrace();
        }
    }

    @Override
    public void initData() {
        mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, showGif);
        MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs, new MediaStoreHelper.PhotosResultCallback() {
            @Override
            public void onResultCallback(List<PhotoDirectory> dir) {
                directories.clear();
                directories.addAll(dir);
                photoGridAdapter.notifyDataSetChanged();
            }
        });

        initListener();
    }


    private void initListener() {
        findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PhotoPickerConfig.KEY_SELECTED_PHOTOS, (Serializable) photoGridAdapter.getSelectedPhotos());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AbPreconditions.checkNotEmptyList(photoGridAdapter.getSelectedPhotoPaths())) {
                    List<String> selectedPhotos = photoGridAdapter.getSelectedPhotos();
                    AbSharedPreferencesUtil.putString(PhotoPreview.EXTRA_PHOTOS, AbJsonParseUtils.getJsonString(selectedPhotos));

                    PhotoPreview.builder()
//                            .setPhotos((ArrayList<String>) photoGridAdapter.getSelectedPhotos())
                            .setSelectedPhotos((ArrayList<String>) photoGridAdapter.getSelectedPhotos())
                            .setPhotoMaxCount(maxSelectedCount)
                            .setOnlyPreviewSelectedPhotos(true)
                            .setCurrentItem(0).start(PhotoPickerActivity.this);
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (captureManager == null) {

                captureManager = new ImageCaptureManager(this);
            }

            captureManager.galleryAddPic();
            if (directories.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                Photo photo = new Photo(path.hashCode(), path);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, photo);
                directory.setCoverPath(path);
                photoGridAdapter.toggleSelection(photo);
                photoGridAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == PhotoPreview.REQUEST_CODE && data != null) {
            ArrayList<String> paths = data.getStringArrayListExtra(PhotoPreview.EXTRA_PHOTOS);

            if (resultCode == PhotoPreview.BACK_RESULT_CODE) {
                photoGridAdapter.setSelecedPhotos(paths);
                updateDone();
            } else if (resultCode == PhotoPreview.DONE_RESULT_CODE) {
                Intent intent = new Intent();
                intent.putExtra(PhotoPickerConfig.KEY_SELECTED_PHOTOS, paths);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


    /**
     * 更新选中图片数量
     */
    public void updateDone() {

        List<String> photos = photoGridAdapter.getSelectedPhotos();
        int size = photos == null ? 0 : photos.size();
        tvDone.setClickable(size > 0);
        if (size > 0) {
            tvDone.setBackgroundResource(R.color.done_bg_color);
        } else {
            tvDone.setBackgroundResource(R.color.nodone_bg_color);
        }
        if (maxSelectedCount > 1) {
            tvDone.setText(getString(R.string.__picker_done_with_count, size, maxSelectedCount));
        } else {
            tvDone.setText(getString(R.string.__picker_done));
        }

    }

}
