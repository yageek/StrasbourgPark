package net.yageek.common;


import java.io.IOException;

/**
 * Created by yheinrich on 14.01.18.
 */

public interface Webservice {
    ParkingLocationResponse getParkingLocationResponse() throws IOException;
    ParkingStateResponse getParkingStateResponse() throws IOException;
}
