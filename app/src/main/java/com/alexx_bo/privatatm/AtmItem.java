package com.alexx_bo.privatatm;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class AtmItem implements ClusterItem {
    private final LatLng mPosition;
    private String status;
    private String placeName;
    private String address;

    public AtmItem(LatLng mPosition, String placeName, String fullAddress, String status) {
        this.placeName = placeName;
        this.address = getAddress(fullAddress);
        this.mPosition = mPosition;
        this.status = status;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    private String getAddress(String fullAddress) {
        return fullAddress.replace("Украина,область Днепропетровская,", "");
    }

    public String getStatus() {
        return status;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
    }
}
