package es.nnub.ccreference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Window;
import android.widget.Toast;

import es.nnub.ccreference.activities.IndexActivity;
import es.nnub.ccreference.activities.UpdateDataActivity;
import es.nnub.ccreference.services.NotificationService;

public class MainActivity extends AppCompatActivity {

    final int updateScreenCode = 1;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CCReference";
            String description = "CCReference";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("updates", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade(Fade.OUT));

        createNotificationChannel();

        ActivityManager.TaskDescription taskDescription;

        if (Build.VERSION.SDK_INT >= 28) {
            taskDescription = new ActivityManager.TaskDescription(null, R.drawable.ic_launcher_round);
        } else {
            taskDescription = new ActivityManager.TaskDescription(
                    null,
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round));
        }
        setTaskDescription(taskDescription);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent notifIntent = new Intent(this, NotificationService.class);
        startService(notifIntent);
        super.onPause();

        Thread splashThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Intent i = new Intent(MainActivity.this, IndexActivity.class);
                            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                        }
                    });
                }
            }
        };
        splashThread.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            ActivityManager.TaskDescription taskDescription;
            if (Build.VERSION.SDK_INT >= 28) {
                taskDescription = new ActivityManager.TaskDescription(null, R.drawable.ic_launcher_round);
            } else {
                taskDescription = new ActivityManager.TaskDescription(
                        null,
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round));
            }
            setTaskDescription(taskDescription);
        }
    }
}
