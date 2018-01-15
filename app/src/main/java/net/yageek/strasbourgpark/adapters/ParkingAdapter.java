package net.yageek.strasbourgpark.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.api.ParkingState;
import net.yageek.strasbourgpark.repository.ParkingRepository;
import net.yageek.strasbourgpark.utils.ParkingStatusUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by yheinrich on 08.01.18.
 */

public class ParkingAdapter extends BaseAdapter {
    private final Context context;
    private List<ParkingRepository.ParkingResult> results;

    private ParkingRepository.ParkingResult.Comparators comparator = ParkingRepository.ParkingResult.Comparators.ByName;

    public ParkingAdapter(Context context) {
        this.context = context;
    }

    public ParkingRepository.ParkingResult.Comparators getComparator() {
        return comparator;
    }

    public void setComparator(ParkingRepository.ParkingResult.Comparators comparator) {
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
        return results == null ? 0 : results.size();
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

        ParkingRepository.ParkingResult result = results.get(i);
        holder.parkingName.setText(result.parking.name);


        if(result.state != null) {
            ParkingState state = result.state;

            holder.parkingState.setTextColor(ParkingStatusUtils.colorFromStatus(context, state.status));
            holder.parkingState.setText(ParkingStatusUtils.textFromStatus(context, state.status));

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

    public void setResults(List<ParkingRepository.ParkingResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void clear() {
        this.results.clear();
        notifyDataSetChanged();
    }
}
