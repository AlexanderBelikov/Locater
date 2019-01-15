package su.zzz.locater;

import android.content.Context;
import android.preference.PreferenceManager;

public class LocaterPreferences {
    private static final String TAG = LocaterPreferences.class.getSimpleName();
    private static final String PREF_LOCATION_RECEIVER_STATE = "location_receiver_state";

    public static boolean getLocationReceiverState(Context context){
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LOCATION_RECEIVER_STATE, false);
        return enabled;
    }
    public static void setLocationReceiverState(Context context, boolean enabled){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_LOCATION_RECEIVER_STATE, enabled)
                .apply();
    }

}
