package com.example.mapwithmarker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravis on 01-01-2018.
 */

public class Rainbow implements Parcelable {
    public String rainbow_name;
    public String description;
    public double latitude;
    public double longitude;
    private int numberPics;
    public List<String> photos = new ArrayList<String>();
    public int id;
    public int user_id;

    public Rainbow()
    {}

    public int getnumberPics()
    {
        return photos.size();
    }

    public static final Parcelable.Creator<Rainbow> CREATOR
            = new Parcelable.Creator<Rainbow>() {
        public Rainbow createFromParcel(Parcel in) {
            return new Rainbow(in);
        }

        public Rainbow[] newArray(int size) {
            return new Rainbow[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(rainbow_name);
        parcel.writeString(description);
       // parcel.writeInt(numberPics);
        parcel.writeList(photos);
        parcel.writeInt(id);
        parcel.writeInt(user_id);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    private Rainbow(Parcel in)
    {
        rainbow_name = in.readString();
        description = in.readString();
       // numberPics = in.readInt();
        in.readList(photos,List.class.getClassLoader());
        id = in.readInt();
        user_id = in.readInt();
        latitude = in.readDouble();
        longitude = in .readDouble();
    }

    public static Rainbow fromFile(Context c, String filename)
    {
        File f = new File(c.getFilesDir(),filename);
        //for(File f : files)
        //{
        Gson g = new Gson();
        String json;
        try {
            FileInputStream fr = new FileInputStream(f);
            InputStream is = fr;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fr.close();
            json = new String(buffer,"UTF-8");
            Rainbow r = g.fromJson(json,Rainbow.class);
            return r;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
