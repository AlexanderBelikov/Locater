package su.zzz.locater;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class DeviceLocationSettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = DeviceLocationSettingsActivity.class.getSimpleName();

    public static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private Switch switcherLocaterState;
    private Switch stateGoogleApiClient;
    private Switch stateReceiver;
    private static boolean activityVisible;
    private Snackbar snackbar;
    private boolean snackbarVisible = false;
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

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setMaxWaitTime(15000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        switcherLocaterState.setChecked(LocaterPreferences.getLocationReceiverState(this));

        if(LocaterPreferences.getLocationReceiverState(this)){
            checkPermissionsRequirement();
        }

        switcherLocaterState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
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
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSIONS:
                checkPermissionsRequirement();
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

//    GoogleApiClient.ConnectionCallbacks

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended: ");
    }

    //    GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: ");
    }
//    LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: ");
    }

    public static boolean hasLocationPermission(Context context) {
        boolean result = ((ActivityCompat.checkSelfPermission(context, LOCATION_PERMISSIONS[0])== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, LOCATION_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED));
        return result;
    }
    private void checkPermissionsRequirement(){
        if(LocaterPreferences.getLocationReceiverState(this) && !hasLocationPermission(this)){
            snackbar.show();
        }
    }
}
