package com.pkl.stafftracking;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class LocationService extends IntentService {

	private String TAG = this.getClass().getSimpleName();

	public LocationService() {
		super("Fused Location");
	}

	public LocationService(String name) {
		super("Fused Location");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Location location = intent.getParcelableExtra(TAG);
		if (location != null) {
			Log.i(TAG, "onHandleIntent " + location.getLatitude() + ","
					+ location.getLongitude());
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Builder noti = new Builder(this);
			noti.setContentTitle("Sks Location Client");
			noti.setContentText(location.getLatitude() + ","
					+ location.getLongitude());
			// noti.setSmallIcon(R.drawable.ic_la);

			notificationManager.notify(1234, noti.build());

		}

	}

}
