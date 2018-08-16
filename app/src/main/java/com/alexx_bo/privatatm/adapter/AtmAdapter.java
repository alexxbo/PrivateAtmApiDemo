package com.alexx_bo.privatatm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexx_bo.privatatm.R;
import com.alexx_bo.privatatm.model.Device;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AtmAdapter extends RecyclerView.Adapter<AtmAdapter.ViewHolder> {

    private List<Device> devices;
    private OnClickListener mapClickListener;

    public AtmAdapter(List<Device> devices) {
        this.devices = devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.atm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.status.setText(devices.get(position).getStatus());
        holder.place_name.setText(devices.get(position).getPlaceRu());
        holder.address.setText(devices.get(position).getFullAddressRu());
        holder.distance.setText(exchangeDistance(devices.get(position).getDistanceToCurrPos()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapClickListener != null) {
                    LatLng deviceLatLang =
                            new LatLng(Double.parseDouble(devices.get(holder.getAdapterPosition()).getLatitude()),
                                    Double.parseDouble(devices.get(holder.getAdapterPosition()).getLongitude()));
                    mapClickListener.mapClick(deviceLatLang);
                }
            }
        });
        holder.mapView.setClickable(false);
    }

    private String exchangeDistance(double distanceToCurrPos) {
        String distance = null;
        if (distanceToCurrPos < 1000) {
            distance = ((int) distanceToCurrPos) + " m";
        } else if (distanceToCurrPos >= 1000) {
            double distanceRounding = new BigDecimal(distanceToCurrPos / 1000).setScale(1, RoundingMode.UP).doubleValue();
            distance = distanceRounding + " km";
        }
        return distance;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        GoogleMap mMap;
        TextView place_name;
        TextView address;
        TextView status;
        TextView distance;
        MapView mapView;

        public ViewHolder(View itemView) {
            super(itemView);

            place_name = itemView.findViewById(R.id.tv_place_name);
            address = itemView.findViewById(R.id.tv_address);
            status = itemView.findViewById(R.id.tv_status);
            distance = itemView.findViewById(R.id.tv_distance);
            mapView = itemView.findViewById(R.id.atm_map);
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMapToolbarEnabled(false);
            bindData(devices.get(getAdapterPosition()));
        }

        public void bindData(Device device) {

            LatLng coordinates = new LatLng(Double.parseDouble(device.getLatitude()),
                    Double.parseDouble(device.getLongitude()));
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18));
                mMap.addMarker(new MarkerOptions().position(coordinates));
            }
        }
    }

    public void updateData(List<Device> deviceList) {
        if (devices.size() != 0) {
            devices.clear();
        }
        devices.addAll(deviceList);
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void mapClick(LatLng position);
    }

    public void setOnClickListener(OnClickListener mapClickListener) {
        this.mapClickListener = mapClickListener;
    }
}
