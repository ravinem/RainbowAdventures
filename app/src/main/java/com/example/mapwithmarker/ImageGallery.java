package com.example.mapwithmarker;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by ravis on 04-01-2018.
 */

public class ImageGallery extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_gallery);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        String foldername = getIntent().getStringExtra("filename");
        ArrayList<String> filenames = getIntent().getStringArrayListExtra("photoArray");
        ImageGalleryAdapter adapter = new ImageGalleryAdapter(this, RainbowPhoto.getSpacePhotos(filenames,getBaseContext()));
        recyclerView.setAdapter(adapter);

    }


}
