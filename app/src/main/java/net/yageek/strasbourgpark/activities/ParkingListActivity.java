package net.yageek.strasbourgpark.activities;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.adapters.ParkingAdapter;
import net.yageek.strasbourgpark.adapters.ParkingResult;
import net.yageek.strasbourgpark.views.LoadingView;
import net.yageek.strasbourgparkapi.APIClient;
import net.yageek.strasbourgparkapi.Parking;
import net.yageek.strasbourgparkapi.ParkingLocationResponse;
import net.yageek.strasbourgparkapi.ParkingStateResponse;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParkingListActivity extends AppCompatActivity {

    public static final String TAG = "ParkingList";

    private APIClient client = new APIClient();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ParkingAdapter adapter;
    private ListView listView;
    private LoadingView loadingView;

    private boolean isDownloading = false;

    //region Activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        listView = findViewById(R.id.parking_list_view);
        loadingView =  findViewById(R.id.loading_view);

        adapter = new ParkingAdapter(getApplicationContext());
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(adapter.getCount() < 1) {
            downloadData();
        }
    }
    //endregion

    //region Menu management
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate main menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem refreshData = menu.findItem(R.id.refresh_data);
        if(isDownloading) {
            refreshData.setEnabled(false);
            refreshData.getIcon().setAlpha(50);
        } else {
            refreshData.setEnabled(true);
            refreshData.getIcon().setAlpha(255);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_data:
                downloadData();
                return true;
            case R.id.filterby_name:
                adapter.setComparator(ParkingResult.Comparators.ByName);
                return true;
            case R.id.filterby_free_places:
                adapter.setComparator(ParkingResult.Comparators.ByFreePlaces);
                return true;
            case R.id.filterby_fillingrate:
                adapter.setComparator(ParkingResult.Comparators.ByFillingRate);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //endregion
    private void setLoading(boolean isLoading) {

        this.isDownloading = isLoading;
        if(isLoading) {
            listView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
        }
        invalidateOptionsMenu();
    }

    private void downloadData() {
        Log.d(TAG, "Starting downloading data...");

        // Toggle visibility
       setLoading(true);

        // Network call
        final Future<ParkingLocationResponse> locationFuture = client.getParkingLocationResponseFuture();
        final Future<ParkingStateResponse> statesFuture = client.getParkingStateResponseFuture();

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final ParkingLocationResponse location = locationFuture.get();
                    final ParkingStateResponse states = statesFuture.get();

                    Log.d(TAG, "Download succeeded !");
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setParkings(location.parkings, states.states);
                            setLoading(false);

                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error failed: " + e.getCause());

                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.setLoading(false);
                            loadingView.setText("An error occurs :( Try again later.");
                        }
                    });

                }
            }
        });
    }

    //region UI Thread primitives
    private void runOnMainThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, 800);
    }
    //endregion

}
