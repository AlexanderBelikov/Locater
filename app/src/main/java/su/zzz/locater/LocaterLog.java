package su.zzz.locater;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocaterLog {
    private static final String TAG = LocaterLog.class.getSimpleName();
    private static LocaterLog sLocaterLog;
    private List<String> mList;
    private LocaterLog(Context context){
        mList = new ArrayList<>();
    }
    public List<String> getLogs(){
        return mList;
    }

    public void addLog(String log) {
        mList.add(log);
    }
    public void clearLog() {
        mList.clear();
    }

    public static LocaterLog get(Context context){
        if(sLocaterLog==null){
            sLocaterLog = new LocaterLog(context);
        }
        return sLocaterLog;
    }
}
