package com.example.mapwithmarker;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rainbowadventures.utilities.MySingleton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.fastimageloader.FastImageLoader;


/**
 * Created by ravis on 09-01-2018.
 */

public class Login_activity extends AppCompatActivity {

    public static final String TAG = "Login_activity";
    private RequestQueue queue;
    public static String UserId=null;
    public static int userid = 0;
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

        UserId = PrefSingleton.getInstance().readPreferenceString("userid");

        if(!UserId.isEmpty())
        {
            userid = Integer.parseInt(UserId);
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
        String p = ((EditText)findViewById(R.id.textPassword)).getText().toString();
        if(p.isEmpty())
        {
            ((EditText)findViewById(R.id.textPassword)).setError( "Password required!" );
            return;
        }
        PrepareVolley(s,p);

    }

    public void ForgotClick(View viewS)
    {

    }

    private void PrepareVolley(final String u,String p)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        String baseurl = getResources().getString(R.string.rainbowadventureswebservice);
        baseurl += "/loginuser?username="+u+"&user_password="+p;
        queue = MySingleton.getInstance(this.getApplicationContext()).
            getRequestQueue();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, baseurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            int id = 0;
                            try
                            {
                                id = Integer.parseInt(response);
                            }
                            catch(Exception e)
                            {}
                            if(id > 0 ) {
                                PrefSingleton.getInstance().writePreference("userid", response);
                                PrefSingleton.getInstance().writePreference("username", u);
                                userid = Integer.parseInt(response);
                                UserId = response;
                                GoMain();
                            }
                            else
                            {
                                ((EditText)findViewById(R.id.textUsername)).setError( "Invalid login" );
                            }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        stringRequest.setTag(TAG);
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void RegisterClick(View view) {
        Intent i = new Intent(this,Register_activity.class);
        startActivity(i);
    }
}
