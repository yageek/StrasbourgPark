package net.yageek.strasbourgpark.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import net.yageek.strasbourgpark.R;
import net.yageek.strasbourgpark.adapters.ParkingAdapter;
import net.yageek.strasbourgpark.fragments.ParkingListFragment;
import net.yageek.strasbourgpark.fragments.ParkingMapFragment;
import net.yageek.strasbourgpark.viewmodel.ParkingModel;
import net.yageek.strasbourgpark.vo.DownloadResult;
import net.yageek.strasbourgparkcommon.ParkingResult;

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
        tabsAdapter.mapFragment.selectParking(result.parking.identifier);
    }

    //endregion

    //region
    public class TabsAdapter extends FragmentPagerAdapter {

        private ParkingListFragment listFragment;
        private ParkingMapFragment mapFragment;

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }


        public ParkingListFragment getListFragment() {
            return listFragment;
        }

        public ParkingMapFragment getMapFragment() {
            return mapFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    listFragment = new ParkingListFragment();
                    listFragment.setListener(ParkingListActivity.this);
                    return listFragment;

                default:
                    mapFragment = new ParkingMapFragment();
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
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

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
        switch (item.getItemId()) {
            case R.id.refresh_data:
                parkingModel.fetchData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion

}
