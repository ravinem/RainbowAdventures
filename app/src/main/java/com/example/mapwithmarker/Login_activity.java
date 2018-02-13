package com.example.mapwithmarker;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.fastimageloader.FastImageLoader;


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

        String UserId = PrefSingleton.getInstance().readPreferenceString("userid");
        if(!UserId.isEmpty())
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
        String s = ((EditText)findViewById(R.id.textUsername)).getText().toString();
        if(s.isEmpty())
        {
            ((EditText)findViewById(R.id.textUsername)).setError( "User name is required!" );
            return;
        }
        PrefSingleton.getInstance().writePreference("userid", s);
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
