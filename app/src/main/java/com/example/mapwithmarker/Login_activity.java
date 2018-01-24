package com.example.mapwithmarker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ravis on 09-01-2018.
 */

public class Login_activity extends AppCompatActivity {

    public Login_activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ActionBar ab = getSupportActionBar();
        if(ab!=null)
        {
            ab.setTitle(R.string.app_name);
        }
        PrefSingleton.getInstance().Initialize(getApplicationContext());

        Integer UserId = PrefSingleton.getInstance().readPreference("userid");
        if(UserId > 0)
        {
            GoMain();
        }
    }

    private void GoMain()
    {
        Intent i = new Intent(this,MapsMarkerActivity.class);
        startActivity(i);
    }
    public void LoginClick(View view)
    {

        PrefSingleton.getInstance().writePreference("userid", 2);
        GoMain();
    }

    public void RegisterClick(View view)
    {
        Intent i = new Intent(this,Register_activity.class);
        startActivity(i);
    }

    public void ForgotClick(View viewS)
    {

    }
}
