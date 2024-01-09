package net.yageek.strasbourgpark.wear;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.yageek.common.APIClient;
import net.yageek.common.ParkingResult;
import net.yageek.common.adapters.ParkingBaseAdapter;
import net.yageek.common.repository.ParkingRepository;
import net.yageek.common.utils.ParkingStatusUtils;

import java.util.Collections;
import java.util.List;

public class ParkingActivity extends WearableActivity implements ParkingRepository.Callback {

    private TextView lastRefreshText;
    private View noDataLayout;

    private WearableRecyclerView recyclerView;
    private View loadingView;
    private ParkingRepository repository = new ParkingRepository(new APIClient());
    private WearParkingAdapter adapter;

    private List<ParkingResult> resultsList = Collections.emptyList();
    private Button retryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastRefreshText = (TextView) findViewById(R.id.last_refresh_value);
        recyclerView = (WearableRecyclerView) findViewById(R.id.parking_list);
        loadingView = findViewById(R.id.loading_view);

        adapter = new WearParkingAdapter(this);

        recyclerView.setEdgeItemsCenteringEnabled(true);
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        SnapHelper snap = new LinearSnapHelper();
        snap.attachToRecyclerView(recyclerView);

        noDataLayout = findViewById(R.id.no_data_layer);
        retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(resultsList.size() < 1) {
            fetchData();
        }
    }

    private void fetchData() {
        setLoading(true);
        repository.getParkingResults(this);
        recyclerView.scrollToPosition(0);
    }

    private void setLoading(boolean loading) {
        if(loading) {
            loadingView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            lastRefreshText.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.INVISIBLE);
        } else {
            loadingView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(Throwable t) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_message);

        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fetchData();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setLoading(false);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                setLoading(false);
            }
        });

        builder.create().show();
    }

    private void updateContents(List<ParkingResult> result, String lastRefresh) {
        resultsList = result;

        adapter.setResults(result);
        lastRefreshText.setText(lastRefresh);

        if(result.size() < 1) {
            lastRefreshText.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
        } else {
            lastRefreshText.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResponse(List<ParkingResult> result, String lastRefresh) {
        setLoading(false);
        updateContents(result, lastRefresh);
    }

    @Override
    public void onFailure(Throwable t) {

        showError(t);

        updateContents(Collections.<ParkingResult>emptyList(), "");
    }

    public class WearParkingAdapter extends ParkingBaseAdapter<RecyclerView.ViewHolder> {

        public final static int ParkingRowType = 0;
        public final static int RetryRowType = 1;

        public WearParkingAdapter(Context context) {
            super(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {
                default:
                case ParkingRowType:
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.parking_result_row, parent, false);
                    return new ParkingViewHolder(view);
                case RetryRowType:

                    Button button = new Button(getContext());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ParkingActivity.this.fetchData();
                        }
                    });
                    return new ReloadViewHolder(getContext(), button);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = holder.getItemViewType();

            switch(type) {
                case RetryRowType:
                    break;
                case ParkingRowType:
                    ParkingViewHolder pHolder = (ParkingViewHolder) holder;
                    ParkingResult result = getResults().get(position);

                    pHolder.parkingName.setText(result.parking.name);

                    int freePlaces = result.state.free;
                    Resources res = getContext().getResources();
                    String placeText = res.getQuantityString(R.plurals.free_places, freePlaces, freePlaces);
                    pHolder.parkingPlacesText.setText(placeText);

                    GradientDrawable drawable  = (GradientDrawable) pHolder.parkingStatusImage.getBackground();
                    drawable.setColor(ParkingStatusUtils.colorFromStatus(getContext(), result.state.status));
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {

            int result = getItemCount() - position - 1;
            if (result > 0) {
                return ParkingRowType;
            }
            return RetryRowType;
        }
    }

    public class ParkingViewHolder extends RecyclerView.ViewHolder {
        public TextView parkingName;
        public TextView parkingPlacesText;
        public ImageView parkingStatusImage;
        public ShapeDrawable parkingDrawable;


        public ParkingViewHolder(View itemView) {
            super(itemView);
            parkingName = (TextView) itemView.findViewById(R.id.parking_name);
            parkingPlacesText = (TextView) itemView.findViewById(R.id.parking_places);

            parkingStatusImage = (ImageView) itemView.findViewById(R.id.parking_status_indicator);
            parkingDrawable = (ShapeDrawable) parkingStatusImage.getDrawable();

        }
    }

    public static class ReloadViewHolder extends RecyclerView.ViewHolder {

        public Button button;
        public ReloadViewHolder(Context context, Button button) {
            super(button);
            this.button = button;
            button.setText(context.getResources().getText(R.string.retry));
        }
    }

}
