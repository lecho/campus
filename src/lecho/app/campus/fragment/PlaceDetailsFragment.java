package lecho.app.campus.fragment;

import java.io.File;

import lecho.app.campus.R;
import lecho.app.campus.activity.PlacePhotoActivity;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.loader.PlaceDetailsLoader;
import lecho.app.campus.utils.BitmapAsyncTask;
import lecho.app.campus.utils.BitmapAsyncTask.OnBitmapLoadedListener;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.PlaceDetails;
import lecho.app.campus.utils.UnitsGroup;
import lecho.app.campus.view.UnitsGroupLayout;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
public class PlaceDetailsFragment extends SherlockFragment implements LoaderCallbacks<PlaceDetails>,
		OnBitmapLoadedListener {
	public static final String TAG = "PlaceDetailsFragment";
	private static final int PLACE_DETAILS_LOADER = PlaceDetailsFragment.class.hashCode();

	private View mProgressBar;
	private TextView mSymbol;
	private TextView mName;
	private TextView mDescription;
	private LinearLayout mScrollContent;
	private ImageView mImage;

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
			return new PlaceDetailsLoader(getActivity().getApplicationContext(), getArguments().getLong(
					Config.ARG_PLACE_ID));
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<PlaceDetails> loader, PlaceDetails data) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			mScrollContent.removeAllViews();
			prepareHeader(data);
			prepareImage(data);
			prepareScrollContent(data);
			mProgressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<PlaceDetails> loader) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			mScrollContent.removeAllViews();
			recycleImage();
		}
	}

	@Override
	public void onBitmapLoaded() {
		if (null == mImage.getParent()) {
			mScrollContent.addView(mImage, 0);
			mImage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show));
		} else {
			Log.w(TAG, "Place photo already attached");
		}
	}

	private void prepareScrollContent(PlaceDetails data) {
		for (UnitsGroup unitsGroup : data.unitsGroups) {
			UnitsGroupLayout groupLayout = new UnitsGroupLayout(getActivity());
			if (null != unitsGroup.faculty) {
				groupLayout.setFaculty(unitsGroup.faculty.getShortName());
				groupLayout.addFacultySeparator();
			}
			for (Unit unit : unitsGroup.units) {
				if (groupLayout.getUnitsCount() > 0) {
					groupLayout.addUnitSeparator();
				}
				groupLayout.addUnit(unit.getName());
			}
			mScrollContent.addView(groupLayout);
		}
	}

	private void prepareHeader(final PlaceDetails data) {
		mSymbol.setText(data.place.getSymbol());
		mName.setText(data.place.getName());
		if (TextUtils.isEmpty(data.place.getDescription())) {
			mDescription.setVisibility(View.GONE);
		} else {
			mDescription.setVisibility(View.VISIBLE);
			mDescription.setText(data.place.getDescription());
		}
	}

	private void prepareImage(final PlaceDetails data) {
		recycleImage();
		mImage = (ImageView) View.inflate(getActivity().getApplicationContext(), R.layout.fragment_place_details_image,
				null);
		GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getActivity(),
				new PlacePhotoGestureListener());
		gestureDetector.setOnDoubleTapListener(new PlacePhotoTapListener(getActivity(), data));
		mImage.setOnTouchListener(new PlacePhotoTouchListener(gestureDetector));
		StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append(File.separator).append(
				data.place.getSymbol());
		loadPlaceMainPhoto(data, mImage);

	}

	private void recycleImage() {
		if (null != mImage) {
			final Drawable drawable = mImage.getDrawable();
			if (null != drawable) {
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				bitmap.recycle();
				mImage = null;
			}
		}
	}

	private void loadPlaceMainPhoto(final PlaceDetails data, final ImageView placePhoto) {
		StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append(File.separator)
				.append(data.place.getSymbol()).append(File.separator).append(Config.PLACE_MAIN_PHOTO);

		BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(getActivity(), placePhoto, this);
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
