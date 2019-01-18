package su.zzz.locater;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LocaterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = LocaterActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locater);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_name);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if(mUser!=null){
            TextView viewUserName = mNavigationView.getHeaderView(0).findViewById(R.id.userName);
            viewUserName.setText(mUser.getEmail());
            ConnectDevice();
        }
    }

    private void ConnectDevice() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceAndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = Build.ID+"-"+deviceAndroidId;

        CollectionReference refDevices = db.collection(deviceId);
        Map<String, Object> deviceSettings = new HashMap<>();
        deviceSettings.put("manager", mUser.getUid());
        deviceSettings.put("device_id", deviceAndroidId);

        try {
            refDevices.document("settings").set(deviceSettings, SetOptions.merge())
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

        Map<String, Object> deviceLocations = new HashMap<>();
//        deviceLocations.put("location", "zzz");

        try {
            refDevices.document("locations").set(deviceLocations, SetOptions.merge())
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
    }

    private void OnFirebaseAuth() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference refDevices = db.collection("Devices");
        refDevices.whereEqualTo("Manager", mUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onComplete: "+task.isSuccessful());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
//        DocumentReference deviceReference = db.collection("Devices").document(Build.DISPLAY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(mUser==null){
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.sign_out : {
                mAuth.signOut();
                mUser = null;
            }
            default: {

            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
