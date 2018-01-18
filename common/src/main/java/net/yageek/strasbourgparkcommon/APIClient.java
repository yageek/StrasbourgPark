package net.yageek.strasbourgparkcommon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIClient implements Webservice {
    private Gson gson;
    private OkHttpClient client;

    public static final String LocationEndpoint = "http://carto.strasmap.eu/remote.amf.json/Parking.geometry";
    public static final String StatusEndpoint = "http://carto.strasmap.eu/remote.amf.json/Parking.status";

    public APIClient() {

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cache(null)
        .build();

        // JSON builder
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Parking.class, new Parking.JsonAdapter());
        builder.registerTypeAdapter(ParkingLocationResponse.class, new ParkingLocationResponse.JsonAdapter());
        builder.registerTypeAdapter(ParkingState.class, new ParkingState.JsonAdapter());
        builder.registerTypeAdapter(ParkingStateResponse.class, new ParkingStateResponse.JsonAdapter());
        gson = builder.create();
    }

    private <T> T getRequest(final String url, final Type type) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return gson.fromJson(response.body().charStream(), type);

    }

    public ParkingLocationResponse getParkingLocationResponse() throws IOException  {
        return getRequest(LocationEndpoint, ParkingLocationResponse.class);
    }

    public ParkingStateResponse getParkingStateResponse() throws IOException   {
        return getRequest(StatusEndpoint, ParkingStateResponse.class);
    }
}