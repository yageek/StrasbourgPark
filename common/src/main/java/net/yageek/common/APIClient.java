package net.yageek.common;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;


import javax.net.ssl.SSLContext;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIClient implements Webservice {
    private Gson gson;
    private OkHttpClient client;

    public static final String LocationEndpoint = "https://data.strasbourg.eu/api/explore/v2.1/catalog/datasets/parkings/records?&limit=100&offset=0";
    public static final String StatusEndpoint = "https://data.strasbourg.eu/api/explore/v2.1/catalog/datasets/occupation-parkings-temps-reel/records?limit=100&offset=0";
    /**
     * Initialize SSL
     * @param mContext
     */
    public static void initializeSSLContext(Context mContext){
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    public APIClient() {

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cache(null)
        .build();

        // JSON builder
        gson = new Gson();
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