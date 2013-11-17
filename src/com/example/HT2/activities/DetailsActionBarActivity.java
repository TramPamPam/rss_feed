package com.example.HT2.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.example.HT2.R;

public class DetailsActionBarActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) && (getResources().getBoolean(R.bool.isTablet))) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            DetailsFragment details = new DetailsFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.holder, details).commit();
        }

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
//                                Intent homeIntent = new Intent(this, MainActionBarActivity.class);
//                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(homeIntent);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
