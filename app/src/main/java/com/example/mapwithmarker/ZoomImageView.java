package com.example.mapwithmarker;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.theartofdev.fastimageloader.LoadedFrom;
import com.theartofdev.fastimageloader.ReusableBitmap;
import com.theartofdev.fastimageloader.target.TargetHelper;
import com.theartofdev.fastimageloader.target.TargetImageViewHandler;

import uk.co.senab.photoview.PhotoView;

public class ZoomImageView extends PhotoView
 {

   private ProgressBar mProgressBar;

    private TargetImageViewHandler mHandler;

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new ZoomTargetImageViewHandler(this);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHandler = new ZoomTargetImageViewHandler(this);
    }


    public void loadImage(String url, String specKey, String altSpecKey, ProgressBar progressBar) {
        mProgressBar = progressBar;
        mProgressBar.setVisibility(VISIBLE);
        mHandler.loadImage(url, specKey, altSpecKey, false);
    }


    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mHandler.onViewVisibilityChanged(visibility);
    }

    @Override
    public void onDraw(@SuppressWarnings("NullableProblems") Canvas canvas) {
        super.onDraw(canvas);
        TargetHelper.drawProgressIndicator(canvas, mHandler.getDownloaded(), mHandler.getContentLength());
    }

    private final class ZoomTargetImageViewHandler extends TargetImageViewHandler {


        public ZoomTargetImageViewHandler(ImageView imageView) {
            super(imageView);
            setInvalidateOnDownloading(true);
        }

        @Override
        protected void setImage(ReusableBitmap bitmap, LoadedFrom from) {
            super.setImage(bitmap, from);
            if (bitmap.getSpec().getKey().equals(mSpecKey)) {
                mProgressBar.setVisibility(GONE);
            }
        }
    }
}
