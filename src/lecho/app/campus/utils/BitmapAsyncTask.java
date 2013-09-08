package lecho.app.campus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import lecho.app.campus.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
	public static final int SYMBOL = 1;
	public static final int PATH = 2;
	private static final String TAG = "BitmapAsyncTask";
	private Context mContext;
	private final WeakReference<ImageView> mImageViewReference;
	private final WeakReference<OnBitmapLoadedListener> mListenerReference;

	public BitmapAsyncTask(Context context, ImageView imageView, OnBitmapLoadedListener onBitmapLoadedListener) {
		mContext = context;
		// Use a WeakReference to ensure the ImageView can be garbage collected
		mImageViewReference = new WeakReference<ImageView>(imageView);
		// Use a WeakReference in case activity finished before AsyncTask.
		mListenerReference = new WeakReference<BitmapAsyncTask.OnBitmapLoadedListener>(onBitmapLoadedListener);
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... params) {
		final String path = params[0];// crush crush crush
		Resources resources = mContext.getResources();
		int reqWidth = resources.getDimensionPixelSize(R.dimen.place_image_width);
		int reqHeight = resources.getDimensionPixelSize(R.dimen.place_image_width);
		return decodeSampledBitmapFromAssets(mContext, path, reqWidth, reqHeight);
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

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, null, options);// File(path, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(stream, new Rect(10, 13, 13, 13), options);

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