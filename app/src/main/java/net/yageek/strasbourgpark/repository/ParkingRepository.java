package net.yageek.strasbourgpark.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.yageek.strasbourgpark.api.Parking;
import net.yageek.strasbourgpark.api.ParkingLocationResponse;
import net.yageek.strasbourgpark.api.ParkingState;
import net.yageek.strasbourgpark.api.ParkingStateResponse;
import net.yageek.strasbourgpark.api.Webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by yheinrich on 14.01.18.
 */

public class ParkingRepository {

    public interface Callback {
        void onResponse(List<ParkingResult> result, String lastRefresh);
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
                            callback.onResponse(results, states.dataTime);
                        }
                    });

                } catch (final IOException e) {
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

        for(Parking parking: locationResponse.parkings) {
            for(ParkingState state: stateResponse.states) {

                if(parking.identifier.equals(state.parkingIdentifier)) {
                    newResults.add(new ParkingResult(parking, state, stateResponse.dataTime));
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

    /**
     * Created by yheinrich on 09.01.18.
     */

    public static class ParkingResult {
        public final String lastRefresh;
        public final Parking parking;
        public final ParkingState state;

        protected ParkingResult(Parking parking, ParkingState state, String lastRefresh) {
            this.parking = parking;
            this.state = state;
            this.lastRefresh = lastRefresh;
        }

        int fillingRate() {
            return (int) ((double) state.free / (double) state.total * 100.0);
        }

        public enum Comparators {
            ByName,
            ByFreePlaces,
            ByFillingRate;

            Comparator<ParkingResult> nameComparator = new Comparator<ParkingResult>() {
                @Override
                public int compare(ParkingResult parkingResult, ParkingResult t1) {
                    return parkingResult.parking.name.compareTo(t1.parking.name);
                }
            };

            Comparator<ParkingResult> freePlaces = new Comparator<ParkingResult>() {
                @Override
                public int compare(ParkingResult parkingResult, ParkingResult t1) {
                    return t1.state.free - parkingResult.state.free;
                }
            };

            Comparator<ParkingResult> fillingRate = new Comparator<ParkingResult>() {
                @Override
                public int compare(ParkingResult parkingResult, ParkingResult t1) {
                    return t1.fillingRate() - parkingResult.fillingRate();
                }
            };

            public Comparator<ParkingResult> getComparator() {
                switch (this) {
                    case ByFillingRate: return fillingRate;
                    case ByFreePlaces: return freePlaces;
                    default: return nameComparator;
                }

            }
        }
    }
}
