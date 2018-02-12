package com.example.mapwithmarker;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.theartofdev.fastimageloader.target.TargetAvatarImageView;
import com.theartofdev.fastimageloader.target.TargetImageView;

/**
 * Created by ravis on 07-01-2018.
 */

public class ImageDetail extends BaseActivity {

    public static final String EXTRA_SPACE_PHOTO = "RainbowPhotoActivity.RAINBOW_PHOTO";
    //private TouchImageView mImageView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail);
        mImageView = (ImageView) findViewById(R.id.image);
        RainbowPhoto rainbowPhoto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ZoomImageView zoomImageView = (ZoomImageView) findViewById(R.id.image);
        zoomImageView.loadImage(rainbowPhoto.getUrl(), Specs.FULL_IMAGE, Specs.IMAGE_AVATAR, progressBar);
    }
}
