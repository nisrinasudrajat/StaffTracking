package com.pkl.stafftracking;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import static com.pkl.stafftracking.LoginActivity.TAG_ID;
import static com.pkl.stafftracking.LoginActivity.TAG_USERNAME;

public class HomeActivity extends AppCompatActivity {

    CardView cardView_presensi, cardView_kegiatan, cardView_laporan, cardView_lokasi;
    Button btnLogout;

    Dialog logout_dialog;
    Button logout_ok, logout_cancel;
    TextView logout_text, logout_text2;
    SharedPreferences sharedpreferences;
    String id, username;

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardView_presensi = findViewById(R.id.cardView_presensi);
        cardView_kegiatan = findViewById(R.id.cardView_kegiatan);
        cardView_laporan = findViewById(R.id.cardView_laporan);
        cardView_lokasi = findViewById(R.id.cardView_lokasi);
        btnLogout = findViewById(R.id.home_logout);

        sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);

        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);


        logout_dialog = new Dialog(HomeActivity.this);

        cardView_presensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KehadiranActivity.class);
                startActivity(intent);
            }
        });


        cardView_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        cardView_kegiatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LaporankegiatanActivity.class);
                startActivity(intent);
            }
        });

        cardView_lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LokasiActivity.class);
                startActivity(intent);
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

    }



    private void showLogoutDialog() {

        logout_dialog.setContentView(R.layout.activity_logout_popup);
        logout_ok = (Button)logout_dialog.findViewById(R.id.logout_ok);
        logout_cancel = (Button)logout_dialog.findViewById(R.id.logout_cancel);
        logout_text = (TextView)logout_dialog.findViewById(R.id.logout_text);
        logout_text2 = (TextView)logout_dialog.findViewById(R.id.logout_text2);

        logout_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logout_dialog.show();

        logout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_dialog.dismiss();
            }
        });



        logout_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(LoginActivity.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.commit();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
                logout_dialog.dismiss();
            }
        });


    }
}
