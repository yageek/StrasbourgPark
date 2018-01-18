package net.yageek.strasbourgpark.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import net.yageek.strasbourgparkcommon.ParkingResult;
import net.yageek.strasbourgparkcommon.utils.ParkingStatusUtils;
import net.yageek.strasbourgpark.viewmodel.ParkingModel;
import net.yageek.strasbourgpark.vo.DownloadResult;
import net.yageek.strasbourgparkcommon.Parking;
import net.yageek.strasbourgparkcommon.ParkingState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yheinrich on 14.01.18.
 */

public class ParkingMapFragment extends SupportMapFragment implements OnMapReadyCallback, Observer<DownloadResult> {

    private final static int LOCATION_REQUEST = 0;

    private static final LatLng StrasbourgCenter = new LatLng( 48.5734053,7.7521113);
    private GoogleMap map;

    private ParkingModel parkingModel;
    private Map<String, Marker> markerMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);

        parkingModel = ViewModelProviders.of(getActivity()).get(ParkingModel.class);
        getMapAsync(this);
        return view;
    }

    //region Selection methods
    public void selectParking(String identifier) {

        for(ParkingResult result: parkingModel.getDownloadStatus().getValue().results) {

            if(identifier.equals(result.parking.identifier)) {
                selectMarker(identifier);
                break;
            }
        }

    }

    private void selectMarker(String identifier) {
        Marker marker = markerMap.get(identifier);
        if(marker != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));
            marker.showInfoWindow();
        }
    }

    //endregion


    //region permissions
    private void tryEnablingLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // TODO: Location

            } else {
                String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_REQUEST);
            }
        } else {
            map.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_REQUEST && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            map.setMyLocationEnabled(true);
        }
    }

    //endregion

    //region OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Add a marker in Sydney, Australia, and move the camera.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ParkingMapFragment.StrasbourgCenter, 12));
        parkingModel.getDownloadStatus().observe(this, this);

        map.setLatLngBoundsForCameraTarget(StrasbourgBounds());

        // Bounds
        tryEnablingLocation();
    }

    static LatLngBounds StrasbourgBounds() {
        LatLngBounds bounds = LatLngBounds.builder()
                .include(new LatLng(48.6456, 7.6492))
                .include(new LatLng(48.6456, 7.8789))
                .include(new LatLng(48.5183, 7.8789))
                .include(new LatLng(48.5183, 7.6492)).build();
                return bounds;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(map != null && !map.isMyLocationEnabled()) {
            tryEnablingLocation();
        }
    }

    @Override
    public void onChanged(@Nullable DownloadResult downloadResult) {

        if (downloadResult.status == DownloadResult.Status.Success) {

            IconGenerator generator = new IconGenerator(getActivity());

            map.clear();
            markerMap.clear();
            for(ParkingResult result : downloadResult.results) {

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

                markerMap.put(parking.identifier, marker);
            }

        }
    }

    //endregion
}
