package su.zzz.locater;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

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
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
