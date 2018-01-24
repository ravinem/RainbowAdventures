package com.example.mapwithmarker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
        //mImageView = (TouchImageView) findViewById(R.id.image);
        mImageView = (ImageView) findViewById(R.id.image);
        RainbowPhoto rainbowPhoto = getIntent().getParcelableExtra(EXTRA_SPACE_PHOTO);
        //Bitmap bm = BitmapFactory.decodeFile(rainbowPhoto.getUrl());
        //mImageView.setImageBitmap(bm);
        Glide.with(this)
                .load(rainbowPhoto.getUrl())
                .asBitmap()
                .placeholder(R.drawable.ic_search)
                //.error(R.drawable.ic_cloud_off_red)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView);
    }
}
