package lecho.app.campus.service;

import lecho.app.campus.R;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DataParser;
import lecho.app.campus.utils.DatabaseHelper;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Service for clearing database, parsing xml file and inserting parsed data into database.
 * 
 * @author Lecho
 * 
 */
public class PopulateDBIntentService extends IntentService {
	private static final String TAG = "PopulateDBIntentService";
	public static final String BROADCAST_INTENT_FILTER = "lecho.app.campus:POPULATE_DB_BROADCAST";

	public PopulateDBIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, true).commit();
		DatabaseHelper.clearDB(getApplicationContext());
		DataParser.loadCampusData(getApplicationContext(), R.raw.campus_data);
		// Set current data version.
		prefs.edit().putInt(Config.APP_SHARED_PREFS_CAMPUS_DATA_VERSION, Config.CAMPUS_DATA_VERSION).commit();
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, false).commit();
		Intent intent = new Intent(BROADCAST_INTENT_FILTER);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
	}
}
