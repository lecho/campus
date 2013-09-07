package lecho.app.campus.fragment;

import java.io.File;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.activity.PlacePhotoActivity;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.loader.PlaceDetailsLoader;
import lecho.app.campus.utils.BitmapAsyncTask;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.PlaceDetails;
import lecho.app.campus.utils.UnitsGroup;
import lecho.app.campus.view.UnitsGroupLayout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Displays Place details, photo, name, symbol etc.
 * 
 * @author Lecho
 * 
 */
public class PlaceDetailsFragment extends SherlockFragment implements LoaderCallbacks<PlaceDetails> {
	public static final String TAG = "PlaceDetailsFragment";
	private static final int PLACE_DETAILS_LOADER = PlaceDetailsFragment.class.hashCode();

	private View mProgressBar;
	private TextView mSymbol;
	private TextView mName;
	private TextView mDescription;
	private LinearLayout mScrollContent;
	private View mListHeader;
	ImageView placePhoto;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_place_details, container, false);
		mProgressBar = view.findViewById(R.id.progress_bar);
		mSymbol = (TextView) view.findViewById(R.id.symbol);
		mName = (TextView) view.findViewById(R.id.name);
		mDescription = (TextView) view.findViewById(R.id.description);
		mScrollContent = (LinearLayout) view.findViewById(R.id.scroll_view_content);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(PLACE_DETAILS_LOADER, null, this);
	}

	@Override
	public Loader<PlaceDetails> onCreateLoader(int id, Bundle args) {
		if (PLACE_DETAILS_LOADER == id) {
			// mViewSwitcher.setDisplayedChild(0);
			return new PlaceDetailsLoader(getActivity().getApplicationContext(), getArguments().getLong(
					Config.ARG_PLACE_ID));
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<PlaceDetails> loader, PlaceDetails data) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			// Fill the sticky header with symbol, name,description
			prepareStickyHeader(data);
			// Fill the list header.
			prepareListHeader(data);
			// Fill the list adapter.
			// mUnitsAdapter = new PlaceUnitsAdapter(getActivity().getApplicationContext(), R.layout.list_item_unit,
			// data.unitsGroups);
			// setListAdapter(mUnitsAdapter);

			prepareScrollContent(data);
			mProgressBar.setVisibility(View.GONE);

		}
	}

	private void prepareScrollContent(PlaceDetails data) {
		for (UnitsGroup unitsGroup : data.unitsGroups) {
			UnitsGroupLayout groupLayout = new UnitsGroupLayout(getActivity());
			if (null != unitsGroup.faculty) {
				groupLayout.setFaculty(unitsGroup.faculty.getShortName());
				groupLayout.addSeparator();
			}
			for (Unit unit : unitsGroup.units) {
				if (groupLayout.getUnitsCount() > 0) {
					groupLayout.addSeparator();
				}
				groupLayout.addUnit(unit.getName());
			}
			mScrollContent.addView(groupLayout);
		}
	}

	@Override
	public void onLoaderReset(Loader<PlaceDetails> loader) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			// Nothing to do here.
		}
	}

	private void prepareStickyHeader(final PlaceDetails data) {
		mSymbol.setText(data.place.getSymbol());
		mName.setText(data.place.getName());
		if (TextUtils.isEmpty(data.place.getDescription())) {
			mDescription.setVisibility(View.GONE);
		} else {
			mDescription.setVisibility(View.VISIBLE);
			mDescription.setText(data.place.getDescription());
		}
	}

	private void prepareListHeader(final PlaceDetails data) {
		mListHeader = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_place_details_list_header,
				null);
		// Photo.
		placePhoto = (ImageView) mListHeader.findViewById(R.id.place_photo);
		GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getActivity(),
				new PlacePhotoGestureListener());
		gestureDetector.setOnDoubleTapListener(new PlacePhotoTapListener(getActivity(), data));
		placePhoto.setOnTouchListener(new PlacePhotoTouchListener(gestureDetector));
		loadPlaceMainPhoto(data, placePhoto);
		mScrollContent.addView(mListHeader);
		// Set list header.
		// if (getListView().getHeaderViewsCount() == 0) {
		// getListView().addHeaderView(mListHeader);
		// }
	}

	private void loadPlaceMainPhoto(final PlaceDetails data, final ImageView placePhoto) {
		StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append(File.separator)
				.append(data.place.getSymbol()).append(File.separator).append(Config.PLACE_MAIN_PHOTO);

		BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(getActivity(), placePhoto);
		bitmapAsyncTask.execute(placePhotoPath.toString());
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
