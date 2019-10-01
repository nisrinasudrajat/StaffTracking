package com.pkl.stafftracking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.pkl.stafftracking.LoginActivity.TAG_ID;
import static com.pkl.stafftracking.LoginActivity.my_shared_preferences;
import static com.pkl.stafftracking.PresensiActivity.getDayName;

public class LaporankegiatanActivity extends AppCompatActivity{

    Button SubmitButton, UploadButton;

    EditText ket ;
    TextView date, notif;
    SharedPreferences sharedpreferences;
    String user_id;

    Uri uri;

    public static final String PDF_UPLOAD_HTTP_URL = "https://trackingforadmin.000webhostapp.com/upload_file.php";
    public static final String my_shared_preferences = "my_shared_preferences";
    public final static String TAG_ID = "id";
    public int PDF_REQ_CODE = 1;

    String PdfPathHolder, PdfID;
    ProgressDialog progressDialog; // untuk membuat progressbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kegiatan);

        AllowRunTimePermission();

        SubmitButton = (Button) findViewById(R.id.Btn);
        UploadButton = (Button) findViewById(R.id.Button);
        ket = (EditText) findViewById(R.id.keterangan);
        date = (TextView) findViewById(R.id.mydate);
        notif = (TextView) findViewById(R.id.file);

        //menampilkan tanggal
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String mMonth = month_date.format(c.getTime());
        //int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String tanggal = mDay + " " + mMonth + " " + mYear;
        date.setText(tanggal);
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        user_id = sharedpreferences.getString(TAG_ID, null);
       // date.setText(user_id);

        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(LaporankegiatanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    SelectPdf();
                }else
                    ActivityCompat.requestPermissions(LaporankegiatanActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
            }

                // PDF selection code start from here .
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uri!=null){ //user telah memilih file
                    submituserpulang();
                    PdfUploadFunction(uri);
                }
                else
                    Toast.makeText(LaporankegiatanActivity.this, "Pilih File", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void submituserpulang() {
        // Showing progress dialog at user registration time.
        progressDialog = new ProgressDialog(LaporankegiatanActivity.this);
        progressDialog.setMessage("Please Wait, We are Inserting Your Data on Server");
        progressDialog.show();

        //get value from EditText.
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        user_id = sharedpreferences.getString(TAG_ID, null);

        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://trackingforadmin.000webhostapp.com/upload_files.php",
        new Response.Listener<String>() {
            @Override
            public void onResponse(String ServerResponse) {

                // Hiding the progress dialog after all task complete.
                progressDialog.dismiss();

                // Showing response message coming from server.
                Toast.makeText(LaporankegiatanActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(LaporankegiatanActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                String keterangan = ket.getText().toString();
                Calendar cal = Calendar.getInstance();
                int second = cal.get(Calendar.SECOND);
                int minute = cal.get(Calendar.MINUTE);
                int hourofday = cal.get(Calendar.HOUR_OF_DAY); //24 hour format
                String jam = hourofday + ":" + minute + ":" + second;
                String tanggal = date.getText().toString();
                int day = cal.get(Calendar.DAY_OF_WEEK);
                String hari = getDayName(day);

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", user_id);
                params.put("keterangan", keterangan);
                params.put("hari", hari);
                params.put("tanggal", tanggal);
                params.put("jam", jam);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(LaporankegiatanActivity.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

    private void SelectPdf() {
        Intent intent = new Intent();

        intent.setType("application/pdf");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            notif.setText("PDF is Selected");
        }else {
            Toast.makeText(LaporankegiatanActivity.this, "Silahkan pilih file", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    public void PdfUploadFunction(Uri link) {

        PdfPathHolder = FilePath.getPath(this, link);

        if (PdfPathHolder == null) {

            Toast.makeText(LaporankegiatanActivity.this, "Pilih File dari internal storage dan Coba lagi", Toast.LENGTH_SHORT).show();

        } else {

            try {

                PdfID = UUID.randomUUID().toString();
                new MultipartUploadRequest(this, PdfID, "https://trackingforadmin.000webhostapp.com/upload_file.php")
                        .addFileToUpload(PdfPathHolder, "pdf")
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5)
                        .startUpload();

                         // Hiding the progressDialog after done uploading.

            } catch (Exception exception) {

                Toast.makeText(LaporankegiatanActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(LaporankegiatanActivity.this, "File berhasil di upload", Toast.LENGTH_SHORT).show();
            ket.setText("");
            notif.setText("No File");
            progressDialog.dismiss();
            uri = null;
        }

    }


    public void AllowRunTimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(LaporankegiatanActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {

            Toast.makeText(LaporankegiatanActivity.this,"READ_EXTERNAL_STORAGE permission Access Dialog", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(LaporankegiatanActivity.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(LaporankegiatanActivity.this,"Permission Granted", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(LaporankegiatanActivity.this,"Permission Canceled", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}
