package net.yageek.strasbourgparkcommon.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import net.yageek.strasbourgparkcommon.ParkingResult;

import java.util.Collections;
import java.util.List;

/**
 * Created by yheinrich on 16.01.18.
 */

public abstract class ParkingBaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private final Context context;

    public Context getContext() {
        return context;
    }

    public List<ParkingResult> getResults() {
        return results;
    }

    private List<ParkingResult> results = Collections.emptyList();

    private ParkingResult.Comparators comparator = ParkingResult.Comparators.ByName;

    public ParkingBaseAdapter(Context context) {
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

    public void setResults(List<ParkingResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void clear() {
        this.results.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
}
