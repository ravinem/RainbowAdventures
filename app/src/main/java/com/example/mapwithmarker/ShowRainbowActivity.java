package com.example.mapwithmarker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.rainbowadventures.utilities.MySingleton;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by ravis on 03-01-2018.
 */

public class ShowRainbowActivity extends BaseActivity {

    public static final String TAG = "Register_activity";
    private RequestQueue queue;
    private Rainbow _rainbow;
    public ShowRainbowActivity() {
        // Required empty public constructor12
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Retrieve the content view that renders the map.
            setContentView(R.layout.markerinfowindowlayout);
            Toolbar toolbar = findViewById(R.id.toolbarShowRainbow);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            int id = getIntent().getIntExtra("id",0);
            GetRainbowFromId(id);
            actionbar.setTitle("");

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void FillDataFromRainbow()
    {
        TextView tvName = (TextView) findViewById(R.id.VRainbowName);
        TextView tvDesc = (TextView) findViewById(R.id.VTextDesc);
        tvName.setText(_rainbow.rainbow_name);
        tvDesc.setText(_rainbow.description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menu_inflator = getMenuInflater();
menu_inflator.inflate(R.menu.options_rainbowdetail,menu);
     return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if(menuItem.getItemId() == R.id.menuImage)
        {
            //File f = new File(getBaseContext().getFilesDir(),filename+"D");
            if(_rainbow.photos!=null && _rainbow.photos.size()>0) {
                Intent intent = new Intent(getBaseContext(), ImageGallery.class);
                //intent.putExtra("filename", filename);
                intent.putStringArrayListExtra("photoArray", (ArrayList<String>)_rainbow.photos);
                startActivity(intent);
            }
        }
        return true;
    }

    public void onDelete(View view)
    {
        AskForThePermission(this,R.string.alert_delete_title,R.string.alert_delete_message);

    }

    public void onEdit(View view)
    {
        try {
            Intent i = new Intent(this, Edit_rainbow.class);
            i.putExtra("rainbow", _rainbow);
            startActivityForResult(i, 70);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 70) {
            if (resultCode == Activity.RESULT_OK) {
                _rainbow = (Rainbow) data.getParcelableExtra("rainbow");
                FillDataFromRainbow();
            }
        }
    }

    public void AskForThePermission(final Context _context, int titleId, int messageId)
    {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(_context);
        aBuilder.setTitle(titleId);
        aBuilder.setMessage(messageId)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            DeleteRainbow(_rainbow.id);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog ad = aBuilder.create();
        ad.show();
    }

    private void GetRainbowFromId(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);


        String ur = AppApplication.baseurl + "/Single_Rainbow_Details?rainbow_id="+id;
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ur,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.isEmpty())
                        {
                            Gson g = new Gson();
                            _rainbow = g.fromJson(response, Rainbow.class);
                            FillDataFromRainbow();
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

    private void DeleteRainbow(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);


        String ur = AppApplication.baseurl + "/delete_rainbow?id="+id;
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
                            d.putExtra("id",_rainbow.id);
                            setResult(RESULT_OK,d);
                            finish();
                        }

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
