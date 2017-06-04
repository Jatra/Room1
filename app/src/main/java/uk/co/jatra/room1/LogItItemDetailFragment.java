package uk.co.jatra.room1;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uk.co.jatra.room1.data.LogItDatabase;
import uk.co.jatra.room1.data.LogItItem;

public class LogItItemDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";

    private LogItItem item;
    private LogItDatabase db;
    private View rootView;
    private Disposable subscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = LogItDatabase.getInstance(this.getContext());

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            subscription = getItems(getArguments().getInt(ARG_ITEM_ID, 0))
                    .subscribe(items -> {
                        if (!items.isEmpty()) {
                            this.item = items.get(0);
                            setTitle(item);
                            showItem();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "No item from rx", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void deleteItem() {
        subscription.dispose();
        db.logitItemDAO().deleteById(item.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.logititem_detail, container, false);
        showItem();
        return rootView;
    }

    private Flowable<List<LogItItem>> getItems(int id) {
        return db.logitItemDAO().loadAllById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void setTitle(LogItItem item) {
        Activity activity = this.getActivity();
        if (activity != null) {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(item.getDescription());
            }
        }
    }

    private void showItem() {
        if (item != null && rootView != null) {
            ((TextView) rootView.findViewById(R.id.logititem_detail)).setText(item.getId() + ": " + item.getDescription() + " - " + item.getLastLogged());
        }
    }
}
