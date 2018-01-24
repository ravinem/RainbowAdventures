package com.rainbowadventures.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.example.mapwithmarker.BuildConfig;
import com.example.mapwithmarker.MapsMarkerActivity;
import com.example.mapwithmarker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by ravis on 18-01-2018.
 */

public class GetCurrentLocationHelper  {

    private Activity _ctxt;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

    public GetCurrentLocationHelper(Activity ctxt,FusedLocationProviderClient fusedLocation)
    {
        _ctxt = ctxt;
        mFusedLocationClient = fusedLocation;
    }

    public void showSnackbar(final String text) {
        View container = _ctxt.findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    public void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(_ctxt.findViewById(android.R.id.content),
                _ctxt.getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(_ctxt.getString(actionStringId), listener).show();
    }

    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(_ctxt,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(_ctxt,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(_ctxt,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startLocationPermissionRequest();
                        }
                    });
        } else {
            startLocationPermissionRequest();
        }
    }


}
