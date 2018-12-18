package su.zzz.locater;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocaterFragment extends Fragment {
    private static final String TAG = LocaterFragment.class.getSimpleName();
    private Switch mSwitch;
    private LocationManager mLocationManager;

    public static Fragment newInatance() {
        return new LocaterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_locater, container, false);
        mLocationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        mSwitch = v.findViewById(R.id.switcher);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG, "onCheckedChanged: " + String.valueOf(b));
//                Intent intent = new Intent(getActivity(), LocaterService.class);
//                PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 1, intent, 0);
                Intent intent = new Intent(getActivity(), LocaterReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (b) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "checkSelfPermission: -");
                        return;
                    }
//                    mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(new Criteria(), true), 1000, 1, pendingIntent);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, pendingIntent);
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, pendingIntent);
                    mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 0, pendingIntent);
                } else {
                    mLocationManager.removeUpdates(pendingIntent);
                }

            }
        });
        return v;
    }
}
