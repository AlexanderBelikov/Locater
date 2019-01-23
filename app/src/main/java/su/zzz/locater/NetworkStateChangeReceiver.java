package su.zzz.locater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkStateChangeReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkStateChangeReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
    }
}
