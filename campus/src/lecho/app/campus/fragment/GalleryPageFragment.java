package lecho.app.campus.fragment;

import lecho.app.campus.R;
import lecho.app.campus.utils.BitmapAsyncTask;
import lecho.app.campus.utils.BitmapAsyncTask.OnBitmapLoadedListener;
import lecho.app.campus.utils.Config;
import lecho.app.campus.view.ZoomImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * 
 * @author Lecho
 * 
 */
public class GalleryPageFragment extends SherlockFragment implements OnBitmapLoadedListener {
	private static final String TAG = "GalleryPageFragment";
	public static final String EXTRA_PATH = "lecho.app.campus:EXTRA_PATH";
	private RelativeLayout mLayout;
	private ZoomImageView mImageView;
	private ProgressBar mProgressBar;

	public static GalleryPageFragment newInstance(String path) {
		GalleryPageFragment fragment = new GalleryPageFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_PATH, path);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gallery_page, container, false);
		mLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
		mImageView = new ZoomImageView(getActivity());
		mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		loadPlaceImage(getArguments().getString(EXTRA_PATH), mImageView);
		return view;
	}

	private void loadPlaceImage(String path, final ZoomImageView imageView) {
		BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(getActivity(), path, imageView,
				R.dimen.gallery_place_image_request_width, R.dimen.gallery_place_image_request_height, this);
		bitmapAsyncTask.execute();
	}

	@Override
	public void onDestroy() {
		recycleImage();
		super.onDestroy();
	}

	private void recycleImage() {
		// TODO Implement reference counting. Recycle for pre-honeycomb.
		if (null != mImageView) {
			final Drawable drawable = mImageView.getDrawable();
			if (null != drawable) {
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				bitmap.recycle();
				mImageView = null;
				if (Config.DEBUG) {
					Log.d(TAG, "Recycled bitmap");
				}
			}
		}
	}

	@Override
	public void onBitmapLoaded(boolean success) {
		if (success) {
			mProgressBar.setVisibility(View.GONE);
			if (null == mImageView.getParent()) {
				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				mImageView.setLayoutParams(lp);
				mLayout.addView(mImageView);
			}
		}

	}
}
