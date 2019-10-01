package com.pkl.stafftracking.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Pulang {
    //Deklarasi Variable
    private String hari;
    private String tanggal;
    private String jam;
    private String nama;

    public Pulang(String hari, String tanggal, String jam, String nama) {
        this.hari = hari;
        this.tanggal = tanggal;
        this.jam = jam;
        this.nama = nama;
    }

    public String getHari() {
        return hari;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJam() {
        return jam;
    }

    public String getNama() { return nama; }
}
