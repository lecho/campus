package lecho.app.campus.activity;

import lecho.app.campus.R;
import lecho.app.campus.service.PopulateDBIntentService;
import lecho.app.campus.utils.Config;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class StartActivity extends SherlockFragmentActivity {

	private BroadcastReceiver mPopulateDBReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			goToCampusMapActivity();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		if (null == savedInstanceState) {
			// Check if there is new data version.
			SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
			int campusDataVersion = prefs.getInt(Config.APP_SHARED_PREFS_CAMPUS_DATA_VERSION, 0);
			if (Config.CAMPUS_DATA_VERSION != campusDataVersion) {
				// Database has to be upgraded.
				prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, true).commit();
				Intent serviceIntent = new Intent(getApplicationContext(), PopulateDBIntentService.class);
				startService(serviceIntent);
			} else {
				// No action needed, go to map.
				goToCampusMapActivity();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		boolean isParsingOngoing = prefs.getBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, false);
		if (!isParsingOngoing) {
			goToCampusMapActivity();
		} else {
			IntentFilter intentFilter = new IntentFilter(PopulateDBIntentService.BROADCAST_INTENT_FILTER);
			LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mPopulateDBReceiver,
					intentFilter);
		}
	}

	private void goToCampusMapActivity() {
		Intent intent = new Intent(StartActivity.this, CampusMapActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mPopulateDBReceiver);
	}

}
