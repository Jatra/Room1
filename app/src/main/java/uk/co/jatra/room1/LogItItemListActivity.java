package uk.co.jatra.room1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uk.co.jatra.room1.data.LogItDatabase;
import uk.co.jatra.room1.data.LogItItem;

public class LogItItemListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private LogItDatabase db;
    private List<LogItItem> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = LogItDatabase.getInstance(this);
        setContentView(R.layout.activity_logititem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> db.logitItemDAO().insert(new LogItItem("stuff", new Date())));

        RxView.clicks(fab)
                .observeOn(Schedulers.io())
                .subscribe(view -> db.logitItemDAO().insert(new LogItItem("stuff", new Date())));

        View recyclerView = findViewById(R.id.logititem_list);
        assert recyclerView != null;

        getItems()

                .subscribe(items -> {
                    this.itemList = items;
                    setupRecyclerView((RecyclerView) recyclerView, itemList);
                });


        if (findViewById(R.id.logititem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private Flowable<List<LogItItem>> getItems() {
        return db.logitItemDAO().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void showToast(Throwable e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<LogItItem> items) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<LogItItem> values;

        public SimpleItemRecyclerViewAdapter(List<LogItItem> items) {
            this.values = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.logititem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = values.get(position);
            holder.mIdView.setText(values.get(position).getDescription());
            holder.mContentView.setText(values.get(position).getLastLogged().toString());

            holder.mView.setOnClickListener(v -> {
                if (mTwoPane) {
                    displayDetail(holder);
                } else {
                    startDetailActivity(holder, v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return values.size();
        }

        private void startDetailActivity(ViewHolder holder, View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, LogItItemDetailActivity.class);
            intent.putExtra(LogItItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

            context.startActivity(intent);
        }

        private void displayDetail(ViewHolder holder) {
            Bundle arguments = new Bundle();
            arguments.putInt(LogItItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
            LogItItemDetailFragment fragment = new LogItItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.logititem_detail_container, fragment)
                    .commit();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public LogItItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
