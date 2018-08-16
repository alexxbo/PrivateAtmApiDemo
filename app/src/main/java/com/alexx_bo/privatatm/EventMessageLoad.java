package com.alexx_bo.privatatm;

import com.alexx_bo.privatatm.model.Device;

import java.util.List;

public class EventMessageLoad {

    private List<Device> devices;

    public EventMessageLoad(List<Device> devices) {
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
