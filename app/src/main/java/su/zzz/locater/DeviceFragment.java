package su.zzz.locater;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Map;

public class DeviceFragment extends SupportMapFragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    public static final String ARG_DEVICE_ID = "device_id";
    private TextView mTextViewDeviceId;
    private String deviceId;
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Location mDeviceLocation;
    private LatLng deviceLatLng;
    private MarkerOptions deviceMarker;
    private Map<String, Object> deviceInfo;
    private ListenerRegistration mListenerRegistration;


    public static DeviceFragment newInstance(String deviceId){
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_ID, deviceId);
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        deviceId = getArguments().getString(ARG_DEVICE_ID);
        mDeviceLocation = new Location("");
        deviceLatLng = new LatLng(0,0);
        deviceMarker = new MarkerOptions();
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                showDevice();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();

        mListenerRegistration = FirebaseFirestore.getInstance()
                .collection("admins").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("devices").document(deviceId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        Log.i(TAG, "onEvent: ");
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Log.d(TAG, "Current data: " + documentSnapshot.getData());
                            deviceInfo = documentSnapshot.getData();
                            updateUI();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }

                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        mListenerRegistration.remove();
        mClient.disconnect();
    }
    //    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_device, container, false);
//        mTextViewDeviceId = v.findViewById(R.id.device_id);
//        mTextViewDeviceId.setText(deviceId);
//        return v;
//    }

    private void updateUI(){
        if(mMap == null){
            return;
        }
        GeoPoint geoPoint = (GeoPoint)deviceInfo.get("last_location_point");
        LatLng deviceLatLng = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
        float zoom = 17;
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(deviceLatLng, zoom);
        MarkerOptions deviceMarker = new MarkerOptions().position(deviceLatLng);
        mMap.clear();
        mMap.addMarker(deviceMarker);
        mMap.animateCamera(update);
    }
    private void showDevice(){
        if(mMap == null || deviceId.isEmpty()){
            return;
        }
        FirebaseFirestore.getInstance()
                .collection("admins").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("devices").document(deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                deviceInfo = document.getData();
                                updateUI();
//                                mDeviceLastGeoPoint = document.getData().get("last_location_point");
//                                document.getData().get("last_location_point").
//                                Log.i(TAG, "onCompletex: "+ document.getData().get("last_location_point"));
//                                GeoPoint mDeviceGeoPoint = document.getData().get("last_location_point");
//                                Timestamp mDeviceGeoPoint
//                                mDeviceLocation.setLatitude(document.getData().get("last_location_point"));
//                                mDeviceLocation.setLongitude();
//                                Log.i(TAG, "onCompletex: "+ document.getDate("device_model"));
                            } else {
                                Log.i(TAG, "onComplete: document.exists false");
                            }
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }
}
