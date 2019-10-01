package com.pkl.stafftracking;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.pkl.stafftracking.PresensiActivity.getDayName;
import static com.pkl.stafftracking.PresensiActivity.distance;

public class KehadiranActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    Button TakeImageButton, UploadImageOnServerButton, submit;
    private static final String TAG = "KehadiranActivity";
    ImageView ShowSelectedImage;
    private EditText presensi;
    TextView textLat,textLong;
    TextView presensi_popup_text, presensi_popup_text2, jarak;
    Button presensi_popup_button;
    Dialog presensi_dialog;
    RequestQueue requestQueue;
    ProgressDialog pDialog;

    Bitmap FixBitmap;

    String UserID ="userid";
    String Nama = "nama";
    String ImageData = "image_url" ;
    String Day = "day";
    String Date = "date" ;
    String Time = "time";
    String Lat = "lat";
    String Longi = "longi";
    String Ket = "keterangan";
    double lat,longi, lat2, longi2,lat1,longi1;
    String nama_user;
    int index;

    ProgressDialog progressDialog ;
    Uri drawable_uri = Uri.parse("android.resource://com.pkl.stafftracking/drawable/border_blue");
    String HttpUrl = "https://trackingforadmin.000webhostapp.com/upload_absen.php";
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    String hari,tanggal,jam, user_id, latitude,longitude, alasan_absen;
    private StringRequest stringRequest;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    public final static String TAG_ID = "id";
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;
    SharedPreferences sharedpreferences;
    public static final String my_shared_preferences = "my_shared_preferences";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presensi);

        TakeImageButton = (Button)findViewById(R.id.button_take_photo);
        UploadImageOnServerButton = (Button)findViewById(R.id.button_upload_photo);
        ShowSelectedImage = (ImageView)findViewById(R.id.user_photo);
        submit = findViewById(R.id.presensi_tombol);
        presensi = findViewById(R.id.presensi_box);
        textLat = (TextView) findViewById((R.id.latitude_presensi));
        textLong = (TextView) findViewById((R.id.longitude_presensi));
        jarak = (TextView) findViewById((R.id.jarak));
        presensi_dialog = new Dialog(KehadiranActivity.this);
        requestQueue = Volley.newRequestQueue(KehadiranActivity.this);
        pDialog = new ProgressDialog(KehadiranActivity.this);
        //mendapatkan user yang sedang login
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        user_id = sharedpreferences.getString(TAG_ID, null);
        //untuk maps
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //get long lat kantor
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation(); //check whether location service is enable or not in your  phone
        //get long lat untuk distance
        //getlonglat();
        //untuk data kalender
        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY); //24 hour format
        jam = hourofday + ":" + minute + ":" + second;
        int mYear = cal.get(Calendar.YEAR);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String mMonth = month_date.format(cal.getTime());
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        tanggal = mDay + " " + mMonth + " " + mYear;
        int day = cal.get(Calendar.DAY_OF_WEEK);
        hari = getDayName(day);

        byteArrayOutputStream = new ByteArrayOutputStream();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAlasanAbsen();
            }
        });

        TakeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude = textLat.getText().toString();
                longitude =  textLong.getText().toString();
                lat = Double.parseDouble(latitude);
                longi = Double.parseDouble(longitude);
                UploadImageToServer();
            }
        });

        if (ContextCompat.checkSelfPermission(KehadiranActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    private void getlonglat() {
        String link = "https://trackingforadmin.000webhostapp.com/getlatlong.php";
        final ArrayList<HashMap<String, String>> list_data;
        requestQueue = Volley.newRequestQueue(KehadiranActivity.this);
        list_data = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.GET, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = new JSONArray(response);
                    for (int a = 0; a < array.length(); a ++){
                        JSONObject json = array.getJSONObject(a);
                        HashMap<String, String> map  = new HashMap<String, String>();
                        map.put("id", json.getString("user_id"));
                        map.put("nama", json.getString("nama"));
                        map.put("latitude", json.getString("latitude"));
                        map.put("longitude", json.getString("longitude"));
                        list_data.add(map);
                    }
                    for (index = 0; index < array.length(); index++) {
                        String i = list_data.get(index).get("id");
                        if(i.equals(user_id)){
                          String a = list_data.get(index).get("latitude");
                          String b = list_data.get(index).get("longitude");
                          nama_user = list_data.get(index).get("nama");
                          lat2 = Double.parseDouble(a);
                          longi2 = Double.parseDouble(b);
                            }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(KehadiranActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);

        double jrk = distance(lat1,longi1,lat2,longi2);
        jarak.setText(String.format("%.2f", jrk)+" KM");
    }

    private void submitAlasanAbsen() {
        // Showing progress dialog at user registration time.
        pDialog.setMessage("Please Wait, We are Inserting Your Data on Server");
        pDialog.show();

        //get value from EditText.
        alasan_absen = presensi.getText().toString().trim();

        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Hiding the progress dialog after all task complete.
                        pDialog.dismiss();

                        // Showing response message coming from server.
                        Toast.makeText(KehadiranActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        pDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(KehadiranActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("user_id", user_id);
                params.put("alasan_absen", alasan_absen);
                params.put("hari", hari);
                params.put("tanggal", tanggal);
                params.put("jam", jam);
                params.put("keterangan", "tidak hadir");
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(KehadiranActivity.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(KehadiranActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                ShowSelectedImage.setImageURI(drawable_uri);

                Toast.makeText(KehadiranActivity.this,string1,Toast.LENGTH_LONG).show();
                showPresensiPopup();
            }

            @Override
            protected String doInBackground(Void... params) {
                String keterangan = "hadir";
                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(UserID, user_id);
                HashMapParams.put(Nama, nama_user);
                HashMapParams.put(ImageData, ConvertImage);
                HashMapParams.put(Day, hari);
                HashMapParams.put(Date, tanggal);
                HashMapParams.put(Time, jam);
                HashMapParams.put(Lat, latitude);
                HashMapParams.put(Longi, longitude);
                HashMapParams.put(Ket, keterangan);

                String FinalData = imageProcessClass.ImageHttpRequest("https://trackingforadmin.000webhostapp.com/upload_image.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();

    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(KehadiranActivity.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void showPresensiPopup() {
        presensi_dialog.setContentView(R.layout.activity_presensi_popup);
        presensi_popup_button = (Button)presensi_dialog.findViewById(R.id.presensi_popup_button);
        presensi_popup_text = (TextView)presensi_dialog.findViewById(R.id.presensi_popup_text);
        presensi_popup_text2 = (TextView)presensi_dialog.findViewById(R.id.presensi_popup_text2);

        presensi_popup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presensi_dialog.dismiss();
            }
        });

        presensi_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        presensi_dialog.show();
    }

    private boolean isEmpty(String s) {
        // Cek apakah ada fields yang kosong, sebelum disubmit
        return TextUtils.isEmpty(s);
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +

                Double.toString(location.getLongitude());
        textLat.setText(String.valueOf(location.getLatitude()));
        textLong.setText(String.valueOf(location.getLongitude() ));
        latitude = textLat.getText().toString();
        longitude =  textLong.getText().toString();
        lat1 = Double.parseDouble(latitude);
        longi1 = Double.parseDouble(longitude);
        getlonglat();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
