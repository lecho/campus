package lecho.app.campus.fragment;

import java.io.File;

import lecho.app.campus.R;
import lecho.app.campus.activity.PlaceImageActivity;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.loader.PlaceDetailsLoader;
import lecho.app.campus.utils.BitmapAsyncTask;
import lecho.app.campus.utils.BitmapAsyncTask.OnBitmapLoadedListener;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.PlaceDetails;
import lecho.app.campus.utils.UnitsGroup;
import lecho.app.campus.utils.Utils;
import lecho.app.campus.view.UnitsGroupLayout;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Displays Place details, image, name, symbol etc.
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
		args.putLong(Config.EXTRA_PLACE_ID, placeId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onDestroy() {
		recycleImage();
		super.onDestroy();
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
					Config.EXTRA_PLACE_ID));
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<PlaceDetails> loader, PlaceDetails data) {
		if (PLACE_DETAILS_LOADER == loader.getId()) {
			mScrollContent.removeAllViews();
			if (null != data && null != data.place) {
				prepareHeader(data);
				prepareImage(data);
				prepareScrollContent(data);
				mProgressBar.setVisibility(View.GONE);
			} else {
				Log.w(TAG, "Null data returned from details loader");
			}
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
	public void onBitmapLoaded(boolean success) {
		if (success && null == mImage.getParent()) {
			final ViewTreeObserver observer = mScrollContent.getViewTreeObserver();
			observer.addOnPreDrawListener(new OnPreDrawListener() {

				@Override
				public boolean onPreDraw() {
					observer.removeOnPreDrawListener(this);
					int childCount = mScrollContent.getChildCount();
					if (childCount < 2) {
						// No need for animation
						return true;
					}
					View v0 = mScrollContent.getChildAt(0);
					View v1 = mScrollContent.getChildAt(1);
					int deltaX = v1.getTop() - v0.getTop();
					final TranslateAnimation anim = new TranslateAnimation(0, 0, -deltaX, 0);
					anim.setDuration(300);
					for (int i = 0; i < mScrollContent.getChildCount(); ++i) {
						View v = mScrollContent.getChildAt(i);
						v.startAnimation(anim);
					}
					return true;
				}
			});
			mScrollContent.addView(mImage, 0);
			mImage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show));
		}
	}

	private void prepareScrollContent(PlaceDetails data) {
		prepareUnitsGroups(data);
		prepareGoToGMapsLink(data);
	}

	private void prepareUnitsGroups(PlaceDetails data) {
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

	private void prepareGoToGMapsLink(PlaceDetails data) {
		double latitude = data.place.getLatitude();
		double longitude = data.place.getLongtitude();

		TextView gMapsLink = new TextView(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int margin = Utils.dp2px(getActivity(), 16);
		lp.setMargins(margin, margin, margin, margin);
		lp.gravity = Gravity.RIGHT;
		gMapsLink.setLayoutParams(lp);
		gMapsLink.setMinHeight(Utils.dp2px(getActivity(), 48));
		gMapsLink.setGravity(Gravity.RIGHT);
		gMapsLink.setText(R.string.go_to_google_maps);
		gMapsLink.setTextColor(getResources().getColorStateList(R.color.selector_gmaps_link));
		gMapsLink.setOnClickListener(new GoToGMapsClickListener(getActivity(), latitude, longitude));
		mScrollContent.addView(gMapsLink);
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
		mImage.setOnClickListener(new PlaceImageClickListener(getActivity(), data));
		loadPlaceImage(data, mImage);

	}

	private void recycleImage() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (null != mImage) {
				final Drawable drawable = mImage.getDrawable();
				if (null != drawable) {
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					bitmap.recycle();
					mImage = null;
				}
			}
		}
	}

	private void loadPlaceImage(final PlaceDetails data, final ImageView imageView) {
		final String path = new StringBuilder(Utils.getPlaceImagesDir(data.place.getSymbol())).append(File.separator)
				.append(Config.PLACE_MAIN_PHOTO).toString();
		BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(getActivity(), imageView, this);
		bitmapAsyncTask.execute(path);
	}

	private class PlaceImageClickListener implements OnClickListener {
		private final Activity mActivity;
		private final long mPlaceId;
		private final String mPlaceSymbol;

		public PlaceImageClickListener(final Activity activity, final PlaceDetails data) {
			mActivity = activity;
			mPlaceId = data.place.getId();
			mPlaceSymbol = data.place.getSymbol();
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mActivity, PlaceImageActivity.class);
			intent.putExtra(Config.EXTRA_PLACE_ID, mPlaceId);
			intent.putExtra(Config.EXTRA_PLACE_SYMBOL, mPlaceSymbol);
			mActivity.startActivity(intent);
		}
	}

	private static class GoToGMapsClickListener implements OnClickListener {
		private final Activity mActivity;
		private final double mLatitude;
		private final double mLongitude;

		public GoToGMapsClickListener(final Activity activity, double latitude, double longitude) {
			mActivity = activity;
			mLatitude = latitude;
			mLongitude = longitude;
		}

		@Override
		public void onClick(View v) {
			if (Utils.launchGMaps(mActivity, mLatitude, mLongitude)) {
				Log.i(TAG, "Start Google Maps application");
			} else {
				// TODO Show error message
			}

		}
	}
}
