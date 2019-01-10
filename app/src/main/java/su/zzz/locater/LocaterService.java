package su.zzz.locater;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class LocaterService extends IntentService {
    private static final String TAG = LocaterService.class.getSimpleName();
    public static Intent newIntent(Context context) {
        return new Intent(context, LocaterService.class);
    }
    public LocaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("New car seat price")
                .setSmallIcon(android.R.drawable.star_big_on)
                .setContentTitle("New car seat price")
                .setContentText("Last car seat price"+DateFormat.getDateTimeInstance().format(new Date()))
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
