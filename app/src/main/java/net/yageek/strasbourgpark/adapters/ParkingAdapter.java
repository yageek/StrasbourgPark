package net.yageek.strasbourgpark.adapters;

import android.content.Context;
import android.text.Layout;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by yheinrich on 08.01.18.
 */

public class ParkingAdapter extends BaseAdapter {
    private final Context context;
    private List<Parking> parkings = new ArrayList<>();
    private HashMap<String, ParkingState> statesMap = new HashMap<>();

    public ParkingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return parkings.size();
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
            holder.parkingPrice = inflatedView.findViewById(R.id.parking_price);
            holder.parkingFreePlaces = inflatedView.findViewById(R.id.parking_free_places);
            holder.availability = inflatedView.findViewById(R.id.parking_availability_slider);

            inflatedView.setTag(holder);
            view = inflatedView;
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Parking parking = parkings.get(i);
        holder.parkingName.setText(parking.name);
        holder.parkingPrice.setText(parking.price_fr);

        if(statesMap.containsKey(parking.identifier)) {
            ParkingState state = statesMap.get(parking.identifier);
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
        TextView parkingPrice;
        TextView parkingFreePlaces;
        ProgressBar availability;
    }

    public void setParkings(List<Parking> parkings, List<ParkingState> states) {
        this.parkings = parkings;

        statesMap.clear();
        for(ParkingState state: states) {
            statesMap.put(state.parkingIdentifier, state);
        }
    }

    public void clear() {
        this.parkings.clear();
        this.statesMap.clear();
    }
}
