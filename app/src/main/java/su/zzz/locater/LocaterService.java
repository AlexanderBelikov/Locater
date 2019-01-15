package su.zzz.locater;

import android.app.IntentService;
import android.app.Notification;
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

import com.google.android.gms.location.LocationResult;
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
            Location location = LocationResult.extractResult(intent).getLastLocation();
            if (location != null){
                notificationText = location.getLatitude()+" : "+location.getLongitude();
                locationsMap.put(String.valueOf(location.getTime()), new GeoPoint(location.getLatitude(),location.getLongitude()));
            }
        }
        Log.i(TAG, "notificationText: "+notificationText);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Locater")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle(DateFormat.getDateTimeInstance().format(new Date()))
                .setContentText(notificationText)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, notification);

//        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//        if(mUser == null){
//            Log.i(TAG, "onHandleIntent: mUser == null");
//        } else {
//            Log.i(TAG, "onHandleIntent: getUid: "+mUser.getUid());
//        }
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

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
