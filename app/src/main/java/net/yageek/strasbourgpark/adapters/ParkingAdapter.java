package net.yageek.strasbourgpark.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.utils.ParkingStatusUtils;
import net.yageek.strasbourgparkapi.ParkingResult;
import net.yageek.strasbourgparkapi.ParkingState;
import net.yageek.strasbourgparkapi.adapters.ParkingBaseAdapter;

/**
 * Created by yheinrich on 08.01.18.
 */

public class ParkingAdapter extends ParkingBaseAdapter<ParkingAdapter.ViewHolder> implements View.OnClickListener {

    public interface Listener {
        void resultSelected(ParkingResult result);
    }
    public final String TAG = "ParkingAdapter";

    public ParkingAdapter(Context context) {
        super(context);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Listener listener;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(getContext()).inflate(R.layout.parking_row, parent,false );
        return new ViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParkingResult result = getResults().get(position);

        holder.parkingName.setText(result.parking.name);

        if(result.state != null) {
            ParkingState state = result.state;

            holder.parkingState.setTextColor(ParkingStatusUtils.colorFromStatus(getContext(), state.status));
            holder.parkingState.setText(ParkingStatusUtils.textFromStatus(getContext(), state.status));

            int progress = (int) ((double) state.free / (double) state.total * 100.0);

            holder.availability.setIndeterminate(false);
            holder.availability.setProgress(progress);

            holder.parkingFreePlaces.setVisibility(View.VISIBLE);
            holder.parkingFreePlaces.setText(String.format("%d", state.free));
        } else {
            holder.parkingFreePlaces.setVisibility(View.INVISIBLE);
            holder.availability.setIndeterminate(true);
        }

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Click on item " + v.getTag());

        if(listener != null) {
            int index = (int) v.getTag();
            listener.resultSelected(getResults().get(index));
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView parkingName;
        TextView parkingState;
        TextView parkingFreePlaces;
        ProgressBar availability;

        public ViewHolder(View itemView) {
            super(itemView);

            parkingName = itemView.findViewById(R.id.parking_name);
            parkingState = itemView.findViewById(R.id.parking_state);
            parkingFreePlaces = itemView.findViewById(R.id.parking_free_places);
            availability = itemView.findViewById(R.id.parking_availability_slider);
        }
    }
}
