package su.zzz.locater;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocaterService extends IntentService {
    private static final String TAG = LocaterService.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static Intent newIntent(Context context) {
        return new Intent(context, LocaterService.class);
    }
    public LocaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
        Map<String, Object> locationsMap = new HashMap<>();

        LocationResult result = LocationResult.extractResult(intent);
        String notificationText = "???:???";
        if(result != null){
            Location lastLocation = LocationResult.extractResult(intent).getLastLocation();
            if (lastLocation != null){
                notificationText = lastLocation.getLatitude()+" : "+lastLocation.getLongitude();
//                locationsMap.put(location.getTime(), new GeoPoint(location.getLatitude(),location.getLongitude()));
            }
            List<Location> locations = result.getLocations();
            for (Location location:locations){
                locationsMap.put(String.valueOf(location.getTime()), new GeoPoint(location.getLatitude(),location.getLongitude()));
            }
            Log.i(TAG, "onHandleIntent: size: "+result.getLocations().size());

        }
        if(locationsMap.isEmpty()){
            return;
        }
        Log.i(TAG, "notificationText: "+notificationText);
        locationsMap.put("device_model", Build.DISPLAY);
        try {
            FirebaseFirestore.getInstance()
                    .collection("admins").document(LocaterPreferences.getLocaterAdminUid(getApplicationContext()))
                    .collection("devices").document(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))
//                    .set(locationsMap, SetOptions.merge())
                    .set(locationsMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "onSuccess: ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: ");
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }
//        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(mUser == null){
//            Log.i(TAG, "onHandleIntent: mUser == null");
//        } else {
//            Log.i(TAG, "onHandleIntent: getUid: "+mUser.getUid());
//        }

        /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference deviceReference = db.collection("Devices").document(Build.DISPLAY);

        try {
            deviceReference.set(locationsMap, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "onSuccess: ");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: ");
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }
        */
                //.doc(user_id).set({foo:'bar'}, {merge: true})
//        DocumentReference deviceReference = db.collection("Devices").document("Locations");
//        deviceReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if(document.exists()){
//                        Log.i(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.i(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

//        Log.i(TAG, "ANDROID_ID: "+Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        Log.i(TAG, "MODEL: "+Build.MODEL);
//        Log.i(TAG, "BRAND: "+Build.BRAND);
//        Log.i(TAG, "DEVICE: "+Build.DEVICE);
//        Log.i(TAG, "ID: "+Build.ID);
//        Log.i(TAG, "MANUFACTURER: "+Build.MANUFACTURER);
//        Log.i(TAG, "DISPLAY: "+Build.DISPLAY);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    public static void setLocationUpdates(Context context, boolean isOn) {
        Log.i(TAG, "setLocationUpdates: "+isOn);
        Intent intent = LocaterService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        LocationRequest mLocationRequest;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
//        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(0);
//        mLocationRequest.setMaxWaitTime(15000);

        if(isOn){
            String adminUid = LocaterPreferences.getLocaterAdminUid(context);
            if(adminUid.isEmpty()){
                Log.i(TAG, "setLocationUpdates: adminUid.isEmpty");
                return;
            }
            try {
                if (!LocaterActivity.checkPermission(context)) {
                    Log.i(TAG, "checkSelfPermission: Fail");
                    return;
                }
                LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest,pendingIntent);
                LocaterPreferences.setLocationReceiverState(context, true);
            } catch (Exception e) {
                Log.e(TAG, "requestLocationUpdates: ", e);
            }
        } else {
            try {
                if (!LocaterActivity.checkPermission(context)) {
                    Log.i(TAG, "checkSelfPermission: Fail");
                    return;
                }
                LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(pendingIntent);
                LocaterPreferences.setLocationReceiverState(context, false);
            } catch (Exception e) {
                Log.e(TAG, "removeLocationUpdates: ", e);
            }
        }
    }
    public static void initAdmin(Context context) {
        Log.i(TAG, "initAdmin: ");
        Map<String, Object> adminDevices = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String adminUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference adminReference = db.collection("admins").document(adminUid);

        adminReference.set(adminDevices, SetOptions.merge());
//        DocumentReference deviceReference = adminReference.collection("devices").document(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
//        try {
//            deviceReference.set(new HashMap<>(), SetOptions.merge())
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.i(TAG, "onSuccess: ");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.i(TAG, "onFailure: ");
//                        }
//                    });
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: ", e);
//        }

    }
}
