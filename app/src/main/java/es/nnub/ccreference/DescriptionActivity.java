package es.nnub.ccreference;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.nnub.ccreference.descriptions.BaseDescriptionAdapter;
import es.nnub.ccreference.descriptions.EnemyDescription;
import es.nnub.ccreference.descriptions.ItemDescription;
import es.nnub.ccreference.services.FloatingWidgetService;

public class DescriptionActivity extends BaseActionBarActivity {

    private Menu menu;
    private BaseDescriptionAdapter adapter;
    private String name;
    private Intent overlayIntent;
    private boolean isGoingBack = false;
    private boolean boundToFloatingWidget = false;
    private FloatingWidgetService floatingWidgetService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Bundle extras = getIntent().getExtras();
        String itemName = extras.getString("itemName");
        actionBar.setTitle(itemName);
        actionBar.setDisplayHomeAsUpEnabled(true);

        name = extras.getString("name");
        initalizeLayoutFromBaseResourceName(name);
        //fillDescriptionFields();
    }

    private FloatingWidgetService.FloatingWidgetCallbacks floatingWidgetCallbacks = new FloatingWidgetService.FloatingWidgetCallbacks() {
        @Override
        public MediaController onMediaControllerRequested() {
            return new MediaController(DescriptionActivity.this);
        }

        @Override
        public void openSearchLink(String query) {
            Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
            i.putExtra(SearchManager.QUERY, query);
            if (i.resolveActivity(DescriptionActivity.this.getPackageManager()) != null) {
                DescriptionActivity.this.startActivity(i);
            }
        }

        @Override
        public void onPressCloseButton() {
            floatingWidgetService.registerCallbacks(null); // unregister
            unbindService(serviceConnection);
            boundToFloatingWidget = false;
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FloatingWidgetService.FloatingWidgetBinder binder = (FloatingWidgetService.FloatingWidgetBinder) service;
            floatingWidgetService = binder.getService();
            boundToFloatingWidget = true;
            floatingWidgetService.registerCallbacks(floatingWidgetCallbacks);
            floatingWidgetService.ready();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundToFloatingWidget = false;
        }
    };

    @Override
    protected void onPause() {
        if (!isGoingBack && Settings.canDrawOverlays(this)) {
            overlayIntent = new Intent(this, FloatingWidgetService.class);
            overlayIntent.putExtra("name", name);
            bindService(overlayIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (boundToFloatingWidget) {
            floatingWidgetService.registerCallbacks(null); // unregister
            unbindService(serviceConnection);
            boundToFloatingWidget = false;
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        if (boundToFloatingWidget) {
            floatingWidgetService.registerCallbacks(null); // unregister
            unbindService(serviceConnection);
            boundToFloatingWidget = false;
        }
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        if (boundToFloatingWidget) {
            floatingWidgetService.registerCallbacks(null); // unregister
            unbindService(serviceConnection);
            boundToFloatingWidget = false;
        }
        super.onDestroy();
    }

    protected void initalizeLayoutFromBaseResourceName(String name) {
        BaseDescriptionAdapter descriptionAdapter;
        String contentType;
        String contentName;

        Pattern pattern = Pattern.compile("^([a-z]+)_([a-z_]+)$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.find()){
            MatchResult matchResult = matcher.toMatchResult();
            contentType = matchResult.group(1);
            contentName = matchResult.group(2);
        } else {
            throw new RuntimeException("WRONGLY CRAFTED LINK: " + name);
        }

        switch (contentType) {
            case "enemy":
                descriptionAdapter = new EnemyDescription(this);
                break;
            case "item":
                descriptionAdapter = new ItemDescription(this);
                break;
            default:
                throw new RuntimeException("UNKNOWN TYPE:" + contentType);
        }

        descriptionAdapter.loadResourcesFromName("desc_" + contentType + "_" + contentName);
        ScrollView baseView = findViewById(R.id.baseScrollView);
        descriptionAdapter.initializeView(this, baseView, null);
        adapter = descriptionAdapter;
    }

    protected void fillDescriptionFields() {
        ImageView imageView = findViewById(R.id.image1);
        TextView summaryView = findViewById(R.id.text_summary);
        TextView statsView = findViewById(R.id.text_stats);
        TextView descriptionView = findViewById(R.id.text_description);

        Bundle extras = getIntent().getExtras();
        String itemName = extras.getString("name");
        Resources res = getResources();

        Drawable image = res.getDrawable(
                res.getIdentifier("desc_" + itemName + "_icon", "drawable", getPackageName()),
                null
        );
        image.setFilterBitmap(false);
        int summary = res.getIdentifier("desc_" + itemName + "_summary", "string", getPackageName());
        int stats = res.getIdentifier("desc_" + itemName + "_stats", "string", getPackageName());
        int description = res.getIdentifier("desc_" + itemName + "_description", "string", getPackageName());

        imageView.setImageDrawable(image);
        summaryView.setText(summary);
        statsView.setText(stats);
        descriptionView.setText(description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (!adapter.addExtras(this, menu)) {
            return super.onCreateOptionsMenu(menu);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        isGoingBack = true;
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = adapter.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            isGoingBack = true;
        }

        if (!result) {
            return super.onOptionsItemSelected(item);
        } else {
            return true;
        }
    }
}
