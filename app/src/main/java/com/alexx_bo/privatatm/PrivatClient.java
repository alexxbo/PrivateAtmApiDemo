package com.alexx_bo.privatatm;

import com.alexx_bo.privatatm.interfaces.PrivatApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class PrivatClient {
    private static final String API_BASE_URL = "https://api.privatbank.ua";
    private static PrivatClient ourInstance;
    private PrivatApi privatApi;

    static PrivatClient getInstance() {
        if (ourInstance == null) {
            ourInstance = new PrivatClient();
        }
        return ourInstance;
    }

    private PrivatClient() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        privatApi = retrofit.create(PrivatApi.class);
    }

    public PrivatApi getPrivatApi(){
        return privatApi;
    }
}
