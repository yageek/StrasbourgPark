package net.yageek.strasbourgpark.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgparkapi.Parking;
import net.yageek.strasbourgparkapi.ParkingState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yheinrich on 08.01.18.
 */

public class ParkingAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<ParkingResult> results = new ArrayList<>();

    private ParkingResult.Comparators comparator = ParkingResult.Comparators.ByName;

    public ParkingAdapter(Context context) {
        this.context = context;
    }

    public ParkingResult.Comparators getComparator() {
        return comparator;
    }

    public void setComparator(ParkingResult.Comparators comparator) {
        this.comparator = comparator;
        Collections.sort(results, comparator.getComparator());
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return -1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null) {
            View inflatedView = LayoutInflater.from(context).inflate(R.layout.parking_row, viewGroup,false );

            holder = new ViewHolder();
            holder.parkingName = inflatedView.findViewById(R.id.parking_name);
            holder.parkingState = inflatedView.findViewById(R.id.parking_state);
            holder.parkingFreePlaces = inflatedView.findViewById(R.id.parking_free_places);
            holder.availability = inflatedView.findViewById(R.id.parking_availability_slider);

            inflatedView.setTag(holder);
            view = inflatedView;
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ParkingResult result = results.get(i);
        holder.parkingName.setText(result.parking.name);


        if(result.state != null) {
            ParkingState state = result.state;

            holder.parkingState.setTextColor(colorFromStatus(state.status));
            holder.parkingState.setText(textFromStatus(state.status));

            int progress = (int) ((double) state.free / (double) state.total * 100.0);

            holder.availability.setIndeterminate(false);
            holder.availability.setProgress(progress);

            holder.parkingFreePlaces.setVisibility(View.VISIBLE);
            holder.parkingFreePlaces.setText(String.format("%d", state.free));
        } else {
            holder.parkingFreePlaces.setVisibility(View.INVISIBLE);
            holder.availability.setIndeterminate(true);
        }

        return view;
    }

    public static class ViewHolder {
        TextView parkingName;
        TextView parkingState;
        TextView parkingFreePlaces;
        ProgressBar availability;
    }

    public void setParkings(List<Parking> parkings, List<ParkingState> states) {

        ArrayList<ParkingResult> newResults = new ArrayList<>();

        for(Parking parking: parkings) {
            for(ParkingState state: states) {

                if(parking.identifier.equals(state.parkingIdentifier)) {
                    newResults.add(new ParkingResult(parking, state));
                    break;
                }
            }
        }
        this.results = newResults;
        notifyDataSetChanged();
    }

    public void clear() {
        this.results.clear();
        notifyDataSetChanged();
    }

    public int colorFromStatus(ParkingState.Status status) {

        switch (status) {
            case Full: return ContextCompat.getColor(context, R.color.colorParkingFull);
            case Open: return ContextCompat.getColor(context, R.color.colorParkingOpen);
            case Closed: return ContextCompat.getColor(context, R.color.colorParkingClosed);
            default:
            case NotAvailable: return ContextCompat.getColor(context, R.color.colorParkingNotAvailable);

        }
    }

    public String textFromStatus(ParkingState.Status status) {

        switch (status) {
            case Full: return context.getResources().getString(R.string.parking_state_full);
            case Open: return context.getResources().getString(R.string.parking_state_open);
            case Closed: return context.getResources().getString(R.string.parking_state_closed);
            default:
            case NotAvailable: return context.getResources().getString(R.string.parking_state_notavailable);

        }
    }
}
