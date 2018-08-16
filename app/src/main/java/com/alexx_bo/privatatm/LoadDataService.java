package com.alexx_bo.privatatm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.alexx_bo.privatatm.model.Device;
import com.alexx_bo.privatatm.model.ResponceData;
import com.alexx_bo.privatatm.model.Tw;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;


public class LoadDataService extends IntentService {
    private static final String ACTION_LOAD = "com.alexx_bo.privatatm.action.LOAD";
    private static final String EXTRA_LAT_LANG = "com.alexx_bo.privatatm.extra.LATLANG";
    private List<Device> mDevices;

    public LoadDataService() {
        super("LoadDataService");
    }

    public static void startActionLoad(Context context, LatLng coordPos) {
        Intent intent = new Intent(context, LoadDataService.class);
        intent.setAction(ACTION_LOAD);
        intent.putExtra(EXTRA_LAT_LANG, coordPos);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD.equals(action)) {
                final LatLng coordinates = intent.getParcelableExtra(EXTRA_LAT_LANG);
                handleActionLoad(coordinates);
            }
        }
    }

    private void handleActionLoad(LatLng coordinates) {
        Call<ResponceData> responseBody = PrivatClient.getInstance().getPrivatApi().getData("", "Днепр");
        try {
            Response<ResponceData> response = responseBody.execute();
            if (response.isSuccessful()) {
                mDevices = response.body().getDevices();
                setDistance(coordinates);
                setStatus();
                Collections.sort(mDevices, new Comparator<Device>() {
                    @Override
                    public int compare(Device o1, Device o2) {
                        return (int) (o1.getDistanceToCurrPos() - o2.getDistanceToCurrPos());
                    }
                });
                EventBus.getDefault().post(new EventMessageLoad(mDevices));
            } else {
                EventBus.getDefault().post(new EventMessageLoadFail(response.errorBody().string()));
            }
        } catch (IOException e) {
            EventBus.getDefault().post(new EventMessageLoadFail("No internet connection"));
        }
    }

    private void setDistance(LatLng curPosCoord) {
        for (Device device : mDevices) {
            LatLng latLngDevice = new LatLng(Double.parseDouble(device.getLatitude())
                    , Double.parseDouble(device.getLongitude()));
            Double distance = SphericalUtil.computeDistanceBetween(curPosCoord, latLngDevice);
            device.setDistanceToCurrPos(distance.intValue());
        }
    }

    private String isOpen(Tw timeWork) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("452"));
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        String time = null;
        switch (currentDay) {
            case 2:
                time = timeWork.getMon();
                break;
            case 3:
                time = timeWork.getTue();
                break;
            case 4:
                time = timeWork.getWed();
                break;
            case 5:
                time = timeWork.getThu();
                break;
            case 6:
                time = timeWork.getFri();
                break;
            case 7:
                time = timeWork.getSat();
                break;
            case 1:
                time = timeWork.getSun();
                break;
            default:
                break;
        }
        String[] tw = time.split(" - ");
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date current;
        String status = getString(R.string.status_close);
        try {
            current = dateFormat.parse(dateFormat.format(currentDate));
            Date morningFirst = dateFormat.parse(tw[0]);
            Date morningSecond = dateFormat.parse(tw[1]);
            if (current.after(morningFirst) && current.before(morningSecond)) {
                status = getString(R.string.status_open);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }

    private void setStatus() {
        for (Device device : mDevices) {
            device.setStatus(isOpen(device.getTw()));
        }
    }
}
