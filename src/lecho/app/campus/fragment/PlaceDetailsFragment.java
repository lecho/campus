package lecho.app.campus.fragment;

import java.util.ArrayList;
import java.util.List;

import lecho.app.campus.dao.DaoSession;
import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.PlaceDao;
import lecho.app.campus.dao.PlaceUnit;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.dao.UnitDao;
import lecho.app.campus.utils.DatabaseHelper;
import lecho.app.campus.utils.PlaceDetails;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * Displays Place details, photo, name, symbol etc.
 * 
 * @author Lecho
 * 
 */
public class PlaceDetailsFragment extends SherlockListFragment implements LoaderCallbacks<PlaceDetails> {
	private static final String ARG_PLACE_ID = "lecho.app.campus:PLACE_ID";
	private static final int PLACE_DETAILS_LOADER = PlaceDetailsFragment.class.hashCode();

	private View mHeader;
	private PlaceDetailsAdapter mPlaceDetailsAdapter;

	public static PlaceDetailsFragment newInstance(long placeId) {
		PlaceDetailsFragment fragment = new PlaceDetailsFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_PLACE_ID, placeId);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(PLACE_DETAILS_LOADER, null, this);
	}

	@Override
	public Loader<PlaceDetails> onCreateLoader(int id, Bundle args) {
		if (PLACE_DETAILS_LOADER == id) {
			return new PlaceDetailsLoader(getActivity().getApplicationContext(), getArguments().getLong(ARG_PLACE_ID));
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<PlaceDetails> loader, PlaceDetails data) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {

		}
	}

	@Override
	public void onLoaderReset(Loader<PlaceDetails> loader) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {

		}
	}

	private static class PlaceDetailsAdapter extends ArrayAdapter<Unit> {

		public PlaceDetailsAdapter(Context context, int textViewResourceId, List<Unit> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return super.getView(position, convertView, parent);
		}

	}

	/**
	 * Loads Place details from database, name, symbol, faculties etc. Loader
	 * doesn't what for changes in database, there's no need for that in this
	 * app.
	 * 
	 * @author Lecho
	 * 
	 */
	private static class PlaceDetailsLoader extends AsyncTaskLoader<PlaceDetails> {

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
			List<PlaceUnit> placeUnitList = place.getPlaceUnitList();
			List<Unit> unitList = new ArrayList<Unit>(placeUnitList.size());
			for (PlaceUnit placeUnit : placeUnitList) {
				Unit unit = unitDao.loadDeep(placeUnit.getUnitId());
				unitList.add(unit);
			}
			PlaceDetails placeDetails = new PlaceDetails();
			placeDetails.place = place;
			placeDetails.unitList = unitList;
			return placeDetails;
		}

		/**
		 * Called when there is new data to deliver to the client. The super
		 * class will take care of delivering it; the implementation here just
		 * adds a little more logic.
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
		 * Helper function to take care of releasing resources associated with
		 * an actively loaded data set.
		 */
		protected void onReleaseResources(PlaceDetails data) {
			// TODO Check if this code is needed.
			if (null != data) {
				// Detach data from DaoSession.
				PlaceDao placeDao = mDaoSession.getPlaceDao();
				placeDao.detach(data.place);
				UnitDao unitDao = mDaoSession.getUnitDao();
				for (Unit unit : data.unitList) {
					unitDao.detach(unit);
				}
				data = null;
			}
		}
	}

}
