package trainedge.bu_pro;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class ProfileNotification {
    private static final String NOTIFICATION_TAG = "Profile";

    public static void notify(final Context context,
                              final String exampleString) {
        final Resources res = context.getResources();
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.logo1);
        final String ticker = exampleString;
        final String title = "Profile manager active";
        final String text = "Your app is running.";
        Intent pi = new Intent(context, SplashActivity.class);
        pi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR)
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setContentIntent(PendingIntent.getActivity(
                        context,
                        0,
                        pi,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
