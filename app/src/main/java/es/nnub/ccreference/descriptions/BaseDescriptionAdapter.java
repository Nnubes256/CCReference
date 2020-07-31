package es.nnub.ccreference.descriptions;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import es.nnub.ccreference.services.FloatingWidgetService;

public abstract class BaseDescriptionAdapter {
    private View view;
    private Context context;

    public BaseDescriptionAdapter(@NonNull Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    public View getView() {
        return view;
    }

    abstract public void loadResourcesFromName(String resourceName);
    abstract public View initializeView(
            @NonNull Context context,
            @NonNull ViewGroup container,
            FloatingWidgetService.FloatingWidgetCallbacks floatingWidgetCallbacks);

    public boolean addExtras(@NonNull AppCompatActivity activity, Menu menu) {
        return false;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
