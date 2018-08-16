package com.alexx_bo.privatatm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alexx_bo.privatatm.AtmItem;
import com.alexx_bo.privatatm.AtmRender;
import com.alexx_bo.privatatm.EventMessageLoad;
import com.alexx_bo.privatatm.EventMessageLoadFail;
import com.alexx_bo.privatatm.LoadDataService;
import com.alexx_bo.privatatm.R;
import com.alexx_bo.privatatm.model.Device;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final String ARG_DEVICE = "param1";
    public static final String TAG = "MapFragment";
    private ClusterManager<AtmItem> mClusterManager;
    MapView mapView;
    GoogleMap mMap;
    public LatLng mDefaultLocation = new LatLng(48.4633751, 35.0385002);
    private boolean mapReady;
    private List<Device> mDevices;
    private Location mLastKnowLocation;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LatLng coordPos;
        if (getArguments() != null) {
            Device currDevice = getArguments().getParcelable(ARG_DEVICE);
            coordPos = new LatLng(Double.parseDouble(currDevice.getLatitude()),
                    Double.parseDouble(currDevice.getLongitude()));
        } else {
            coordPos = mDefaultLocation;
        }
        LoadDataService.startActionLoad(getContext(), coordPos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mDevices = new ArrayList<>();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(getActivity(), this)
                .build();

        mapView = view.findViewById(R.id.mapview);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventLoad(EventMessageLoad event) {
        if (mDevices.size() == 0) {
            mDevices.addAll(event.getDevices());
            if (mapReady) {
                setUpClusterer();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventLoadFail(EventMessageLoadFail event) {
        Toast.makeText(getContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));
        requestLocationPermission();
    }

    private void setUpClusterer() {
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setRenderer(new AtmRender(getContext(), mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        addItems();
    }

    private void addItems() {
        for (Device device : mDevices) {
            double lat = Double.parseDouble(device.getLatitude());
            double lng = Double.parseDouble(device.getLongitude());
            AtmItem item = new AtmItem(new LatLng(lat, lng), device.getPlaceRu(),
                    device.getFullAddressRu(), device.getStatus());
            mClusterManager.addItem(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    private void requestLocationPermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        placeDetection();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @SuppressLint("MissingPermission")
    private void placeDetection() {

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        }
    }

    private void getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity())
                .getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLastKnowLocation = task.getResult();
                    if (mLastKnowLocation != null) {
                        Log.i(TAG, "onComplete: lastKnownLocation: " + mLastKnowLocation);
                        LoadDataService.startActionLoad(getContext(),
                                new LatLng(mLastKnowLocation.getLatitude(),
                                        mLastKnowLocation.getLongitude()));
                    }
                }
            }
        });
    }

    private void getMyPlaces() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Places.getPlaceDetectionClient(getActivity())
                .getCurrentPlace(null)
                .addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceLikelihoodBufferResponse response = task.getResult();

                            for (PlaceLikelihood place : response) {
                                Log.i(TAG, "onComplete: plase: " + place.getPlace().getAddress());
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getLastKnownLocation();
        getMyPlaces();
        return false;
    }

    public void moveCamera(LatLng position) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }
}
