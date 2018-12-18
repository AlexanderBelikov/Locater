package su.zzz.locater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocaterReceiver extends BroadcastReceiver {
    private static final String TAG = LocaterReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
    }

}
