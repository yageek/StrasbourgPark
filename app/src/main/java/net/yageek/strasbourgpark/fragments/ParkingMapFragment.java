package net.yageek.strasbourgpark.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import net.yageek.strasbourgpark.api.Parking;
import net.yageek.strasbourgpark.api.ParkingState;
import net.yageek.strasbourgpark.repository.ParkingRepository;
import net.yageek.strasbourgpark.utils.ParkingStatusUtils;
import net.yageek.strasbourgpark.viewmodel.ParkingModel;
import net.yageek.strasbourgpark.vo.DownloadResult;

/**
 * Created by yheinrich on 14.01.18.
 */

public class ParkingMapFragment extends SupportMapFragment implements OnMapReadyCallback, Observer<DownloadResult> {

    private static final LatLng StrasbourgCenter = new LatLng( 48.5734053,7.7521113);
    private GoogleMap map;
    private UiSettings uiSettings;

    private ParkingModel parkingModel;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);

        parkingModel = ViewModelProviders.of(getActivity()).get(ParkingModel.class);
        getMapAsync(this);
        return view;
    }


    //region OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in Sydney, Australia, and move the camera.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ParkingMapFragment.StrasbourgCenter, 12));
        parkingModel.getDownloadStatus().observe(this, this);
    }

    @Override
    public void onChanged(@Nullable DownloadResult downloadResult) {

        if (downloadResult.status == DownloadResult.Status.Success) {

            IconGenerator generator = new IconGenerator(getActivity());
            generator.setStyle(IconGenerator.STYLE_ORANGE);

            map.clear();
            for(ParkingRepository.ParkingResult result : downloadResult.results) {

                Parking parking = result.parking;
                ParkingState state = result.state;

                generator.setColor(ParkingStatusUtils.colorFromStatus(getActivity(), state.status));
                Bitmap icon = generator.makeIcon(String.format("%d", state.free));

                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(parking.lat, parking.lon))
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .title(parking.name);

                Marker marker = map.addMarker(options);
                marker.setTag(result);
            }

        }
    }

    //endregion
}
