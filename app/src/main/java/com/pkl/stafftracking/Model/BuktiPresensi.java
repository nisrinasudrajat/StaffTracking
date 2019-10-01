package com.pkl.stafftracking.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BuktiPresensi {

    //Deklarasi Variable
    private String jam;
    private String hari;
    private String tanggal;
    private Double latitude;
    private Double longitude;
    private String imgUrl;
    private String key;

    // Fugsi Get
    public String getKey() {

        return key;
    }
    public Double getLatitude() {

        return latitude;
    }
    public Double getLongitude() {

        return longitude;
    }
    public String getImgUrl() {

        return imgUrl;
    }
    public String getJam() {

        return jam;
    }
    public String getTanggal() {

        return tanggal;
    }
    public String getHari() {

        return hari;
    }

    //Membuat Konstuktor kosong untuk membaca data snapshot
    public BuktiPresensi(){
    }

    //Konstruktor dengan beberapa parameter, untuk mendapatkan Input Data dari User
    public BuktiPresensi(String hari, String tanggal, String jam, Double latitude, Double longitude, String imgUrl) {
        this.hari = hari;
        this.tanggal = tanggal;
        this.jam = jam ;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgUrl = imgUrl;
    }
}


