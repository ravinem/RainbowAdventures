package com.example.mapwithmarker;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateRainbowFragment extends BaseActivity implements View.OnClickListener {

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
    private CircularProgressButton circularButton = null;
    ProgressDialog progressDialog;

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
            circularButton = (CircularProgressButton)findViewById(R.id.circularButton);
            circularButton.setIndeterminateProgressMode(true);
            filename = CreateRainbowFragment.df.format(_coords.latitude);
            addListenerOnButton();
            addListenerOnPicsButton();
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

    public void onCircularClick(View view){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading ....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        circularButton.setProgress(2);
        new BackgroundProcess().execute(this);
    }

    public void onClick(View view) {
        button.setText("Uploading...");
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

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotosTakenNumber++;
            String s = String.valueOf(currentPhotosTakenNumber)+ " "+
                        getResources().getString(R.string.number_image)
                        + " " + getResources().getString(R.string.maximum_image_allowed_number);
            tvNumberImages.setText(s);

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
            Rainbow r =new Rainbow();
            r.coords = _coords;
            r.Name = ((EditText)findViewById(R.id.RainbowName)).getText().toString();
            r.Description = ((EditText)findViewById(R.id.RainbowDesc)).getText().toString();
            r.numberPics = currentPhotosTakenNumber;
            r.photos = photos;
            Gson g = new Gson();
            String data = g.toJson(r);
            File f = new File(getBaseContext().getFilesDir(),filename);

            FileOutputStream outputStream;

            try {
                outputStream = new FileOutputStream(f);
                outputStream.write(data.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ClearFilesFromPhone();
            Intent d = new Intent();
            d.putExtra("lati",filename);
            setResult(RESULT_OK,d);
            finish();
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

                Integer userid = PrefSingleton.getInstance().readPreference("userid");
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
            //if(progress!=null)
            //  progress.dismiss();
            //progress = null;
        }
    }

    }



