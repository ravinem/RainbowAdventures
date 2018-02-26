package com.example.mapwithmarker;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import android.Manifest;

import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rainbowadventures.utilities.GetCurrentLocationHelper;
import com.rainbowadventures.utilities.MySingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends BaseActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener
        , GoogleMap.OnInfoWindowClickListener, AddressListDialogFragement.NoticeDialogListener
        , GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    public static final String TAG = "MapsMarkerActivity";
    private GoogleMap _googleMap;
    private static LatLng location = null;
    private Rainbow currRainbow;
    private List<Address> locs;
    private Locale current;
    private final int mapsZoom = 15;
    private FusedLocationProviderClient mFusedLocationClient;
    private RequestQueue queue;
    private boolean isSearch = false;
    private GetCurrentLocationHelper currentlocationhelperObject = new GetCurrentLocationHelper(this, mFusedLocationClient);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            isSearch = true;
            String query = intent.getStringExtra(SearchManager.QUERY);
            try {
                if (current == null) {
                    current = getResources().getConfiguration().locale;
                }
                if(com.rainbowadventures.utilities.GetCurrentLocationHelper.IsInternetConnectivityAvailable(getBaseContext()))
                {
                    PrepareVolley(query,current);
                }
                else {
                    Geocoder geocoder = new Geocoder(getBaseContext(), current);
                    locs = geocoder.getFromLocationName(query, 5);
                    ShowLocationThatsSearched();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public void onDialogItemClick(DialogFragment dialog) {
        Integer i = AddressListDialogFragement.index;
        LatLng ll = new LatLng(locs.get(i).getLatitude(), locs.get(i).getLongitude());
        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, mapsZoom));
        location = null;
        dialog.dismiss();
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
try {
    _googleMap = googleMap;
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
        _googleMap.setMyLocationEnabled(true);
    } else {
        // Show rationale and request permission.
        currentlocationhelperObject.requestPermissions();
    }

    UiSettings u = _googleMap.getUiSettings();
    u.setZoomControlsEnabled(true);

    _googleMap.setOnMapClickListener(this);
    _googleMap.setOnInfoWindowClickListener(this);
    _googleMap.setOnMarkerClickListener(this);
    // getFilesForMarkers();
    String id = Login_activity.UserId;
    PrepareVolleyAllRainbows(AppApplication.baseurl + "/getallRainbowbyUserId?user_id="+id);
    if (location != null) {
        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mapsZoom));
        location = null;
    }
}
catch(Exception e)
{
    e.printStackTrace();
    throw e;
}
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        getLastLocation();
        return false;
    }

    private void prepareNewlyAddedRainbow(Rainbow r) {


            LatLng c = new LatLng(r.latitude,r.longitude);
            _googleMap.addMarker(new MarkerOptions().position(c)
                    .title(r.rainbow_name)).setSnippet(String.valueOf(r.id));
    }

    @Override
    public void onMapClick(LatLng coords) {
        final LatLng _coords = coords;
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setTitle(R.string.alert_dialog_title);
        aBuilder.setMessage(R.string.alert_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogInterface.dismiss();
                        try {
                            Intent intent = new Intent(getBaseContext(), CreateRainbowFragment.class);
                            //CreateRainbowFragment f = new CreateRainbowFragment();
                            intent.putExtra("lati", _coords.latitude);
                            intent.putExtra("longi", _coords.longitude);

                            startActivityForResult(intent, 90);
                        } catch (Exception e) {
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            return true;
        }

        //String Filename = CreateRainbowFragment.df.format(marker.getPosition().latitude);
        //currRainbow = getFilesForMarkers(Filename);
        //marker.setTitle(currRainbow.rainbow_name);
        marker.showInfoWindow();
        return marker.isInfoWindowShown();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        try {
            //String Filename = CreateRainbowFragment.df.format(marker.getPosition().latitude);
            //Rainbow r = getFilesForMarkers(Filename);
            Intent intent = new Intent(getBaseContext(), ShowRainbowActivity.class);
            //ShowRainbowActivity f = new ShowRainbowActivity();
            //intent.putExtra("name", marker.getTitle());
            intent.putExtra("id", Integer.parseInt(marker.getSnippet()));
            //intent.putExtra("lati", marker.getPosition().latitude);
            startActivityForResult(intent, 80);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 90) {
            if (resultCode == Activity.RESULT_OK) {
                Rainbow r = new Rainbow();
                r.latitude = data.getDoubleExtra("lati",0);
                r.longitude = data.getDoubleExtra("longi",0);
                r.rainbow_name = data.getStringExtra("name");
                r.id = data.getIntExtra("id",0);
                prepareNewlyAddedRainbow(r);
                isSearch = true;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 80) {
            if (resultCode == Activity.RESULT_OK) {
                data.getIntExtra("id",0);

                PrepareVolleyAllRainbows(AppApplication.baseurl + "/getallRainbowbyUserId?user_id="+Login_activity.UserId);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == GetCurrentLocationHelper.REQUEST_PERMISSIONS_REQUEST_CODE) {

            if (grantResults.length <= 0) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    _googleMap.setMyLocationEnabled(false);
                    //return;
                }
                currentlocationhelperObject.showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }}

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                           //Location mLastLocation = task.getResult();
                            //LatLng location= new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                            _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,mapsZoom));
                            /*_googleMap.addCircle(new CircleOptions()
                                    .center(location)
                                    .clickable(false)
                                    .fillColor(Color.CYAN)
                                    .strokeColor(Color.BLUE)
                                    .radius(100)
                                    .visible(true));*/
                        } else {
                           currentlocationhelperObject.showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    private void ShowLocationThatsSearched()
    {
        if (locs != null && !locs.isEmpty()) {
            if (locs.size() == 1) {
                LatLng ll = new LatLng(locs.get(0).getLatitude(), locs.get(0).getLongitude());
                location = ll;
            } else {
                CharSequence[] addrs = new CharSequence[locs.size()];
                for (int i = 0; i < locs.size(); i++) {
                    addrs[i] = locs.get(i).getFeatureName();
                }
                Bundle b = new Bundle();
                b.putCharSequenceArray("locations", addrs);
                DialogFragment df = new AddressListDialogFragement();
                df.setArguments(b);
                df.show(getFragmentManager(), "");
            }
        }
    }

    private void PrepareVolley(String query,final Locale locale)
    {
        if(query.isEmpty())
        {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        String searchKey = getResources().getString(R.string.googlePlaceKeySearch);
        query = query.replace(' ','+');
        String searchUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="+query+"&key="+searchKey;
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, searchUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        locs = com.rainbowadventures.utilities.GetCurrentLocationHelper.ConvertLocationJsonToAddress(response,locale);
                        ShowLocationThatsSearched();
                        if (location != null) {
                            _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mapsZoom));
                            location = null;
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

    private void PrepareVolleyAllRainbows(final String query)
    {
        if(query.isEmpty())
        {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        queue =MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        _googleMap.clear();
                        Gson g= new Gson();
                        Type listType = new TypeToken<ArrayList<Rainbow>>(){}.getType();
                        List<Rainbow> rs = g.fromJson(response,listType);
                        for (Rainbow r :rs) {
                            LatLng c = new LatLng(r.latitude,r.longitude);
                            _googleMap.addMarker(new MarkerOptions().position(c)
                                  .title(r.rainbow_name).snippet(String.valueOf(r.id)));
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
