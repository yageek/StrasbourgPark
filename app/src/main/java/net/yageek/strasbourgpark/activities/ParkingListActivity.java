package net.yageek.strasbourgpark.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.adapters.ParkingAdapter;
import net.yageek.strasbourgpark.adapters.ParkingResult;
import net.yageek.strasbourgpark.views.LoadingView;
import net.yageek.strasbourgpark.api.APIClient;
import net.yageek.strasbourgpark.api.ParkingLocationResponse;
import net.yageek.strasbourgpark.api.ParkingStateResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParkingListActivity extends AppCompatActivity {

    public static final String TAG = "ParkingList";

    private APIClient client = new APIClient();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ParkingAdapter adapter;
    private ListView listView;
    private LoadingView loadingView;
    private TextView noItemTextView;
    private TextView lastRefreshText;

    private boolean isDownloading = false;

    //region Activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        listView = findViewById(R.id.parking_list_view);
        loadingView = findViewById(R.id.loading_view);
        noItemTextView = findViewById(R.id.parking_list_view_no_item);
        lastRefreshText = findViewById(R.id.parking_last_refresh_text);

        adapter = new ParkingAdapter(getApplicationContext());
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUI();
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

        this.isDownloading = isLoading;
        if(isLoading) {
            listView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            noItemTextView.setVisibility(View.INVISIBLE);
            lastRefreshText.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
            updateContents();
        }
        invalidateOptionsMenu();
    }

    private void showError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadData();
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

    private void downloadData() {
        Log.d(TAG, "Starting downloading data...");

        // Toggle visibility
       setLoading(true);

        // Network call
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final  ParkingLocationResponse location = client.getParkingLocationResponse();
                    final ParkingStateResponse states = client.getParkingStateResponse();

                    Log.d(TAG, "Download succeeded !");
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            updateContents(location, states);
                            setLoading(false);

                        }
                    });

                } catch (IOException e) {
                    Log.e(TAG, "Error during download: " + e.getCause());

                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            showError();
                        }
                    });
                }
            }
        });
    }


    private void updateContents(ParkingLocationResponse locationResponse, ParkingStateResponse parkingStateResponse) {
        adapter.setParkings(locationResponse.parkings, parkingStateResponse.states);
        lastRefreshText.setText(parkingStateResponse.dataTime);
        updateContents();
    }

    private void updateContents() {

        if(adapter.getCount() < 1) {
            noItemTextView.setVisibility(View.VISIBLE);
            lastRefreshText.setVisibility(View.GONE);
        } else {
            noItemTextView.setVisibility(View.INVISIBLE);
            lastRefreshText.setVisibility(View.VISIBLE);
        }
    }

    private void updateUI() {

        if(adapter.getCount() < 1) {
            downloadData();
        }
    }


    //region UI Thread primitives
    private void runOnMainThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, 800);
    }
    //endregion

}
