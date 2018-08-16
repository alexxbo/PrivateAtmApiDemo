package com.alexx_bo.privatatm.interfaces;

import com.alexx_bo.privatatm.model.ResponceData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PrivatApi {

    @GET("/p24api/infrastructure?json&atm")
    Call<ResponceData> getData(@Query("address") String address, @Query("city") String city);
}
