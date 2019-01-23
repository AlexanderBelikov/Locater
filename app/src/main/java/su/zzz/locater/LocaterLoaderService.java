package su.zzz.locater;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class LocaterLoaderService extends IntentService {
    private static final String TAG = LocaterLoaderService.class.getSimpleName();
//    public static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);
    public static final long POLL_INTERVAL_MS = TimeUnit.SECONDS.toMillis(15);
    public LocaterLoaderService() {
        super(TAG);
    }
    public static Intent newIntent(Context context){
        Log.i(TAG, "newIntent: ");
        return new Intent(context, LocaterLoaderService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
    }

    public static void setServiceAlarm(Context context) {
        Log.i(TAG, "setServiceAlarm: ");
        Intent intent = LocaterLoaderService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pendingIntent);
//        alarmManager.cancel(pendingIntent);
//        pendingIntent.cancel();
    }

}
