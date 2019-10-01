package com.pkl.stafftracking.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AlasanAbsen {

    //Deklarasi Variable
    private String jam;
    private String hari;
    private String tanggal;
    private String alasan_absen;
    private String key;

    //Fungsi get
    public String getKey() {

        return key;
    }

    public String getAlasan_absen() {

        return alasan_absen;
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

    //Membuat Konstuktor kosong untuk membaca data
    public AlasanAbsen(){
    }

    //Konstruktor dengan beberapa parameter, untuk mendapatkan Input Data dari User
    public AlasanAbsen(String hari, String tanggal, String jam, String alasan_absen) {
        this.hari = hari;
        this.tanggal = tanggal;
        this.jam = jam ;
        this.alasan_absen = alasan_absen;
    }
}

