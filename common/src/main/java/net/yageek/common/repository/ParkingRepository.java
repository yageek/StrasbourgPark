package net.yageek.common.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.yageek.common.Parking;
import net.yageek.common.ParkingLocationResponse;
import net.yageek.common.ParkingResult;
import net.yageek.common.ParkingState;
import net.yageek.common.ParkingStateResponse;
import net.yageek.common.Webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by yheinrich on 14.01.18.
 */

public class ParkingRepository {

    public interface Callback {
        void onResponse(List<ParkingResult> result);
        void onFailure(Throwable t);
    }

    private static final String TAG = "ParkingRepository";

    private Webservice client;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ParkingRepository(Webservice client) {
        this.client = client;
    }

    public void getParkingResults(final Callback callback) {
        Log.d(TAG, "Starting downloading data...");

        // Network call
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final ParkingLocationResponse location = client.getParkingLocationResponse();
                    final ParkingStateResponse states = client.getParkingStateResponse();
                    Log.d(TAG, "Download succeeded !");

                    final List<ParkingResult> results = computeResults(location, states);
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(results);
                        }
                    });

                } catch (final Exception e) {
                    Log.e(TAG, "Error during download: " + e.getCause());

                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }
            }
        });
    }

    List<ParkingResult> computeResults(ParkingLocationResponse locationResponse, ParkingStateResponse stateResponse) {

        ArrayList<ParkingResult> newResults = new ArrayList<>();

        for(Parking parking: locationResponse.results) {
            for(ParkingState state: stateResponse.results) {

                if(parking.name.equals(state.nom_parking)) {
                    newResults.add(new ParkingResult(parking, state));
                    break;
                }
            }
        }

        return newResults;
    }

    private void runOnMainThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
