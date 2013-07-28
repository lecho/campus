package lecho.app.campus.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.dao.DaoSession;
import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.PlaceDao;
import lecho.app.campus.dao.PlaceUnit;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.dao.UnitDao;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DatabaseHelper;
import lecho.app.campus.utils.PlaceDetails;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * Displays Place details, photo, name, symbol etc.
 * 
 * @author Lecho
 * 
 */
public class PlaceDetailsFragment extends SherlockListFragment implements LoaderCallbacks<PlaceDetails> {
	private static final String TAG = PlaceDetailsFragment.class.getSimpleName();
	private static final int PLACE_DETAILS_LOADER = PlaceDetailsFragment.class.hashCode();

	private View mHeader;
	private PlaceUnitsAdapter mUnitsAdapter;

	public static PlaceDetailsFragment newInstance(long placeId) {
		PlaceDetailsFragment fragment = new PlaceDetailsFragment();
		Bundle args = new Bundle();
		args.putLong(Config.ARG_PLACE_ID, placeId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
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
			return new PlaceDetailsLoader(getActivity().getApplicationContext(), getArguments().getLong(
					Config.ARG_PLACE_ID));
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<PlaceDetails> loader, PlaceDetails data) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			// Fill the list header.
			prepareHeader(data);
			// Fill the list adapter.
			mUnitsAdapter = new PlaceUnitsAdapter(getActivity().getApplicationContext(), R.layout.list_item_unit,
					data.units);
			setListAdapter(mUnitsAdapter);

		}
	}

	private void prepareHeader(PlaceDetails data) {
		mHeader = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_place_details_header, null);
		// Photo.
		ImageView placePhoto = (ImageView) mHeader.findViewById(R.id.place_photo);
		loadPlaceMainPhoto(data, placePhoto);
		// Symbol.
		TextView placeSymbol = (TextView) mHeader.findViewById(R.id.place_symbol);
		placeSymbol.setText(data.place.getSymbol());
		// Name.
		TextView placeName = (TextView) mHeader.findViewById(R.id.place_name);
		String placeNameText = data.place.getName();
		if (TextUtils.isEmpty(placeNameText)) {
			placeName.setVisibility(View.GONE);
		} else {
			placeName.setText(placeNameText);
		}
		// Description.
		TextView placeDescription = (TextView) mHeader.findViewById(R.id.place_description);
		String placeDescriptionText = data.place.getDescription();
		if (TextUtils.isEmpty(placeDescriptionText)) {
			placeDescription.setVisibility(View.GONE);
		} else {
			placeDescription.setText(placeDescriptionText);
		}
		// More info.
		// TODO Set place more info button action
		ImageButton placeMoreInfo = (ImageButton) mHeader.findViewById(R.id.place_more_info_button);

		// Set list header.
		if (getListView().getHeaderViewsCount() == 0) {
			getListView().addHeaderView(mHeader);
		}
	}

	private void loadPlaceMainPhoto(PlaceDetails data, ImageView placePhoto) {
		StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append("/")
				.append(data.place.getSymbol()).append("/").append(Config.PLACE_MAIN_PHOTO);
		InputStream inputStream = null;
		try {
			inputStream = getActivity().getAssets().open(placePhotoPath.toString());
			placePhoto.setImageBitmap(BitmapFactory.decodeStream(inputStream));
		} catch (IOException e) {
			Log.e(TAG, "Could not load main place photo from file: " + placePhotoPath.toString(), e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close stream for main place photo from file: " + placePhotoPath.toString(), e);
				}
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<PlaceDetails> loader) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			// Nothing to do here.
		}
	}

	private static class PlaceUnitsAdapter extends ArrayAdapter<Unit> {

		public PlaceUnitsAdapter(Context context, int textViewResourceId, List<Unit> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// // TODO Auto-generated method stub
			// return super.getView(position, convertView, parent);
			TextView tv = new TextView(getContext());
			tv.setText(getItem(position).getName());
			return tv;
		}

	}

	/**
	 * Loads Place details from database, name, symbol, faculties etc. Loader
	 * doesn't what for changes in database, there's no need for that in this
	 * app.
	 * 
	 * Implementation based on sample from android sdk documentation.
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
			List<Unit> units = new ArrayList<Unit>(placeUnitList.size());
			for (PlaceUnit placeUnit : placeUnitList) {
				// using eager loading, I need all that data.
				Unit unit = unitDao.loadDeep(placeUnit.getUnitId());
				units.add(unit);
			}
			PlaceDetails placeDetails = new PlaceDetails(place, units);
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
				for (Unit unit : data.units) {
					unitDao.detach(unit);
				}
				data = null;
			}
		}
	}

}
