package lecho.app.campus.service;

import java.util.Locale;

import lecho.app.campus.R;
import lecho.app.campus.dao.DaoMaster;
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
	protected void onHandleIntent(Intent intent) {
		SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, true).commit();
		if (intent.getBooleanExtra(Config.EXTRA_LANGUAGE_CHANGED, false)) {
			// delete data from database if language was changed, in other cases database will be upgraded by DaoMaster.
			DatabaseHelper.clearDB(getApplicationContext());
		}
		DataParser.loadCampusData(getApplicationContext(), R.raw.campus_data);
		// Set current data version.
		final String currentLanguage = Locale.getDefault().getLanguage();
		prefs.edit().putString(Config.APP_SHARED_PREFS_LANGUAGE, currentLanguage).commit();
		prefs.edit().putInt(Config.APP_SHARED_PREFS_SCHEMA_VERSION, DaoMaster.SCHEMA_VERSION).commit();
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, false).commit();
		Intent broadcastIntent = new Intent(BROADCAST_INTENT_FILTER);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
	}
}
