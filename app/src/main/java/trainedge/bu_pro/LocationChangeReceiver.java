package trainedge.bu_pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LocationService.class);
        context.startService(i);
    }
}
