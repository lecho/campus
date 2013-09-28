package lecho.app.campus.fragment;

import lecho.app.campus.R;
import lecho.app.campus.utils.BitmapAsyncTask;
import lecho.app.campus.utils.BitmapAsyncTask.OnBitmapLoadedListener;
import lecho.app.campus.view.ZoomImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Single page of product guide. Loads image from raw resources in case of future i18n.
 * 
 * @author Lecho
 * 
 */
public class ProductGuidePageFragment extends SherlockFragment implements OnBitmapLoadedListener {
	public static final String EXTRA_RAW_RESOURCE = "lecho.app.campus:RAW_RESOURCE";
	private RelativeLayout mLayout;
	private ZoomImageView mImageView;
	private ProgressBar mProgressBar;

	public static ProductGuidePageFragment newInstance(int rawResource) {
		ProductGuidePageFragment fragment = new ProductGuidePageFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_RAW_RESOURCE, rawResource);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_product_guide_page, container, false);
		mLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
		mImageView = new ZoomImageView(getActivity());
		mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		loadImage(getArguments().getInt(EXTRA_RAW_RESOURCE), mImageView);
		return view;
	}

	private void loadImage(int rawResource, final ZoomImageView imageView) {
		BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(getActivity(), rawResource, imageView,
				R.dimen.product_guide_image_width, R.dimen.product_guide_image_height, this);
		bitmapAsyncTask.execute();
	}

	@Override
	public void onDestroy() {
		recycleImage();
		super.onDestroy();
	}

	private void recycleImage() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (null != mImageView) {
				final Drawable drawable = mImageView.getDrawable();
				if (null != drawable) {
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					bitmap.recycle();
					mImageView = null;
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
