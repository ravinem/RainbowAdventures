package com.example.mapwithmarker;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.theartofdev.fastimageloader.target.TargetAvatarImageView;
import com.theartofdev.fastimageloader.target.TargetImageView;

import java.io.File;

/**
 * Created by ravis on 06-01-2018.
 */

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {

    private RainbowPhoto[] mSpacePhotos;
    private Context mContext;

    public ImageGalleryAdapter(Context context, RainbowPhoto[] spacePhotos) {
        mContext = context;
        mSpacePhotos = spacePhotos;
    }

    private Context getContext()
    {
        return mContext;
    }

    @Override
    public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.large_image, parent, false);
        ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

        RainbowPhoto spacePhoto = mSpacePhotos[position];
        ImageView imageView = ((MyViewHolder) holder).mPhotoImageView;
        //File f = new File(spacePhoto.getUrl());
        //Glide.with(mContext)
          //    .load(spacePhoto.getUrl())//.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
              //.placeholder(R.drawable.ic_search)
            //  .into(imageView);
        try {
            ((TargetAvatarImageView) (imageView)).loadImage(spacePhoto.getUrl(), Specs.IMAGE_AVATAR);
        }
        catch(Exception ex)
        {
         ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (mSpacePhotos.length);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mPhotoImageView;
        //public TextView mText;

        public MyViewHolder(View itemView) {

            super(itemView);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            mPhotoImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                RainbowPhoto spacePhoto = mSpacePhotos[position];
                Intent intent = new Intent(mContext, ImageDetail.class);
                intent.putExtra(ImageDetail.EXTRA_SPACE_PHOTO, spacePhoto);
                mContext.startActivity(intent);
            }
        }
    }

}
