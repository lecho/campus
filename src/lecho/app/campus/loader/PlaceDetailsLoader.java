package lecho.app.campus.loader;

import java.util.List;

import lecho.app.campus.dao.DaoSession;
import lecho.app.campus.dao.FacultyDao;
import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.PlaceDao;
import lecho.app.campus.dao.PlaceUnitDao;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.dao.UnitDao;
import lecho.app.campus.utils.DatabaseHelper;
import lecho.app.campus.utils.PlaceDetails;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Loads Place details from database, name, symbol, faculties etc. Loader doesn't watch for changes in database, there's
 * no need for that in this app.
 * 
 * Implementation based on sample from android sdk documentation.
 * 
 * @author Lecho
 * 
 */
public class PlaceDetailsLoader extends AsyncTaskLoader<PlaceDetails> {
	private static final String QUERY_UNITS_BY_PLACE_ORDER_BY_FACULTY = "left join " + PlaceUnitDao.TABLENAME
			+ " PU on PU." + PlaceUnitDao.Properties.UnitId.columnName + "=T." + UnitDao.Properties.Id.columnName
			+ " left join " + FacultyDao.TABLENAME + " F on F." + FacultyDao.Properties.Id.columnName + "=T."
			+ UnitDao.Properties.FacultyId.columnName + " where PU." + PlaceUnitDao.Properties.PlaceId.columnName
			+ "=? order by F." + FacultyDao.Properties.ShortName.columnName + " asc";

	private Long mPlaceId;
	private DaoSession mDaoSession;
	private PlaceDetails mData;

	public PlaceDetailsLoader(Context context, Long placeId) {
		super(context);
		mPlaceId = placeId;
	}

	/**
	 * You know what is does, right:P
	 */
	@Override
	public PlaceDetails loadInBackground() {
		mDaoSession = DatabaseHelper.getDaoSession(getContext());
		PlaceDao placeDao = mDaoSession.getPlaceDao();
		UnitDao unitDao = mDaoSession.getUnitDao();
		Place place = placeDao.load(mPlaceId);
		String[] args = new String[] { Long.toString(place.getId()) };
		List<Unit> units = unitDao.queryDeep(QUERY_UNITS_BY_PLACE_ORDER_BY_FACULTY, args);
		PlaceDetails placeDetails = new PlaceDetails(place, units);
		return placeDetails;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class will take care of delivering it; the
	 * implementation here just adds a little more logic.
	 */
	@Override
	public void deliverResult(PlaceDetails data) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (data != null) {
				onReleaseResources(data);
			}
		}
		PlaceDetails oldData = data;
		mData = data;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(data);
		}

		// At this point we can release the resources associated with
		// 'oldData' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldData != null) {
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
		} else {
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
	public void onCanceled(PlaceDetails data) {
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
	}

	/**
	 * Helper function to take care of releasing resources associated with an actively loaded data set.
	 */
	protected void onReleaseResources(PlaceDetails data) {

	}
}
