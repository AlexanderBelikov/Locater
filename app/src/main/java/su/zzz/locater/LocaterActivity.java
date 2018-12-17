package su.zzz.locater;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LocaterActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LocaterFragment.newInatance();
    }
}
