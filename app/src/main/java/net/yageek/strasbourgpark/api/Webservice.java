package net.yageek.strasbourgpark.api;


import java.io.IOException;

import okhttp3.Call;

/**
 * Created by yheinrich on 14.01.18.
 */

public interface Webservice {
    ParkingLocationResponse getParkingLocationResponse() throws IOException;
    ParkingStateResponse getParkingStateResponse() throws IOException;
}
