package es.nnub.ccreference;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import es.nnub.ccreference.BaseActionBarActivity;
import es.nnub.ccreference.R;

public class CategoryActivity extends CustomListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Bundle extras = getIntent().getExtras();
        String itemName = extras.getString("itemName");
        actionBar.setTitle(itemName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        autoFillListViewWithPassedData();
    }
}
