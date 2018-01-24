package com.example.mapwithmarker;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.net.FileNameMap;
import java.util.ArrayList;


/**
 * Created by ravis on 03-01-2018.
 */

public class ShowRainbowActivity extends BaseActivity {

    private String filename;
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
            String name = getIntent().getStringExtra("name");
            String desc = getIntent().getStringExtra("desc");
            Double lati = getIntent().getDoubleExtra("lati",0);
            filename = CreateRainbowFragment.df.format(lati);
            _rainbow = Rainbow.fromFile(this,filename);
            FillDataFromRainbow();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void FillDataFromRainbow()
    {
        TextView tvName = (TextView) findViewById(R.id.VRainbowName);
        TextView tvDesc = (TextView) findViewById(R.id.VTextDesc);
        tvName.setText(_rainbow.Name);
        tvDesc.setText(_rainbow.Description);
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
                intent.putExtra("filename", filename);
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
                        //dialogInterface.dismiss();
                        try {
                            File f = new File(_context.getFilesDir(),filename);
                            if(f!=null && f.exists())
                            {
                                f.delete();
                            }
                            File fd = new File(_context.getFilesDir(),filename+"D");
                            if(fd!=null && fd.exists() && fd.isDirectory())
                            {
                                String[] children = fd.list();
                                for (int ii = 0; ii < children.length; ii++)
                                {
                                    new File(fd, children[ii]).delete();
                                }
                                fd.delete();
                            }
                            setResult(RESULT_OK,null);
                            finish();
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

}
