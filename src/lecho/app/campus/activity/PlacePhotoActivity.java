package lecho.app.campus.activity;

import java.io.IOException;

import lecho.app.campus.R;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.PhotoBitmapLoader;
import lecho.app.campus.view.ZoomImageView;
import android.content.Context;
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

public class PlacePhotoActivity extends SherlockFragmentActivity {
	private static final String TAG = PlaceDetailsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_photo);

		final String placeSymbol = getIntent().getStringExtra(Config.ARG_PLACE_SYMBOL);
		final ViewPager pager = (ViewPager) findViewById(R.id.view_pager);

		StringBuilder placePhotoPath = new StringBuilder(Config.APP_ASSETS_DIR).append("/").append(placeSymbol)
				.append("/").append(Config.PLACE_MAIN_PHOTO);
		try {
			final String[] paths = getAssets().list(placePhotoPath.toString());
			pager.setAdapter(new ImagesPagerAdapter(getApplicationContext(), paths));
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
		private String[] mPaths;
		private Context mContext;

		public ImagesPagerAdapter(Context context, String[] paths) {
			mContext = context;
			mPaths = paths;
		}

		@Override
		public int getCount() {
			return mPaths.length;
		}

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
			/*
			 * Load the new bitmap in the background thread
			 */
			final String path = mPaths[position];
			new Handler().post(new PhotoBitmapLoader(mContext, zoomImageView, path));
			container.addView(zoomImageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return zoomImageView;

		}

	}
}
