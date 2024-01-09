package net.yageek.common.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import net.yageek.common.ParkingState;
import net.yageek.strasbourgpark.common.R;


/**
 * Created by yheinrich on 15.01.18.
 */

public class ParkingStatusUtils {

    public static int colorFromStatus(Context context, ParkingState.Status status) {

        switch (status) {
            case Full: return ContextCompat.getColor(context, R.color.colorParkingFull);
            case Open: return ContextCompat.getColor(context, R.color.colorParkingOpen);
            case Closed: return ContextCompat.getColor(context, R.color.colorParkingClosed);
            default:
            case NotAvailable: return ContextCompat.getColor(context, R.color.colorParkingNotAvailable);

        }
    }

    public static String textFromStatus(Context context, ParkingState.Status status) {

        switch (status) {
            case Full: return context.getResources().getString(R.string.parking_state_full);
            case Open: return context.getResources().getString(R.string.parking_state_open);
            case Closed: return context.getResources().getString(R.string.parking_state_closed);
            default:
            case NotAvailable: return context.getResources().getString(R.string.parking_state_notavailable);

        }
    }
}
