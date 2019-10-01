package com.pkl.stafftracking;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.pkl.stafftracking.LokasiActivity.TAG_ID;
import static com.pkl.stafftracking.LokasiActivity.my_shared_preferences;

public class AndroidLocationServices extends Service {

    WakeLock wakeLock;
    SharedPreferences sharedpreferences;
    String user_id, latitude, longitude;

    private LocationManager locationManager;
    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            Log.e("Google", "Location Changed");
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://trackingforadmin.000webhostapp.com/lokasi.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {

                            // Showing response message coming from server.
                            Toast.makeText(AndroidLocationServices.this, ServerResponse, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            // Showing error message if something goes wrong.
                            Toast.makeText(AndroidLocationServices.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
                    //session = sharedpreferences.getBoolean(session_status, false);
                    user_id = sharedpreferences.getString(TAG_ID, null);
                    Calendar cal = Calendar.getInstance();
                    int second = cal.get(Calendar.SECOND);
                    int minute = cal.get(Calendar.MINUTE);
                    int hourofday = cal.get(Calendar.HOUR_OF_DAY); //24 hour format
                    String jam = hourofday + ":" + minute + ":" + second;
                    int mYear = cal.get(Calendar.YEAR);
                    int mMonth = cal.get(Calendar.MONTH);
                    //mMonth = mMonth + 1 ;
                    int mDay = cal.get(Calendar.DAY_OF_MONTH);
                    String tanggal = mYear + "-" + mMonth + "-" + mDay;
                    // Creating Map String Params.
                    Map<String, String> params = new HashMap<String, String>();

                    // Adding All values to Params.
                    params.put("user_id", user_id);
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    params.put("tanggal", tanggal);
                    params.put("jam", jam);
                    return params;
                }

            };

            // Creating RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(AndroidLocationServices.this);

            // Adding the StringRequest object into requestQueue.
            requestQueue.add(stringRequest);

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    };

    public AndroidLocationServices() {
        // TODO Auto-generated constructor stub
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        // Toast.makeText(getApplicationContext(), "Service Created",
        // Toast.LENGTH_SHORT).show();

        Log.e("Google", "Service Created");
        wakeLock.acquire();
       wakeLock.release();

    }

    @SuppressLint("MissingPermission")
    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

//		new ToggleGPS(getApplicationContext()).turnGPSOn();

        // Toast.makeText(getApplicationContext(), "Service Started",
        // Toast.LENGTH_SHORT).show();
        Log.e("Google", "Service Started");

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3600*1000, 0, listener);
        stopSelf();

        //return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();


    }

}
