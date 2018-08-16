
package com.alexx_bo.privatatm.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tw implements Parcelable{

    @SerializedName("mon")
    @Expose
    private String mon;
    @SerializedName("tue")
    @Expose
    private String tue;
    @SerializedName("wed")
    @Expose
    private String wed;
    @SerializedName("thu")
    @Expose
    private String thu;
    @SerializedName("fri")
    @Expose
    private String fri;
    @SerializedName("sat")
    @Expose
    private String sat;
    @SerializedName("sun")
    @Expose
    private String sun;
    @SerializedName("hol")
    @Expose
    private String hol;

    protected Tw(Parcel in) {
        mon = in.readString();
        tue = in.readString();
        wed = in.readString();
        thu = in.readString();
        fri = in.readString();
        sat = in.readString();
        sun = in.readString();
        hol = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mon);
        dest.writeString(tue);
        dest.writeString(wed);
        dest.writeString(thu);
        dest.writeString(fri);
        dest.writeString(sat);
        dest.writeString(sun);
        dest.writeString(hol);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tw> CREATOR = new Creator<Tw>() {
        @Override
        public Tw createFromParcel(Parcel in) {
            return new Tw(in);
        }

        @Override
        public Tw[] newArray(int size) {
            return new Tw[size];
        }
    };

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getHol() {
        return hol;
    }

    public void setHol(String hol) {
        this.hol = hol;
    }

}
