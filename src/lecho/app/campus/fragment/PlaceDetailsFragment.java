package lecho.app.campus.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.activity.PlacePhotoActivity;
import lecho.app.campus.dao.DaoSession;
import lecho.app.campus.dao.Faculty;
import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.PlaceDao;
import lecho.app.campus.dao.PlaceUnit;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.dao.UnitDao;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DatabaseHelper;
import lecho.app.campus.utils.PhotoBitmapLoader;
import lecho.app.campus.utils.PlaceDetails;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

	@Override
	public void onLoaderReset(Loader<PlaceDetails> loader) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			// Nothing to do here.
		}
	}

	private void prepareHeader(final PlaceDetails data) {
		mHeader = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_place_details_header, null);
		// Photo.
		ImageView placePhoto = (ImageView) mHeader.findViewById(R.id.place_photo);
		GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getActivity(),
				new PlacePhotoGestureListener());
		gestureDetector.setOnDoubleTapListener(new PlacePhotoTapListener(getActivity(), data));
		placePhoto.setOnTouchListener(new PlacePhotoTouchListener(gestureDetector));
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

		// Set list header.
		if (getListView().getHeaderViewsCount() == 0) {
			getListView().addHeaderView(mHeader);
		}
	}

	private void loadPlaceMainPhoto(final PlaceDetails data, final ImageView placePhoto) {
		StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append(File.separator)
				.append(data.place.getSymbol()).append(File.separator).append(Config.PLACE_MAIN_PHOTO);
		new Handler().post(new PhotoBitmapLoader(getActivity(), placePhoto, placePhotoPath.toString()));
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

	/**
	 * Units Array Adapter.
	 * 
	 * @author Lecho
	 * 
	 */
	private static class PlaceUnitsAdapter extends ArrayAdapter<Unit> {

		public PlaceUnitsAdapter(Context context, int textViewResourceId, List<Unit> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				convertView = View.inflate(getContext(), R.layout.list_item_unit, null);
				holder = new ViewHolder();
				holder.unitName = (TextView) convertView.findViewById(R.id.unit_name);
				holder.unitFaculty = (TextView) convertView.findViewById(R.id.unit_facluty);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Unit unit = getItem(position);
			holder.unitName.setText(unit.getName());
			Faculty faculty = unit.getFaculty();
			if (null != faculty) {
				holder.unitFaculty.setText(unit.getFaculty().getShortName());
			} else {
				holder.unitFaculty.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		private static class ViewHolder {
			TextView unitName;
			TextView unitFaculty;
		}

	}

	private static class PlacePhotoGestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}

	private static class PlacePhotoTapListener implements OnDoubleTapListener {
		private Activity mActivity;
		private long mPlaceId;
		private String mPlaceSymbol;

		public PlacePhotoTapListener(final Activity activity, final PlaceDetails data) {
			mActivity = activity;
			mPlaceId = data.place.getId();
			mPlaceSymbol = data.place.getSymbol();
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Intent intent = new Intent(mActivity, PlacePhotoActivity.class);
			intent.putExtra(Config.ARG_PLACE_ID, mPlaceId);
			intent.putExtra(Config.ARG_PLACE_SYMBOL, mPlaceSymbol);
			mActivity.startActivity(intent);
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}

	}

	private static class PlacePhotoTouchListener implements OnTouchListener {

		private GestureDetectorCompat mGestureDetector;

		public PlacePhotoTouchListener(final GestureDetectorCompat gestureDetector) {
			mGestureDetector = gestureDetector;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mGestureDetector.onTouchEvent(event);
			return true;
		}

	}
}
