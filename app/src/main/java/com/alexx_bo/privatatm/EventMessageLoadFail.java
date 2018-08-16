package com.alexx_bo.privatatm;

public class EventMessageLoadFail {

    String message;

    public EventMessageLoadFail(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
