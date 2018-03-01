package com.example.mapwithmarker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.rainbowadventures.utilities.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ravis on 09-01-2018.
 */

public class Edit_rainbow extends BaseActivity {
    public static final String TAG = "Edit_rainbow";
    private RequestQueue queue;
    private Rainbow _rainbow;
    EditText eRainbowName;
    EditText eRainbowDesc;
    TextView eNumberPics;
    Button eButtonSubmit;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_rainbow);
        //Toolbar toolbar = findViewById(R.id.toolbarEditRainbow);
        //setSupportActionBar(toolbar);
        _rainbow = (Rainbow)getIntent().getParcelableExtra("rainbow");

        eRainbowName = (EditText) findViewById(R.id.ERainbowName);
        eRainbowDesc = (EditText) findViewById(R.id.ERainbowDesc);
        eRainbowName.setText(_rainbow.rainbow_name);
        eRainbowDesc.setText(_rainbow.description);
        String s = String.valueOf(_rainbow.getnumberPics())+ " "+
                getResources().getString(R.string.number_image)
                + " " + getResources().getString(R.string.maximum_image_allowed_number);
        eNumberPics = (TextView) findViewById(R.id.EtextviewImageNumber);
        eNumberPics.setText(s);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if(menuItem.getItemId() == R.id.menuImage)
        {
            if(_rainbow.photos!=null && _rainbow.photos.size()>0) {
                Intent intent = new Intent(getBaseContext(), ImageGallery.class);
                intent.putExtra("filename", filename);
                intent.putStringArrayListExtra("photoArray", (ArrayList<String>)_rainbow.photos);
                startActivity(intent);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menu_inflator = getMenuInflater();
        menu_inflator.inflate(R.menu.options_rainbowdetail,menu);
        return true;
    }

    public void submitClick(View view)
    {
        _rainbow.rainbow_name = ((EditText)findViewById(R.id.ERainbowName)).getText().toString();
        _rainbow.description = ((EditText)findViewById(R.id.ERainbowDesc)).getText().toString();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        String payload = "";
        final JSONObject js = new JSONObject();
        try {
            js.put("rainbow_name", _rainbow.rainbow_name);
            js.put("description", _rainbow.description );
            js.put("id",_rainbow.id);
            payload = js.toString();
            Log.d(TAG,payload);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }

        final String ur = AppApplication.baseurl + "/update_rainbow";
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        JsonRequest<String> jsonObjReq = new JsonRequest<String>(
                Request.Method.POST, ur, payload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Intent d = new Intent();
                        d.putExtra("rainbow",_rainbow);
                        setResult(RESULT_OK,d);
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
                progressDialog.dismiss();
            }
        }
        ) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }

                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        jsonObjReq.setTag(TAG);
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq);

    }

}
