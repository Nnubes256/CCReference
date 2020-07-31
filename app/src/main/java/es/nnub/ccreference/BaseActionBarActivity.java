package es.nnub.ccreference;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.nnub.ccreference.activities.AboutActivity;
import es.nnub.ccreference.activities.UpdateDataActivity;
import es.nnub.ccreference.services.NotificationService;

abstract public class BaseActionBarActivity extends AppCompatActivity {

    protected final int updateRequestCode = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutItem:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.updateDataItem:
                Intent i = new Intent(this, UpdateDataActivity.class);
                startActivityForResult(i,
                        updateRequestCode,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == updateRequestCode) {
            if (resultCode == RESULT_OK) {
                boolean success = data.getBooleanExtra("success", false);
                if (success) {
                    Toast.makeText(this, R.string.data_updated_toast_text, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.data_updated_error_toast_text, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
