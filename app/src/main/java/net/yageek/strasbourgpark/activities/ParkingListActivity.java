package net.yageek.strasbourgpark.activities;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.adapters.ParkingAdapter;
import net.yageek.strasbourgpark.views.LoadingView;
import net.yageek.strasbourgparkapi.APIClient;
import net.yageek.strasbourgparkapi.ParkingLocationResponse;
import net.yageek.strasbourgparkapi.ParkingStateResponse;

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
        downloadData();
    }


    void downloadData() {

        // Toggle visibility

        listView.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);

        // Network call
        final Future<ParkingLocationResponse> locationFuture = client.getParkingLocationResponseFuture();
        final Future<ParkingStateResponse> statesFuture = client.getParkingStateResponseFuture();

        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final ParkingLocationResponse location = locationFuture.get();
                    final ParkingStateResponse states = statesFuture.get();
                    runOnMainThreadAfterDelay(new Runnable() {
                        @Override
                        public void run() {

                            adapter.setParkings(location.parkings, states.states);
                            adapter.notifyDataSetChanged();

                            listView.setVisibility(View.VISIBLE);
                            loadingView.setVisibility(View.INVISIBLE);
                        }
                    }, 800);

                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getLocalizedMessage());

                    runOnMainThreadAfterDelay(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.setLoading(false);
                            loadingView.setText("An error occurs :( Try again later.");
                        }
                    }, 800);

                }
            }
        });
    }

    private void runOnMainThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    private void runOnMainThreadAfterDelay(Runnable runnable, long delay){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }
}
