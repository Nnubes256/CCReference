package es.nnub.ccreference.activities;

import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.nnub.ccreference.BaseActionBarActivity;
import es.nnub.ccreference.CategoryActivity;
import es.nnub.ccreference.CustomArrayAdapter;
import es.nnub.ccreference.CustomListActivity;
import es.nnub.ccreference.MainActivity;
import es.nnub.ccreference.R;

public class IndexActivity extends CustomListActivity {

    private boolean isLaunchingActivityAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fillListView(R.array.index_categories, R.array.index_categories_icons, R.array.index_categories_links);

        if (!Settings.canDrawOverlays(this)) {
            askPermission();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean doNotificationUpdate = extras.getBoolean("notificationUpdate", false);

            if (doNotificationUpdate && !isLaunchingActivityAlready) {
                isLaunchingActivityAlready = true;
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(IndexActivity.this, UpdateDataActivity.class);
                        startActivityForResult(i, updateRequestCode);
                    }
                });
            }
        }
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 2084);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == updateRequestCode) {
            isLaunchingActivityAlready = false;
        }
    }
}
