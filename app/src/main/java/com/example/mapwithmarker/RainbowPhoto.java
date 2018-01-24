package com.example.mapwithmarker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ravis on 04-01-2018.
 */

public class RainbowPhoto implements Parcelable {

    private String mUrl;
    private String mTitle;

    public RainbowPhoto(String url, String title) {
        mUrl = url;
        mTitle = title;
    }

    protected RainbowPhoto(Parcel in) {
        mUrl = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<RainbowPhoto> CREATOR = new Creator<RainbowPhoto>() {
        @Override
        public RainbowPhoto createFromParcel(Parcel in) {
            return new RainbowPhoto(in);
        }

        @Override
        public RainbowPhoto[] newArray(int size) {
            return new RainbowPhoto[size];
        }
    };

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public static RainbowPhoto[] getSpacePhotos(ArrayList<String> fileNames, Context _context) {

     /*   RainbowPhoto[] pictures = new RainbowPhoto[1];
        RainbowPhoto rp = new RainbowPhoto("vjvjhvhj","bchchghgfh");
        pictures[0] = rp;
        return pictures;*/

        //String[] photos = new File( _context.getFilesDir(), foldername+"D").list();
        RainbowPhoto[] pics = new RainbowPhoto[fileNames.size()];
        int length = pics.length;
        for (int i=0;i<length;i++) {
            //pics[i] = new RainbowPhoto(_context.getFilesDir().getAbsolutePath()+"/"+foldername+"D"+"/"+ photos[i],"");
            pics[i] = new RainbowPhoto(fileNames.get(i),"");
        }
        return pics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrl);
        parcel.writeString(mTitle);
    }
}
