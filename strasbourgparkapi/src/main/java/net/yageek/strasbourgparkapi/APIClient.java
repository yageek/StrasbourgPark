package net.yageek.strasbourgparkapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIClient {
    private Gson gson;
    private OkHttpClient client = new OkHttpClient();
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    public static final String LocationEndpoint = "http://carto.strasmap.eu/remote.amf.json/Parking.geometry";
    public static final String StatusEndpoint = "http://carto.strasmap.eu/remote.amf.json/Parking.status";

    public APIClient() {

        // JSON builder
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Parking.class, new Parking.JsonAdapter());
        builder.registerTypeAdapter(ParkingLocationResponse.class, new ParkingLocationResponse.JsonAdapter());
        builder.registerTypeAdapter(ParkingState.class, new ParkingState.JsonAdapter());
        builder.registerTypeAdapter(ParkingStateResponse.class, new ParkingStateResponse.JsonAdapter());
        gson = builder.create();
    }

    private <T> Future<T> getRequest(final String url, final Type type) {

        Future<T> future = executor.submit(new Callable<T>() {
            @Override
            public T call() throws IOException, NullPointerException {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                T result = gson.fromJson(response.body().charStream(), type);
                if (result == null) {
                    throw new NullPointerException();
                }
                return result;
            }
        });
        return future;
    }

    public Future<ParkingLocationResponse> getParkingLocationResponseFuture() {
        return getRequest(LocationEndpoint, ParkingLocationResponse.class);
    }

    public Future<ParkingStateResponse> getParkingStateResponseFuture() {
        return getRequest(StatusEndpoint, ParkingStateResponse.class);
    }
}