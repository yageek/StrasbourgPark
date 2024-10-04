package net.yageek.strasbourgpark.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import net.yageek.common.APIClient;
import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.adapters.ParkingAdapter;
import net.yageek.strasbourgpark.fragments.ParkingListFragment;
import net.yageek.strasbourgpark.fragments.ParkingMapFragment;
import net.yageek.strasbourgpark.viewmodel.ParkingModel;
import net.yageek.strasbourgpark.vo.DownloadResult;
import net.yageek.common.ParkingResult;

public class ParkingListActivity extends AppCompatActivity implements ParkingAdapter.OnParkingResultSelected {

    private static final String TAG = "Main activity";
    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private ParkingModel parkingModel;
    private TabLayout tabs;

    //region Activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APIClient.initializeSSLContext(this);
        setContentView(R.layout.main_activity);

        tabsAdapter = new TabsAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(tabsAdapter);

        tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        parkingModel = ViewModelProviders.of(this).get(ParkingModel.class);
    }

    @Override
    public void onParkingResultSelected(ParkingResult result) {
        TabLayout.Tab tab = tabs.getTabAt(1);
        tab.select();
        tabsAdapter.mapFragment.selectParking(result.parking.name);
    }

    //endregion

    //region
    public class TabsAdapter extends FragmentPagerAdapter {

        private ParkingListFragment listFragment;
        private ParkingMapFragment mapFragment;

        public TabsAdapter(FragmentManager fm) {
            super(fm);
            listFragment = new ParkingListFragment();
            listFragment.setListener(ParkingListActivity.this);

            mapFragment = new ParkingMapFragment();

        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return listFragment;

                default:
                    return mapFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    //endregion

    //region Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate main menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {

        MenuItem refreshData = menu.findItem(R.id.refresh_data);

        DownloadResult result = parkingModel.getDownloadStatus().getValue();
        Boolean isDownloading = result == null ? false : result.status == DownloadResult.Status.Loading;

        if(isDownloading) {
            refreshData.setEnabled(false);
            refreshData.getIcon().setAlpha(50);
        } else {
            refreshData.setEnabled(true);
            refreshData.getIcon().setAlpha(255);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_data) {
            parkingModel.fetchData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

}
