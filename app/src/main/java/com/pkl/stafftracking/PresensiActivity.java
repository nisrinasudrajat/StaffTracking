package com.pkl.stafftracking;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pkl.stafftracking.Model.AlasanAbsen;
import com.pkl.stafftracking.Model.BuktiPresensi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PresensiActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private static final int PERMISSION_CODE = 1000;
    private int IMAGE_CAPTURE_CODE = 1001;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE_IMG_PICK = 1001;
    private static final String TAG = "PresensiActivity";

    //inisialisasi
    Button mTakePhotoBtn;
    Button mUploadPhotoBtn;
    Button submit;
    ImageView mUserPhoto;

    TextView presensi_popup_text, presensi_popup_text2, jarak;
    Button presensi_popup_button;
    Dialog presensi_dialog;

    Uri image_uri; //get url image

    String storage_Path = "images/";
    String database_Path = "presensi";

    private ProgressDialog mProgressD;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    Uri drawable_uri = Uri.parse("android.resource://com.pkl.stafftracking/drawable/border_blue");

    private EditText presensi;
    private TextView Lat,Long;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LatLng latLng;
    private boolean isPermission;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    double lat,longi, lat2, longi2;
    private DatabaseReference reff;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presensi);

        //getSupportActionBar().setTitle("Presensi");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presensi = findViewById(R.id.presensi_box);

        Lat = (TextView) findViewById((R.id.latitude_presensi));
        Long = (TextView) findViewById((R.id.longitude_presensi));
        jarak = (TextView) findViewById((R.id.jarak));
        mUploadPhotoBtn = findViewById(R.id.button_upload_photo);
        mUserPhoto = findViewById(R.id.user_photo);
        mTakePhotoBtn = findViewById(R.id.button_take_photo);
        submit = findViewById(R.id.presensi_tombol);

        mProgressD = new ProgressDialog(PresensiActivity.this);
        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        // mengambil referensi ke Firebase Database
         FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = currentFirebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userid).child(database_Path);
        //Dialog popup on presensi when finish uplading data
        presensi_dialog = new Dialog(PresensiActivity.this);
        //
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //get long lat kantor
        getlonglat();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation(); //check whether location service is enable or not in your  phone

        // terjadi jika user akan izin (mengisi kolom dan menekan tombol submit)
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //user izin tidak hadir
                Calendar cal = Calendar.getInstance();
                int second = cal.get(Calendar.SECOND);
                int minute = cal.get(Calendar.MINUTE);
                int hourofday = cal.get(Calendar.HOUR_OF_DAY); //24 hour format
                String jam = hourofday + ":" + minute + ":" + second;
                int mYear = cal.get(Calendar.YEAR);
                int mMonth = cal.get(Calendar.MONTH);
                mMonth = mMonth + 1;
                int mDay = cal.get(Calendar.DAY_OF_MONTH);
                String tanggal = mDay + "/" + mMonth + "/" + mYear;
                int day = cal.get(Calendar.DAY_OF_WEEK);
                String hari = getDayName(day);
                if(!isEmpty(presensi.getText().toString())){
                    submitAlasan(new AlasanAbsen(hari, tanggal, jam, presensi.getText().toString()));
                }
                else
                    Toast.makeText(PresensiActivity.this,"Tulis alasan Anda Absen", Toast.LENGTH_LONG).show();

                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        presensi.getWindowToken(), 0);
            }
        });
        //Button click take picture
        mTakePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmellow, req runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED) {
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to req permissions
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        openCamera();
                    }
                } else {
                    //system os < marshmellow
                    openCamera();
                }
            }
        });

        // klik btn kirim foto
        mUploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageFileToFirebaseStorage();
            }
        });
    }

    private void getlonglat() {
        auth = FirebaseAuth.getInstance();
        // mengambil referensi ke Firebase Database
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = currentFirebaseUser.getUid();
        reff = FirebaseDatabase.getInstance().getReference().child("User").child(userid);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double lat2 = (double) dataSnapshot.child("latitude").getValue();
                double longi2 = (double) dataSnapshot.child("longitude").getValue();
                // calculate distance
                double jrk = distance(lat,longi,lat2,longi2);
                jarak.setText(String.format("%.2f", jrk)+" KM");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.TITLE, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //called when user deny or allow req permission popup
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission from pop up was granted
                    openCamera();
                } else {
                    //permission denied
                    Toast.makeText(PresensiActivity.this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (image_uri != null) {

            // Setting progressDialog Title.
            mProgressD.setTitle("Mengirim gambar...");
            mProgressD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            // Showing progressDialog.
            mProgressD.show();
            mAuth = FirebaseAuth.getInstance();
            // mengambil referensi ke Firebase Database
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userid = currentFirebaseUser.getUid();
            // Creating second StorageReference.
            StorageReference storageReference2nd = mStorageRef.child(userid).child(storage_Path + System.currentTimeMillis() + "." + GetFileExtension(image_uri));
            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Calendar cal = Calendar.getInstance();
                            int second = cal.get(Calendar.SECOND);
                            int minute = cal.get(Calendar.MINUTE);
                            int hourofday = cal.get(Calendar.HOUR_OF_DAY); //24 hour format
                            String jam = hourofday + ":" + minute + ":" + second;
                            int mYear = cal.get(Calendar.YEAR);
                            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                            String mMonth = month_date.format(cal.getTime());
                            int mDay = cal.get(Calendar.DAY_OF_MONTH);
                            String tanggal = mDay + " " + mMonth + " " + mYear;
                            int day = cal.get(Calendar.DAY_OF_WEEK);
                            String hari = getDayName(day);
                            //
                            String url= taskSnapshot.getDownloadUrl().toString(); //return url
                            // get location
                            String latitude = Lat.getText().toString();
                            String longitude =  Long.getText().toString();
                            lat = Double.parseDouble(latitude);
                            longi = Double.parseDouble(longitude);
                            @SuppressWarnings("VisibleForTests")
                            BuktiPresensi PresensiInfo = new BuktiPresensi(hari, tanggal, jam, lat,longi, url);
                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();
                            //DatabaseReference databaseReference = .getReference(); //
                            databaseReference.child("Hadir").child(ImageUploadId).setValue(PresensiInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Toast.makeText(PresensiActivity.this, "File berhasil di upload", Toast.LENGTH_SHORT).show();
                                        // Hiding the progressDialog after done uploading.
                                        mProgressD.dismiss();
                                        //Showing popup dialog after done uplading
                                        showPresensiPopup();
                                        mUserPhoto.setImageURI(drawable_uri); //return kosong
                                    }
                                    else
                                        Toast.makeText(PresensiActivity.this, "Gambar tidak berhasil di upload", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            mProgressD.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(PresensiActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressD.setProgress(currentProgress);
                        }
                    });
        }
        else {

            Toast.makeText(PresensiActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //called when image was captured from camera

        if (resultCode == RESULT_OK) {
            //set captured img to image view
            //image_uri = data.getData();
            mUserPhoto.setImageURI(image_uri);
           // Lat.setText(String.valueOf(location.getLatitude())); // menampilkan lokasi
            //Long.setText(String.valueOf(location.getLongitude() ));

        }
    }
    private void submitAlasan(AlasanAbsen alasanAbsen) {
        // FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // String userid = currentFirebaseUser.getUid();

        databaseReference.child("Absen").push().setValue(alasanAbsen).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                presensi.setText("");
            }
        });
    }

    public static String getDayName(int day){
        switch(day){
            case 1:
                return "Minggu";
            case 2:
                return "Senin";
            case 3:
                return "Selasa";
            case 4:
                return "Rabu";
            case 5:
                return "Kamis";
            case 6:
                return  "Jumat";
            case 7:
                return "Sabtu";
        }

        return "Error";
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
        Lat.setText(String.valueOf(location.getLatitude()));
        Long.setText(String.valueOf(location.getLongitude() ));
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