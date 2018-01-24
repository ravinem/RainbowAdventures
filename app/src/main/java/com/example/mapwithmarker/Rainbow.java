package com.example.mapwithmarker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    public LatLng coords;
    public String Name;
    public String Description;
    public int numberPics;
    public List<String> photos = new ArrayList<String>();

    public Rainbow()
    {}
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
        parcel.writeString(Name);
        parcel.writeString(Description);
        parcel.writeInt(numberPics);
        parcel.writeParcelable(coords,i);
        parcel.writeList(photos);
    }

    private Rainbow(Parcel in)
    {
        Name = in.readString();
        Description = in.readString();
        numberPics = in.readInt();
        coords = in.readParcelable(LatLng.class.getClassLoader());
        in.readList(photos,List.class.getClassLoader());
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
