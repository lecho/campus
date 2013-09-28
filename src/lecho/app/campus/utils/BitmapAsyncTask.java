package lecho.app.campus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapAsyncTask extends AsyncTask<Void, Void, Bitmap> {
	public static final int SYMBOL = 1;
	public static final int PATH = 2;
	private static final String TAG = "BitmapAsyncTask";
	private Context mContext;
	private String mPath;
	private int mRawResource;
	private int mDimenWidth;
	private int mDimenHeight;
	// If false read from raw resources, if true from assets
	private boolean mFromAssets;
	private final WeakReference<ImageView> mImageViewReference;
	private final WeakReference<OnBitmapLoadedListener> mListenerReference;

	public BitmapAsyncTask(Context context, String path, ImageView imageView, int dimenWidth, int dimenHeight,
			OnBitmapLoadedListener onBitmapLoadedListener) {
		mContext = context;
		mPath = path;
		mFromAssets = true;
		mDimenWidth = dimenWidth;
		mDimenHeight = dimenHeight;
		// Use a WeakReference to ensure the ImageView can be garbage collected
		mImageViewReference = new WeakReference<ImageView>(imageView);
		// Use a WeakReference in case activity finished before AsyncTask.
		mListenerReference = new WeakReference<BitmapAsyncTask.OnBitmapLoadedListener>(onBitmapLoadedListener);
	}

	public BitmapAsyncTask(Context context, int rawResource, ImageView imageView, int dimenWidth, int dimenHeight,
			OnBitmapLoadedListener onBitmapLoadedListener) {
		mContext = context;
		mRawResource = rawResource;
		mFromAssets = false;
		mDimenWidth = dimenWidth;
		mDimenHeight = dimenHeight;
		// Use a WeakReference to ensure the ImageView can be garbage collected
		mImageViewReference = new WeakReference<ImageView>(imageView);
		// Use a WeakReference in case activity finished before AsyncTask.
		mListenerReference = new WeakReference<BitmapAsyncTask.OnBitmapLoadedListener>(onBitmapLoadedListener);
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(Void... params) {
		Resources resources = mContext.getResources();
		int reqWidth = resources.getDimensionPixelSize(mDimenWidth);
		int reqHeight = resources.getDimensionPixelSize(mDimenHeight);
		if (mFromAssets) {
			return decodeSampledBitmapFromAssets(mContext, mPath, reqWidth, reqHeight);
		} else {
			return decodeSampledBitmapFromRawResource(mContext, mRawResource, reqWidth, reqHeight);
		}
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (mImageViewReference != null && bitmap != null) {
			final ImageView imageView = mImageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
				callListener(true);
				return;
			}
		}
		callListener(false);
	}

	private void callListener(boolean success) {
		if (mListenerReference != null) {
			final OnBitmapLoadedListener listener = mListenerReference.get();
			if (null != listener) {
				listener.onBitmapLoaded(success);
			}

		}
	}

	public static Bitmap decodeSampledBitmapFromAssets(Context context, String path, int reqWidth, int reqHeight) {

		InputStream stream = null;
		try {
			stream = context.getAssets().open(path);

			return decodeSampledBitmap(reqWidth, reqHeight, stream);

		} catch (IOException e) {
			Log.e(TAG, "Could not load place photo from file: " + path, e);
			return null;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close stream for place photo from file: " + path, e);
				}
			}
		}

	}

	public static Bitmap decodeSampledBitmapFromRawResource(Context context, int rawResource, int reqWidth,
			int reqHeight) {

		InputStream stream = null;
		try {
			stream = context.getResources().openRawResource(rawResource);

			return decodeSampledBitmap(reqWidth, reqHeight, stream);

		} catch (Exception e) {
			Log.e(TAG, "Could not load place photo from raw resource", e);
			return null;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close stream for place photo from raw resource", e);
				}
			}
		}

	}

	private static Bitmap decodeSampledBitmap(int reqWidth, int reqHeight, InputStream stream) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(stream, null, options);// File(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(stream, null, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public interface OnBitmapLoadedListener {
		/**
		 * Called when AsyncTasc finish loading bitmap and set it as ImageView source. Called only if listener is not
		 * null and bitmap was loaded sucessfuly into ImageView.
		 */
		public void onBitmapLoaded(boolean success);
	}
}