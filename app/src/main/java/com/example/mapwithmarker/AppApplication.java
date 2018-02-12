package com.example.mapwithmarker;

import android.app.Application;
import android.graphics.Bitmap;

import com.theartofdev.fastimageloader.FastImageLoader;
import com.theartofdev.fastimageloader.adapter.IdentityAdapter;
import com.theartofdev.fastimageloader.adapter.ImgIXAdapter;

/**
 * Created by ravis on 08-02-2018.
 */

public class AppApplication extends Application {
    public static final int FULL_IMAGE_SIZE = 640;

    public static final int AVATAR_SIZE = 150;

    @Override
    public void onCreate() {
        super.onCreate();

        FastImageLoader
                .init(this)
                .setDefaultImageServiceAdapter(new ImgIXAdapter())
                .setWriteLogsToLogcat(true)
                .setDebugIndicator(true);

        IdentityAdapter identityUriEnhancer = new IdentityAdapter();
        FastImageLoader.buildSpec(Specs.IMAGE_AVATAR)
                .setDimension(AVATAR_SIZE)
                .setImageServiceAdapter(identityUriEnhancer)
                .build();

        FastImageLoader.buildSpec(Specs.FULL_IMAGE)
                .setDimension(FULL_IMAGE_SIZE)
                .setPixelConfig(Bitmap.Config.RGB_565)
                .setImageServiceAdapter(identityUriEnhancer)
                .build();

    }
}
