package su.zzz.locater;

import android.content.Context;
import android.preference.PreferenceManager;

public class LocaterPreferences {
    private static final String TAG = LocaterPreferences.class.getSimpleName();
    private static final String PREF_LOCATION_RECEIVER_STATE = "location_receiver_state";
    private static final String PREF_LOCATER_STATE = "locater_state";
    private static final String PREF_LOCATION_REQUEST_STATE = "location_request_state";
    private static final String PREF_LOCATER_ADMIN_UID = "locater_admin_uid";

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

    public static boolean getLocaterState(Context context){
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LOCATER_STATE, false);
        return enabled;
    }
    public static void setLocaterState(Context context, boolean enabled){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_LOCATER_STATE, enabled)
                .apply();
    }

    public static boolean getLocationRequestState(Context context){
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_LOCATION_REQUEST_STATE, false);
        return enabled;
    }
    public static void setLocationRequestState(Context context, boolean enabled){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_LOCATION_REQUEST_STATE, enabled)
                .apply();
    }

    public static String getLocaterAdminUid(Context context){
        String uid = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LOCATER_ADMIN_UID, "");
        return uid;
    }
    public static void setLocaterAdminUid(Context context, String  uid){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LOCATER_ADMIN_UID, uid)
                .apply();
    }
}
