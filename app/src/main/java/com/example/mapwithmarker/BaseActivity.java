package com.example.mapwithmarker;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ravis on 09-01-2018.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if(ab!=null)
        {
            ab.setTitle("");
        }
    }

}
