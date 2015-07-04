package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());

            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, detailActivityFragment)
                    .commit();
        }

    }
}
