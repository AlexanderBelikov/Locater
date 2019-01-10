package su.zzz.locater;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class DeviceLocationSettingsActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = DeviceLocationSettingsActivity.class.getSimpleName();

    public static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 1606;
    private Switch switcherLocaterState;
    private boolean LocaterState;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_location_settings);

        switcherLocaterState = findViewById(R.id.switcherLocaterState);
        LocaterState = LocaterPreferences.getLocationReceiverState(this);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "onLocationChanged: ");
            }

        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        switcherLocaterState.setChecked(LocaterPreferences.getLocationReceiverState(this));
        switcherLocaterState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                LocaterState = state;
                changeLocaterState();
            }
        });
        switcherLocaterState.setClickable(false);
        if(checkPermission()){
            createGoogleApiClient();
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    createGoogleApiClient();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //    LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: ");
    }

    private boolean checkPermission() {
        boolean result = ((ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED));
        return result;
    }

    private void requestPermissions() {
        Log.i(TAG, "requestPermissions: ");
        boolean shouldShowRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSIONS[0]);
        if(shouldShowRequestPermission){
            Snackbar.make(findViewById(R.id.DeviceLocationSettingsLayout), "Location permissions", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Allow", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                        }
                    })
                    .show();
        } else {
            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    private void createGoogleApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i(TAG, "onConnected: ");
                        switcherLocaterState.setClickable(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i(TAG, "onConnectionSuspended: ");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i(TAG, "onConnectionFailed: ");
                    }
                })
                .build();
        createLocationRequest();
        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setMaxWaitTime(15000);
    }

    private void requestLocationUpdates() {
        Log.i(TAG, "requestLocationUpdates: ");
        try {
            if (!checkPermission()) {
                Log.i(TAG, "requestLocationUpdates: checkSelfPermission: Fail");
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getPendingIntent());
            LocaterPreferences.setLocationReceiverState(this, true);
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdates: ", e);
        }
    }
    private void removeLocationUpdates(){
        Log.i(TAG, "removeLocationUpdates: ");
        try {
            if (!checkPermission()) {
                Log.i(TAG, "removeLocationUpdates: checkSelfPermission: Fail");
                return;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getPendingIntent());
            LocaterPreferences.setLocationReceiverState(this, false);
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdates: ", e);
        }
    }
    private void changeLocaterState(){
        if(LocaterState == LocaterPreferences.getLocationReceiverState(this)){
            Log.i(TAG, "changeLocaterState: LocaterState == LocaterPreferences");
            return;
        }
        if (LocaterState) {
            requestLocationUpdates();
        } else {
            removeLocationUpdates();
        }
    }
    private PendingIntent getPendingIntent() {
        Intent intent = LocaterService.newIntent(this);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
