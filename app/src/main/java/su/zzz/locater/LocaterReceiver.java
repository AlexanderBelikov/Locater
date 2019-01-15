package su.zzz.locater;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.util.Date;

public class LocaterReceiver extends BroadcastReceiver {
    private static final String TAG = LocaterReceiver.class.getSimpleName();
    static final String ACTION_LOCATION_UPDATE ="su.zzz.locater.action.LOCATION_UPDATE";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        LocationResult result = LocationResult.extractResult(intent);
        String notificationText = "???:???";
        if(result != null){
            Location location = LocationResult.extractResult(intent).getLastLocation();
            if (location != null){
                notificationText = location.getLatitude()+" : "+location.getLongitude();
            }
        }

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Locater")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle(DateFormat.getDateTimeInstance().format(new Date()))
                .setContentText(notificationText)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0, notification);
    }
}
