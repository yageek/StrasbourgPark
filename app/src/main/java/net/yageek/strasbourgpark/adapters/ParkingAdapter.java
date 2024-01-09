package net.yageek.strasbourgpark.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.yageek.common.ParkingResult;
import net.yageek.common.ParkingState;
import net.yageek.common.adapters.ParkingBaseAdapter;
import net.yageek.common.utils.ParkingStatusUtils;
import net.yageek.strasbourgpark.R;
/**
 * Created by yheinrich on 08.01.18.
 */

public class ParkingAdapter extends ParkingBaseAdapter<ParkingAdapter.ViewHolder> {

    public interface OnParkingResultSelected {
        void onParkingResultSelected(ParkingResult result);
    }

    public final String TAG = "ParkingAdapter";
    private OnParkingResultSelected listener;

    private ViewHolder.OnPositionSelectedListener holderListener = new ViewHolder.OnPositionSelectedListener() {
        @Override
        public void holderSelected(int position) {
            if(listener != null) {
                listener.onParkingResultSelected(getResults().get(position));
            }
        }
    };

    public ParkingAdapter(Context context) {

        super(context);
    }

    public void setListener(OnParkingResultSelected listener) {
        this.listener = listener;
    }

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

        holder.setListener(holderListener);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public interface OnPositionSelectedListener {
            void holderSelected(int position);
        }

        TextView parkingName;
        TextView parkingState;
        TextView parkingFreePlaces;
        ProgressBar availability;

        private OnPositionSelectedListener listener;

        public ViewHolder(View itemView) {
            super(itemView);

            parkingName = itemView.findViewById(R.id.parking_name);
            parkingState = itemView.findViewById(R.id.parking_state);
            parkingFreePlaces = itemView.findViewById(R.id.parking_free_places);
            availability = itemView.findViewById(R.id.parking_availability_slider);

            itemView.setOnClickListener(this);
        }

        public void setListener(OnPositionSelectedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {

            if(listener != null) {
                listener.holderSelected(getAdapterPosition());
            }
        }
    }

}
