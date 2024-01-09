package net.yageek.strasbourgpark.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.yageek.strasbourgpark.common.R;
import net.yageek.strasbourgpark.adapters.ParkingAdapter;
import net.yageek.strasbourgparkcommon.ParkingResult;
import net.yageek.strasbourgpark.viewmodel.ParkingModel;
import net.yageek.strasbourgpark.views.LoadingView;
import net.yageek.strasbourgpark.vo.DownloadResult;

public class ParkingListFragment extends Fragment implements ParkingAdapter.OnParkingResultSelected {

    public static final String TAG = "ParkingList";

    private ParkingAdapter adapter;
    private RecyclerView recyclerView;
    private LoadingView loadingView;
    private TextView noItemTextView;
    private TextView lastRefreshText;

    private ParkingModel parkingModel;

    private ParkingAdapter.OnParkingResultSelected listener;

    public void setListener(ParkingAdapter.OnParkingResultSelected listener) {
        this.listener = listener;
    }

    //region Fragment life cycle
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parking_list_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.parking_list_view);
        loadingView = rootView.findViewById(R.id.loading_view);
        noItemTextView = rootView.findViewById(R.id.parking_list_view_no_item);
        lastRefreshText = rootView.findViewById(R.id.parking_last_refresh_text);

        adapter = new ParkingAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setHasOptionsMenu(true);

        parkingModel = ViewModelProviders.of(getActivity()).get(ParkingModel.class);

        updateContents();

        parkingModel.getDownloadStatus().observe(this, new Observer<DownloadResult>() {
            @Override
            public void onChanged(@Nullable final DownloadResult downloadResult) {
                switch(downloadResult.status) {
                    case Error:

                        uiRefreshDelay(new Runnable() {
                            @Override
                            public void run() {
                                showError();
                            }
                        });

                        break;
                    case Loading:
                        setLoading(true);
                        break;
                    case Success:
                        uiRefreshDelay(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setResults(downloadResult.results);
                                lastRefreshText.setText(downloadResult.lastRefreshTime);
                                setLoading(false);
                            }
                        });
                        break;
                }
            }
        });
        adapter.setListener(this);
        return rootView;
    }

    private void uiRefreshDelay(Runnable r) {

        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(r, 800);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(adapter.getItemCount() < 1) {
            parkingModel.fetchData();
        }
    }
    //endregion

    //region Menu

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem refreshData = menu.findItem(R.id.refresh_data);

        DownloadResult result = parkingModel.getDownloadStatus().getValue();
        Boolean isDownloading = result == null ? false : result.status == DownloadResult.Status.Loading;

        if(isDownloading) {
            refreshData.setEnabled(false);
            refreshData.getIcon().setAlpha(50);
        } else {
            refreshData.setEnabled(true);
            refreshData.getIcon().setAlpha(255);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate main menu
        inflater.inflate(R.menu.list_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortby_name:
                adapter.setComparator(ParkingResult.Comparators.ByName);
                return true;
            case R.id.sortby_free_places:
                adapter.setComparator(ParkingResult.Comparators.ByFreePlaces);
                return true;
            case R.id.sortby_fillingrate:
                adapter.setComparator(ParkingResult.Comparators.ByFillingRate);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion
    private void setLoading(boolean isLoading) {

        if(isLoading) {
            recyclerView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            noItemTextView.setVisibility(View.INVISIBLE);
            lastRefreshText.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
            updateContents();
        }
        getActivity().invalidateOptionsMenu();
    }

    private void showError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.error_message);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            parkingModel.fetchData();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setLoading(false);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                setLoading(false);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateContents() {

        if(adapter.getItemCount() < 1) {
            noItemTextView.setVisibility(View.VISIBLE);
            lastRefreshText.setVisibility(View.GONE);
        } else {
            noItemTextView.setVisibility(View.INVISIBLE);
            lastRefreshText.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onParkingResultSelected(ParkingResult result) {
        if(listener != null) {
            listener.onParkingResultSelected(result);
        }
    }
}
