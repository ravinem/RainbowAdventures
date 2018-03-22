package com.example.mapwithmarker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rainbowadventures.utilities.MySingleton;

/**
 * Created by ravis on 18-03-2018.
 */

public class SearchUser extends BaseActivity {
    private static final String TAG = "SearchUser";
    EditText et=null;
    TextView tv = null;
    private RequestQueue queue;
    Button su = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user);
        tv = (TextView) findViewById(R.id.NotfoundTextview);
        et = (EditText)findViewById(R.id.searchuserEditText);
        su = (Button)findViewById(R.id.searchUserButton);
        su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Clicked();
            }
        });
    }

    public void Clicked()
    {
        tv.setVisibility(View.GONE);
        final String name = et.getText().toString();
        if(name.isEmpty())
        {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Finding");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);


        String ur = AppApplication.baseurl + "/usernameIdByName?name="+name;
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ur,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.isEmpty())
                        {
                            progressDialog.dismiss();
                            Intent d = new Intent();
                            d.putExtra("_userid",Integer.parseInt(response));
                            d.putExtra("_username",name);
                            setResult(RESULT_OK,d);
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tv.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
        stringRequest.setTag(TAG);
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
