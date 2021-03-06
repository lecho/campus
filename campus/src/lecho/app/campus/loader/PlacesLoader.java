package lecho.app.campus.loader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lecho.app.campus.dao.CategoryDao;
import lecho.app.campus.dao.DaoSession;
import lecho.app.campus.dao.FacultyDao;
import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.PlaceCategoryDao;
import lecho.app.campus.dao.PlaceDao;
import lecho.app.campus.dao.PlaceFacultyDao;
import lecho.app.campus.dao.PlaceUnitDao;
import lecho.app.campus.dao.UnitDao;
import lecho.app.campus.service.PopulateDBIntentService;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DatabaseHelper;
import lecho.app.campus.utils.PlacesList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Loads Places from database, name, symbol etc. Loader register broadcast receiver to listen when
 * {@link PopulateDBIntentService} finish its work.
 * 
 * Implementation based on sample from android sdk documentation.
 * 
 * @author Lecho
 * 
 */
public class PlacesLoader extends AsyncTaskLoader<PlacesList> {
	private static final String TAG = "PlacesLoader";
	public static final String EXTRA_ACTION = "lecho.app.campus:ACTION";
	public static final String EXTRA_ARGUMENT = "lecho.app.campus:ARG";
	// No args
	public static final int LOAD_ALL_PLACES = 1;
	// One arg - category id
	public static final int LOAD_PLACES_BY_CATEGORY = 2;
	public static final int LOAD_PLACES_BY_FACULTY = 3;
	// One arg - faculty id
	public static final int LOAD_PLACES_BY_SEARCH = 4;
	private static final String QUERY_FILTER_PLACES_BY_CATEGORY = "LEFT JOIN " + PlaceCategoryDao.TABLENAME
			+ " PC ON T." + PlaceDao.Properties.Id.columnName + "=PC." + PlaceCategoryDao.Properties.PlaceId.columnName
			+ " LEFT JOIN " + CategoryDao.TABLENAME + " C ON PC." + PlaceCategoryDao.Properties.CategoryId.columnName
			+ "=C." + CategoryDao.Properties.Id.columnName + " WHERE C." + CategoryDao.Properties.Name.columnName
			+ "=?";
	private static final String QUERY_FILTER_PLACES_BY_FACULTY = "LEFT JOIN " + PlaceFacultyDao.TABLENAME + " PF ON T."
			+ PlaceDao.Properties.Id.columnName + "=PF." + PlaceFacultyDao.Properties.PlaceId.columnName
			+ " LEFT JOIN " + FacultyDao.TABLENAME + " F ON PF." + PlaceFacultyDao.Properties.FacultyId.columnName
			+ "=F." + FacultyDao.Properties.Id.columnName + " WHERE F."
			+ FacultyDao.Properties.FilterableName.columnName + "=?";
	// Search by place name, place symbol, place description, units names
	// inside particular place(5 arguments for ?).
	private static final String QUERY_SEARCH_PLACES = "WHERE T." + PlaceDao.Properties.Symbol.columnName
			+ " LIKE ? OR T." + PlaceDao.Properties.Name.columnName + " LIKE ? OR T."
			+ PlaceDao.Properties.Description.columnName + " LIKE ? OR T." + PlaceDao.Properties.Id.columnName
			+ " IN( SELECT PU." + PlaceUnitDao.Properties.PlaceId.columnName + " FROM " + PlaceUnitDao.TABLENAME
			+ " PU WHERE EXISTS( SELECT U.* FROM " + UnitDao.TABLENAME + " U WHERE PU."
			+ PlaceUnitDao.Properties.UnitId.columnName + "=U." + UnitDao.Properties.Id.columnName + " AND (U."
			+ UnitDao.Properties.Name.columnName + " LIKE ? OR U." + UnitDao.Properties.ShortName.columnName
			+ " LIKE ?)))";

	private DaoSession mDaoSession;
	private PlacesList mData;
	private int mAction;
	private String mArgument;
	private CampusDataObserver mCampusDataObserver;

	public PlacesLoader(Context context, int action, String arg) {
		super(context);
		mAction = action;
		mArgument = arg;
	}

	@Override
	public PlacesList loadInBackground() {
		mDaoSession = DatabaseHelper.getDaoSession(getContext());
		PlaceDao placeDao = mDaoSession.getPlaceDao();
		List<Place> places = null;
		switch (mAction) {
		case LOAD_ALL_PLACES:
			if (Config.DEBUG) {
				Log.d(TAG, "Loading all places");
			}
			places = placeDao.loadAll();
			break;
		case LOAD_PLACES_BY_CATEGORY:
			if (Config.DEBUG) {
				Log.d(TAG, "Loading places by category with arguments " + mArgument);
			}
			places = placeDao.queryRaw(QUERY_FILTER_PLACES_BY_CATEGORY, mArgument);
			break;
		case LOAD_PLACES_BY_FACULTY:
			if (Config.DEBUG) {
				Log.d(TAG, "Loading places by faculty with arguments " + mArgument);
			}
			places = placeDao.queryRaw(QUERY_FILTER_PLACES_BY_FACULTY, mArgument);
			break;
		case LOAD_PLACES_BY_SEARCH:
			if (Config.DEBUG) {
				Log.d(TAG, "Loading places by search with arguments " + mArgument);
			}
			StringBuilder sb = new StringBuilder("%").append(mArgument).append("%");
			String arg = sb.toString();
			String[] args = new String[5];
			Arrays.fill(args, arg);
			places = placeDao.queryRaw(QUERY_SEARCH_PLACES, args);
			break;
		default:
			throw new IllegalArgumentException("Invalid PlacesLoader action parameter: " + mAction);
		}
		// I don't want to return null.
		if (null == places) {
			places = Collections.emptyList();
		}
		return new PlacesList(mAction, places);
	}

	/**
	 * Called when there is new data to deliver to the client. The super class will take care of delivering it; the
	 * implementation here just adds a little more logic.
	 */
	@Override
	public void deliverResult(PlacesList data) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (data != null) {
				onReleaseResources(data);
			}
		}
		PlacesList oldData = mData;
		mData = data;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(data);
		}

		// At this point we can release the resources associated with
		// 'oldData' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldData != null && oldData != data) {
			onReleaseResources(oldData);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (mData != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mData);
		}

		// Start watching for changes in the app data.
		if (mCampusDataObserver == null) {
			mCampusDataObserver = new CampusDataObserver(this);
		}
		if (null == mData || takeContentChanged()) {
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(PlacesList data) {
		super.onCanceled(data);

		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(data);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (mData != null) {
			onReleaseResources(mData);
			mData = null;
		}

		// The Loader is being reset, so we should stop monitoring for changes.
		if (mCampusDataObserver != null) {
			LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mCampusDataObserver);
			mCampusDataObserver = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an actively loaded data set.
	 */
	protected void onReleaseResources(PlacesList data) {
	}

	/**
	 * Listen for broadcast from service that insert data into database.
	 * 
	 * @author Lecho
	 * 
	 */
	private static class CampusDataObserver extends BroadcastReceiver {

		private PlacesLoader mLoader;

		public CampusDataObserver(final PlacesLoader loader) {
			mLoader = loader;
			IntentFilter intentFilter = new IntentFilter(PopulateDBIntentService.BROADCAST_INTENT_FILTER);
			LocalBroadcastManager.getInstance(mLoader.getContext()).registerReceiver(this, intentFilter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Config.DEBUG) {
				Log.d(TAG, "Received data change broadcast");
			}
			mLoader.onContentChanged();
		}

	}
}
