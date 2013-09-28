package lecho.app.campus.service;

import lecho.app.campus.R;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DataParser;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class PopulateDBIntentService extends IntentService {
	private static final String TAG = "PopulateDBIntentService";

	public PopulateDBIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		// int campusDataVersion = prefs.getInt(Config.APP_SHARED_PREFS_CAMPUS_DATA_VERSION,
		// Config.CAMPUS_DATA_VERSION);
		// if(Config.CAMPUS_DATA_VERSION != campusDataVersion){
		//
		// }
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, true).commit();
		DataParser.loadCampusData(getApplicationContext(), R.raw.campus_data);
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, false).commit();
	}
}
