package com.example.mapwithmarker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.rainbowadventures.utilities.MySingleton;

import java.util.Locale;

/**
 * Created by ravis on 09-01-2018.
 */

public class Register_activity extends AppCompatActivity {

    public static final String TAG = "Register_activity";
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);
        addListenerOnButton();
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public void addListenerOnButton()
    {
        int df = R.id.butnRegister;
        Button b = (Button) findViewById(df);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                RegisterClick();
            }
        });
    }

    public void RegisterClick()
    {
        String username = ((EditText)findViewById(R.id.textUsername)).getText().toString();
        if(username.isEmpty())
        {
            ((EditText)findViewById(R.id.textUsername)).setError("Username mandatory");
            return;
        }
        String email = ((EditText)findViewById(R.id.textEmail)).getText().toString();
        if(email.isEmpty())
        {
            ((EditText)findViewById(R.id.textEmail)).setError("Email mandatory");
            return;
        }
        String password = ((EditText)findViewById(R.id.textPassword)).getText().toString();
        if(password.isEmpty())
        {
            ((EditText)findViewById(R.id.textPassword)).setError("Password mandatory");
            return;
        }
        else
        {
            String confirmpassword = ((EditText)findViewById(R.id.textConfirmPassword)).getText().toString();
            if(!confirmpassword.equals(password))
            {
                ((EditText)findViewById(R.id.textConfirmPassword)).setError("This should match password.");
                return;
            }
        }
        PrepareVolley(username,password,email);
    }

    private void PrepareVolley(final String u,String p,String e)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);


        String ur = AppApplication.baseurl + "/registeruser?username="+u+"&password="+p+"&email_id="+e;
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ur,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.isEmpty())
                        {
                            PrefSingleton.getInstance().writePreference("userid", response);
                            PrefSingleton.getInstance().writePreference("username", u);
                            Login_activity.userid = Integer.parseInt(response);
                            Login_activity.UserId = response;
                            Intent i = new Intent(getBaseContext(),MapsMarkerActivity.class);
                            startActivity(i);
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
}
