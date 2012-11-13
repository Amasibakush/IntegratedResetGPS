package secure.phone;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StartActivity extends Activity {
    Button btnShowLocation;
    
    
    static final String TAG = "StartActivity";
	static final int ACTIVATION_REQUEST=0;
	DevicePolicyManager devicePolicyManager;
	ComponentName DeviceAdmin;
	ToggleButton toggleButton;

    
    OnClickListener stop = new OnClickListener() {

		public void onClick(View v) {
			SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor edit = mypref.edit();
			edit.putBoolean("stopped", true);
			edit.commit();
			//
			onalarm.stopit(getApplicationContext());

		}
	};
	OnClickListener showlatest = new OnClickListener() {

		public void onClick(View v) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String latitude = pref.getString("latitude", "0.0");
			String longitude = pref.getString("longitude", "0.0");
			TextView lat = (TextView)findViewById(R.id.latitude);
			TextView lon = (TextView)findViewById(R.id.longitude);
			lat.setText(latitude);
			lon.setText(longitude);
		}
	};
	
    // GPSTracker class
    private MyScheduleReceiver onalarm;
    
    OnCheckedChangeListener adminActivator = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			
			if (isChecked) {
				// Activate device administration
				Intent intent = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						DeviceAdmin);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						"Explanation goes here");
				startActivityForResult(intent, ACTIVATION_REQUEST);
			}
			Log.d(TAG, "onCheckedChanged to: " + isChecked);
			
			switch (buttonView.getId()) {
    		case R.id.button_lock_device:
    			// We lock the screen
    			//Toast.makeText(this, "Locking device...", Toast.LENGTH_LONG).show();
    			Log.d(TAG, "Locking device now");
    			devicePolicyManager.lockNow();
    			break;
    		case R.id.button_reset_device:
    			// We reset the device - this will erase entire /data partition!
    			//Toast.makeText(, "Locking device...", Toast.LENGTH_LONG).show();
    			Log.d(TAG,
    					"RESETing device now - all user data will be ERASED to factory settings");
    			devicePolicyManager.wipeData(ACTIVATION_REQUEST);
    			break;
			}	
		}
    };
    
    OnClickListener reset = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d(TAG,
					"RESETing device now - all user data will be ERASED to factory settings");
			devicePolicyManager.wipeData(ACTIVATION_REQUEST);
		}
    };
    
    
    OnClickListener lock = new OnClickListener() {

 		public void onClick(View v) {
 			// TODO Auto-generated method stub
 			Log.d(TAG, "Locking device now");
			devicePolicyManager.lockNow();
 		}
     };
     
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
        toggleButton = (ToggleButton) super
				.findViewById(R.id.toggle_device_admin);
		toggleButton.setOnCheckedChangeListener(adminActivator);

		// Initialize Device Policy Manager service and our receiver class
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		DeviceAdmin = new ComponentName(this, MyDeviceAdminReceiver.class);
		
        
        
        onalarm = new MyScheduleReceiver();
        btnShowLocation = (Button)findViewById(R.id.findlocation);
     // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View arg0) {
            	SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    			Editor edit = mypref.edit();
    			edit.putBoolean("stopped", false);
    			edit.commit();
                // create class object
            	onalarm.schedule(StartActivity.this); 
            }
        });
        Button stoplocating = (Button)findViewById(R.id.stoplocating);
        stoplocating.setOnClickListener(stop);
        Button showl = (Button)findViewById(R.id.showlocationButton);
        showl.setOnClickListener(showlatest);
        
        Button resetBtn = (Button)findViewById(R.id.button_reset_device);
       resetBtn.setOnClickListener(reset);
       
       Button lockBtn = (Button)findViewById(R.id.button_lock_device);
       lockBtn.setOnClickListener(lock);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_start, menu);
        return true;
    }
    

	/**
	 * Called when startActivityForResult() call is completed. The result of
	 * activation could be success of failure, mostly depending on user okaying
	 * this app's request to administer the device.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ACTIVATION_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Administration enabled!");
				toggleButton.setChecked(true);
			} else {
				Log.i(TAG, "Administration enable FAILED!");
				toggleButton.setChecked(false);
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
  
    }
}