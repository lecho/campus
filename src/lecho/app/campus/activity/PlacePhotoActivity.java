package lecho.app.campus.activity;

import java.io.File;
import java.io.IOException;

import lecho.app.campus.R;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.PhotoBitmapLoader;
import lecho.app.campus.view.ZoomImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class PlacePhotoActivity extends SherlockFragmentActivity {
	private static final String TAG = "PlaceDetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_photo);
		final ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
		final PageIndicator indicator = (LinePageIndicator) findViewById(R.id.indicator);
		final String placeSymbol = getIntent().getStringExtra(Config.ARG_PLACE_SYMBOL);
		final StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append(File.separator).append(
				placeSymbol);
		try {
			final String[] paths = getAssets().list(placePhotoPath.toString());
			pager.setAdapter(new ImagesPagerAdapter(placePhotoPath.toString(), paths));
			pager.setPageMargin((int) getResources().getDisplayMetrics().density * 10);
			indicator.setViewPager(pager);
		} catch (IOException e) {
			Log.e(TAG, "Could not list photos for place with symbol: " + placeSymbol);
		}
	}

	/**
	 * Adapter backing up ViewPager acting as images pager.
	 * 
	 * @author lecho
	 * 
	 */
	private static class ImagesPagerAdapter extends PagerAdapter {
		private String mPlacePath;
		private String[] mPaths;

		public ImagesPagerAdapter(String placePath, String[] paths) {
			mPlacePath = placePath;
			mPaths = paths;
		}

		@Override
		public int getCount() {
			return mPaths.length;
		}

		// @Override
		// public CharSequence getPageTitle(int position) {
		// return "ala";
		// }

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			/*
			 * Recycle the old bitmap to free up memory straight away
			 */
			try {
				final ImageView imageView = (ImageView) object;
				final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
				imageView.setImageBitmap(null);
				bitmap.recycle();
			} catch (Exception e) {
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			final ZoomImageView zoomImageView = new ZoomImageView(container.getContext());
			final StringBuilder path = new StringBuilder(mPlacePath).append(File.separator).append(mPaths[position]);
			new Handler().post(new PhotoBitmapLoader(container.getContext(), zoomImageView, path.toString()));
			container.addView(zoomImageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return zoomImageView;

		}

	}
}
