package uk.co.jatra.room1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.schedulers.Schedulers;

public class LogItItemDetailActivity extends AppCompatActivity {

    private final String TAG = "TAG";
    private int itemId;
    private LogItItemDetailFragment fragment;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, LogItItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logititem_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RxView.clicks(fab)
                .observeOn(Schedulers.io())
                .subscribe(this::deleteItem);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            itemId = getIntent().getIntExtra(LogItItemDetailFragment.ARG_ITEM_ID, 1);
            arguments.putInt(LogItItemDetailFragment.ARG_ITEM_ID,
                    itemId);
            fragment = new LogItItemDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.logititem_detail_container, fragment, TAG)
                    .commit();
        }
    }

    private void deleteItem(Object unused) {
        fragment.deleteItem();
        finish();
    }
}
