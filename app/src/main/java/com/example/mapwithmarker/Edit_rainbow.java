package com.example.mapwithmarker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by ravis on 09-01-2018.
 */

public class Edit_rainbow extends BaseActivity {
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
        _rainbow = (Rainbow)getIntent().getParcelableExtra("rainbow");
        filename = CreateRainbowFragment.df.format(_rainbow.latitude);

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
        //r.numberPics = currentPhotosTakenNumber;
        Gson g = new Gson();
        String data = g.toJson(_rainbow);
        File f = new File(getBaseContext().getFilesDir(),filename);
        if(f.exists())
        {
            f.delete();
        }
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(f);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent d = new Intent();
        d.putExtra("rainbow",_rainbow);
        setResult(RESULT_OK,d);
        finish();
    }


}
