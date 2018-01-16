package net.yageek.strasbourgpark.wear;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.yageek.strasbourgparkapi.APIClient;
import net.yageek.strasbourgparkapi.ParkingResult;
import net.yageek.strasbourgparkapi.adapters.ParkingBaseAdapter;
import net.yageek.strasbourgparkapi.repository.ParkingRepository;

import java.util.List;

public class ParkingActivity extends WearableActivity implements ParkingRepository.Callback {


    private WearableRecyclerView recyclerView;
    private View loadingView;
    private ParkingRepository repository = new ParkingRepository(new APIClient());
    private WearParkingAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (WearableRecyclerView) findViewById(R.id.parking_list);
        loadingView = findViewById(R.id.loading_view);

        adapter = new WearParkingAdapter(this);

        recyclerView.setEdgeItemsCenteringEnabled(true);
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        SnapHelper snap = new LinearSnapHelper();
        snap.attachToRecyclerView(recyclerView);

        // Enables Always-on
        setAmbientEnabled();

        fetchData();
    }

    private void fetchData() {
        setLoading(true);
        repository.getParkingResults(this);
    }

    private void setLoading(boolean loading) {
        if(loading) {
            loadingView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            loadingView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponse(List<ParkingResult> result, String lastRefresh) {
        setLoading(false);
        adapter.setResults(result);
    }

    @Override
    public void onFailure(Throwable t) {
        setLoading(false);
    }

    public static class WearParkingAdapter extends ParkingBaseAdapter<ViewHolder> {

        public WearParkingAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.parking_result_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ParkingResult result = getResults().get(position);
            holder.parkingName.setText(result.parking.name);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView parkingName;
        public ViewHolder(View itemView) {
            super(itemView);
            parkingName = (TextView) itemView.findViewById(R.id.parking_text_name);
        }
    }

}
