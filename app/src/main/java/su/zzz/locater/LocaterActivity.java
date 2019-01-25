package su.zzz.locater;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class LocaterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = LocaterActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private NavigationView mNavigationView;
    private Menu mNavigationViewMenu;
    private Switch locaterState;
    private Map<Integer, String> itemsDeviceId;
    public static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 1059;
    public static boolean activityVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locater);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locaterState = toolbar.findViewById(R.id.locater_state);
        locaterState.setChecked(LocaterPreferences.getLocationReceiverState(this));
        locaterState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LocaterService.setLocationUpdates(compoundButton.getContext(), b);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_name);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationViewMenu = mNavigationView.getMenu();
        itemsDeviceId = new HashMap<>();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if(mUser!=null) {
            updateUI();
        }

        if(!LocaterPreferences.getLocaterAdminUid(getApplicationContext()).isEmpty() && LocaterPreferences.getLocationReceiverState(getApplicationContext())){
            if(!checkPermission(this)){
                requestPermissions();
            } else {
                LocaterService.setLocationUpdates(getApplicationContext(), true);
            }
        }
//        LocaterService.setLocationUpdates(getApplicationContext(), true);
    }

    private void updateUI() {
        mNavigationViewMenu.findItem(R.id.user_email).setTitle(mUser.getEmail());
        FirebaseFirestore.getInstance()
                .collection("admins").document(mUser.getUid())
                .collection("devices")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mNavigationViewMenu.removeGroup(R.id.devices_group);
                            itemsDeviceId.clear();
                            int itemDeviceId = 100;
                            for(QueryDocumentSnapshot document: task.getResult()){
                                itemDeviceId++;
                                itemsDeviceId.put(itemDeviceId, document.getId());
                                if(document.getId().equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))){
                                    mNavigationViewMenu.add(R.id.devices_group, itemDeviceId, 0, document.get("device_model").toString()).setIcon(R.drawable.ic_main_phone);

                                } else {
                                    mNavigationViewMenu.add(R.id.devices_group, itemDeviceId, 1, document.get("device_model").toString()).setIcon(R.drawable.ic_phone);
                                }
                            }
                        } else {
                            Log.e(TAG, "OnCompleteListener: ", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityVisible = false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        activityVisible = true;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    private void requestPermissions() {
        Log.i(TAG, "requestPermissions: ");
        boolean shouldShowRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSIONS[0]);
        if(shouldShowRequestPermission){
            Snackbar.make(findViewById(R.id.drawer_layout), "Location permissions", Snackbar.LENGTH_INDEFINITE)
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
            case R.id.user_email : {
                return true;
            }
            default: {
                String deviceId = itemsDeviceId.get(menuItem.getItemId());
                Log.i(TAG, "onNavigationItemSelected: "+deviceId);
                if(!deviceId.isEmpty()){
                    onDeviceSelected(deviceId);
                }

            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onDeviceSelected(String deviceId) {
        Fragment fragment = DeviceFragment.newInstance(deviceId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public static boolean checkPermission(Context context) {
        boolean result = ((ActivityCompat.checkSelfPermission(context, LOCATION_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, LOCATION_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED));
        return result;
    }
}
