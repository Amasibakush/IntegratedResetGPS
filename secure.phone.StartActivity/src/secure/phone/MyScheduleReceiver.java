package secure.phone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;



public class MyScheduleReceiver extends BroadcastReceiver {
	AlarmManager am;
	PendingIntent pi;

	@Override
	public void onReceive(Context context, Intent intent) {
		MyLocationListener gps = new MyLocationListener(context);
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			// TextView lat = (TextView)findViewById(R.id.latitude);
			// lat.setText(String.valueOf(latitude));
			// TextView lon = (TextView)findViewById(R.id.longitude);
			// lon.setText(String.valueOf(longitude));
			SharedPreferences mypref = PreferenceManager
					.getDefaultSharedPreferences(context);
			Editor myEditor = mypref.edit();
			myEditor.putString("latitude", String.valueOf(latitude));
			myEditor.putString("longitude", String.valueOf(longitude));
			myEditor.commit();
			// \n is for new line
			Toast.makeText(context,"Your Location is - \nLat: " + latitude + "\nLong: "+ longitude, Toast.LENGTH_LONG).show();
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}

	}

	public void schedule(Context context) {
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, MyScheduleReceiver.class);
		// intent.putExtra(ONE_TIME, Boolean.FALSE);
		pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// After after 15 seconds
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 5, pi);
	}

	public void stopit(Context context)
	{
		SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean stopped = mypref.getBoolean("stopped", false);
        if(stopped){
        	try{
        		am.cancel(pi);
        	}
        	finally {
        	
        	}
        	
        }

	}
}