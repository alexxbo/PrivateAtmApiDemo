
package com.alexx_bo.privatatm.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device implements Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("cityRU")
    @Expose
    private String cityRU;
    @SerializedName("cityUA")
    @Expose
    private String cityUA;
    @SerializedName("cityEN")
    @Expose
    private String cityEN;
    @SerializedName("fullAddressRu")
    @Expose
    private String fullAddressRu;
    @SerializedName("fullAddressUa")
    @Expose
    private String fullAddressUa;
    @SerializedName("fullAddressEn")
    @Expose
    private String fullAddressEn;
    @SerializedName("placeRu")
    @Expose
    private String placeRu;
    @SerializedName("placeUa")
    @Expose
    private String placeUa;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("tw")
    @Expose
    private Tw tw;

    private double distanceToCurrPos;
    private String status;

    protected Device(Parcel in) {
        type = in.readString();
        cityRU = in.readString();
        cityUA = in.readString();
        cityEN = in.readString();
        fullAddressRu = in.readString();
        fullAddressUa = in.readString();
        fullAddressEn = in.readString();
        placeRu = in.readString();
        placeUa = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        tw = in.readParcelable(Tw.class.getClassLoader());
        distanceToCurrPos = in.readDouble();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(cityRU);
        dest.writeString(cityUA);
        dest.writeString(cityEN);
        dest.writeString(fullAddressRu);
        dest.writeString(fullAddressUa);
        dest.writeString(fullAddressEn);
        dest.writeString(placeRu);
        dest.writeString(placeUa);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeParcelable(tw, flags);
        dest.writeDouble(distanceToCurrPos);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDistanceToCurrPos() {
        return distanceToCurrPos;
    }

    public void setDistanceToCurrPos(double distanceToCurrPos) {
        this.distanceToCurrPos = distanceToCurrPos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCityRU() {
        return cityRU;
    }

    public void setCityRU(String cityRU) {
        this.cityRU = cityRU;
    }

    public String getCityUA() {
        return cityUA;
    }

    public void setCityUA(String cityUA) {
        this.cityUA = cityUA;
    }

    public String getCityEN() {
        return cityEN;
    }

    public void setCityEN(String cityEN) {
        this.cityEN = cityEN;
    }

    public String getFullAddressRu() {
        return fullAddressRu;
    }

    public void setFullAddressRu(String fullAddressRu) {
        this.fullAddressRu = fullAddressRu;
    }

    public String getFullAddressUa() {
        return fullAddressUa;
    }

    public void setFullAddressUa(String fullAddressUa) {
        this.fullAddressUa = fullAddressUa;
    }

    public String getFullAddressEn() {
        return fullAddressEn;
    }

    public void setFullAddressEn(String fullAddressEn) {
        this.fullAddressEn = fullAddressEn;
    }

    public String getPlaceRu() {
        return placeRu;
    }

    public void setPlaceRu(String placeRu) {
        this.placeRu = placeRu;
    }

    public String getPlaceUa() {
        return placeUa;
    }

    public void setPlaceUa(String placeUa) {
        this.placeUa = placeUa;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Tw getTw() {
        return tw;
    }

    public void setTw(Tw tw) {
        this.tw = tw;
    }

}
