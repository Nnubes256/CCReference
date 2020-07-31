package es.nnub.ccreference.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import es.nnub.ccreference.BaseActionBarActivity;
import es.nnub.ccreference.R;

public class AboutActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("About");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button homepageButton = findViewById(R.id.homepageButton);
        homepageButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AboutActivity.this, WebViewActivity.class);
                i.putExtra("title", "Homepage");
                i.putExtra("url", "https://nnub.es");
                startActivity(i);
            }
        });

        Button aboutButton = findViewById(R.id.emailButton);
        aboutButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"nnubes256@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Support inquiry for CCReference");
                i.putExtra(Intent.EXTRA_TEXT, "Dear Ignacio,\n\n");
                i.setType("message/rfc822");
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void goToWiki(View view) {
        Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("title", "CrossCode Wiki");
        i.putExtra("url", "https://crosscode.gamepedia.com/CrossCode_Wiki");
        startActivity(i);
    }
}
