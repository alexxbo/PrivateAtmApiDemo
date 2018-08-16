package com.alexx_bo.privatatm;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alexx_bo.privatatm.adapter.AtmAdapter;
import com.alexx_bo.privatatm.model.Device;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class AtmListFragment extends Fragment {

    private List<Device> mDevices;
    private AtmAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private AtmAdapter.OnClickListener mapClickListener;

    public AtmListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevices = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atm_list, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = view.findViewById(R.id.atm_recycler);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AtmAdapter(mDevices);
        mAdapter.setOnClickListener(mapClickListener);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AtmAdapter.OnClickListener) {
            mapClickListener = (AtmAdapter.OnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapClickListener = null;
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
        if (mDevices.size() > 0) {
            mDevices.clear();
        }
        mDevices = event.getDevices();
        mAdapter.updateData(mDevices);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventLoadFail(EventMessageLoadFail event) {
        Toast.makeText(getContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
