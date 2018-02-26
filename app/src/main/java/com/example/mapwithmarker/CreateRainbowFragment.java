package com.example.mapwithmarker;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.rainbowadventures.utilities.MySingleton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRainbowFragment extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "CreateRainbowFragment";
    private RequestQueue queue;
    public static DecimalFormat df = new DecimalFormat(".####");
    private LatLng _coords;
    private double _lati;
    private double _longi;
    private Button button = null;
    private ImageButton picsButton = null;
    private TextView tvNumberImages = null;
    private String filename  = "";
    private int currentPhotosTakenNumber =0;
    private List<String> photos ;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY_CAPTURE = 2;

    public CreateRainbowFragment() {
        // Required empty public constructor
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            super.onCreate(savedInstanceState);
            storageRef = FirebaseStorage.getInstance().getReference();
            setContentView(R.layout.fragment_create_rainbow);

            _lati = getIntent().getDoubleExtra("lati", 0);
            _longi = getIntent().getDoubleExtra("longi", 0);
            _coords = new LatLng(_lati, _longi);
            tvNumberImages = (TextView) findViewById(R.id.textviewImageNumber);
            filename = CreateRainbowFragment.df.format(_coords.latitude);
            addListenerOnButton();
            addListenerOnPicsButton();
            attachGalleryButtonOnClick();
        } catch (Exception e) {
            Log.e("e",e.getMessage());
        }
    }

    public void addListenerOnButton()
    {
        int df = R.id.submitRainbow;
        button = (Button) findViewById(df);
        button.setOnClickListener(this);
    }


    public void onClick(View view) {
        if(((EditText)findViewById(R.id.RainbowName)).getText().toString().isEmpty())
        {
            ((MaterialEditText)findViewById(R.id.RainbowName)).setError( "Rainbow name is required!" );
            return;
        }
        button.setText("Uploading");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading ....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        new BackgroundProcess().execute(this);
    }

    public void addListenerOnPicsButton()
    {
        int df = R.id.PicsButton;
        picsButton = (ImageButton) findViewById(df);
        picsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
            }
        });
    }

    public void attachGalleryButtonOnClick()
    {
        int df = R.id.GalleryButton;
        ImageButton galleryButton = (ImageButton) findViewById(df);
        final AppCompatActivity ac = this;
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                try {
                    //if (GetCurrentLocationHelper.checkPermission(ac)) {
                        galleryIntent();
                    //}
                }
                catch(Exception ex)
                {
                    Log.e("gallery",ex.getMessage());
                }
            }
        });
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_GALLERY_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotosTakenNumber++;
            String s = String.valueOf(currentPhotosTakenNumber)+ " "+
                        getResources().getString(R.string.number_image)
                        + " " + getResources().getString(R.string.maximum_image_allowed_number);
            tvNumberImages.setText(s);

        }
        if (requestCode == REQUEST_GALLERY_CAPTURE && resultCode == RESULT_OK) {
            final Intent _data = data;
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
            aBuilder.setTitle(R.string.alert_dialog_title);
            aBuilder.setMessage(R.string.gallery_dialog_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            currentPhotosTakenNumber++;
                            String s = String.valueOf(currentPhotosTakenNumber)+ " "+
                                    getResources().getString(R.string.number_image)
                                    + " " + getResources().getString(R.string.maximum_image_allowed_number);
                            tvNumberImages.setText(s);
                            onSelectFromGalleryResult(_data);
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

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File picsDirectory = new File(getFilesDir(), filename+"D");

        if (!picsDirectory.exists()) {
            if (!picsDirectory.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                picsDirectory      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if(currentPhotosTakenNumber > getResources().getInteger(R.integer.maximum_photos_allowed_for_rainbow))
        {
            return;
        }
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            // Error occurred while creating the File
        }
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                FileOutputStream otst = new FileOutputStream(photoFile);
                bm.compress(Bitmap.CompressFormat.JPEG,9,otst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        if(currentPhotosTakenNumber > getResources().getInteger(R.integer.maximum_photos_allowed_for_rainbow))
        {
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            try {
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        public void StartUpload()
        {
            try {
                UploadPicsToFirebase();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    private void UploadPicsToFirebase() throws FileNotFoundException {
        try {

            File picsDirectory = new File(getFilesDir(), filename + "D");
            String[] photoFiles = picsDirectory.list();
            photos = new ArrayList<String>();
            if(photoFiles ==null || photoFiles.length == 0 )
            {
                return;
            }
            progressDialog.setMax(100*photoFiles.length);
            for (int i = 0; i < photoFiles.length; i++) {
                final int totali = photoFiles.length;
                final int currenti = i;
                //InputStream stream = new FileInputStream(new File(picsDirectory.getAbsolutePath() + "/" + photoFiles[i]));
                File photoFile = new File(picsDirectory.getAbsolutePath() + "/" + photoFiles[i]);

                Uri file = Uri.fromFile(photoFile);
                //SharedPreferences sp = getPreferences(MODE_PRIVATE);

                String userid = PrefSingleton.getInstance().readPreferenceString("userid");
                StorageReference imagesRef = storageRef.child(userid+"/"+filename+"/"+photoFiles[i]);

                UploadTask uploadTask = null;
                uploadTask = imagesRef.putFile(file);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //Log.d("progress",String.valueOf("current progress is "+ progress));

                        //sets and increments value of progressbar
                        progressDialog.setProgress(currenti*100 + (int) Math.floor(progress));
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(CreateRainbowFragment.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String s = taskSnapshot.getDownloadUrl().toString();
                        photos.add(s);
                        Log.d("progress","done with i =  " + String.valueOf(currenti));
                    }
                });

            List<UploadTask> l= storageRef.getActiveUploadTasks();
                for (UploadTask t: l) {
                    com.google.android.gms.tasks.Tasks.await(t);
                }
                progressDialog.dismiss();
           /* int counter = l.size();
            while(counter > 0)
            {
                for (int j=0;j<l.size();j++)
                {
                    UploadTask t = l.get(j);
                    if(t.isComplete())
                    {
                        Log.d("progress","task completed");
                        String s = t.getResult().getDownloadUrl().toString();
                        if(!photos.contains(s)) {
                            counter--;
                            photos.add(s);
                            break;
                        }
                        }
                    }
            }*/
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

    private void ClearFilesFromPhone()
    {
            File directory = new File(getFilesDir(), filename + "D");
            if (directory.exists()) {
                File[] fs = directory.listFiles();
                for (File f : fs) {
                    f.delete();
                }
                directory.delete();
            }
    }

    private void PrepareVolley() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        String payload = "";
        final JSONObject js = new JSONObject();
        try {
            js.put("rainbow_name", ((EditText) findViewById(R.id.RainbowName)).getText().toString());
            js.put("description", ((EditText) findViewById(R.id.RainbowDesc)).getText().toString());
            js.put("latitude", String.valueOf(_coords.latitude));
            js.put("longitude", String.valueOf(_coords.longitude));
            if(photos!=null && photos.size()>0) {
                JSONArray p = new JSONArray();
                for (String se:photos
                     ) {
                    p.put(se);
                }

                js.put("photos", p);
            }
            js.put("user_id",Login_activity.userid);
            payload = js.toString();
            Log.d(TAG,payload);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }

        final String ur = AppApplication.baseurl + "/insert_rainbow";
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        JsonRequest<String> jsonObjReq = new JsonRequest<String>(
                Request.Method.POST, ur, payload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        ClearFilesFromPhone();
                        Intent d = new Intent();
                        d.putExtra("name",((EditText) findViewById(R.id.RainbowName)).getText().toString());
                        d.putExtra("lati",_coords.latitude);
                        d.putExtra("longi",_coords.longitude);
                        d.putExtra("id",Integer.parseInt(response));
                        setResult(RESULT_OK,d);
                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.getMessage());
                //ClearFilesFromPhone();

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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        ClearFilesFromPhone();
    }

    class BackgroundProcess extends AsyncTask<CreateRainbowFragment,Integer,Integer> {
        //private ProgressDialog progress;

        @Override
        protected Integer doInBackground(CreateRainbowFragment...arg){
                arg[0].StartUpload();
            return 1;

            // do your processing here like sending data or downloading etc.
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            //progress = ProgressDialog.show(YourActivity.this, "", "Wait...");
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            PrepareVolley();
            //if(progress!=null)
            //  progress.dismiss();
            //progress = null;
        }
    }

    }



