package es.nnub.ccreference.services;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import es.nnub.ccreference.MainActivity;
import es.nnub.ccreference.R;
import es.nnub.ccreference.activities.IndexActivity;

import static android.app.Notification.CATEGORY_STATUS;

public class NotificationService extends Service {

    private NotificationCompat.Builder notifBuilder;
    private Thread splashThread;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent(this, IndexActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("notificationUpdate", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        notifBuilder = new NotificationCompat.Builder(this, "updates")
                .setSmallIcon(R.drawable.ic_notif_icon)
                .setContentTitle("A resources update is available!")
                .setContentText("Press here to update the reference resources!")
                .setContentIntent(pendingIntent)
                .addAction(0, "Update", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(CATEGORY_STATUS)
                .setAutoCancel(true)
                .setLights(Color.argb(1, 0x37, 0x59, 0xD0), 200, 200);

        this.splashThread = new Thread() {
            public void run() {
                try {
                    sleep(30000);
                } catch (InterruptedException e) {} finally {
                    showNotification();
                    NotificationService.this.stopSelf();
                }
            }
        };
        splashThread.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        splashThread.interrupt();
        super.onDestroy();
    }

    public void showNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notifBuilder.build());
    }
}
