package net.yageek.common.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import net.yageek.common.ParkingState;
import net.yageek.strasbourgpark.common.R;


/**
 * Created by yheinrich on 15.01.18.
 */

public class ParkingStatusUtils {

    public static int colorFromStatus(Context context, ParkingState status) {

        switch (status.etat) {
//            case Full: return ContextCompat.getColor(context, R.color.colorParkingFull);
            case 1: return ContextCompat.getColor(context, R.color.colorParkingOpen);
            case 2: return ContextCompat.getColor(context, R.color.colorParkingClosed);
            default:
            return ContextCompat.getColor(context, R.color.colorParkingNotAvailable);

        }
    }

    public static String textFromStatus(Context context, ParkingState status) {

        switch (status.etat) {
//            case Full: return context.getResources().getString(R.string.parking_state_full);
            case 1: return context.getResources().getString(R.string.parking_state_open);
            case 2: return context.getResources().getString(R.string.parking_state_closed);
            default:
                return context.getResources().getString(R.string.parking_state_notavailable);

        }
    }
}
