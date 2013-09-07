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
	private static final String TAG = "BitmapAsyncTask";
	private Context mContext;
	private final WeakReference<ImageView> imageViewReference;
	private String mData;

	public BitmapAsyncTask(Context context, ImageView imageView) {
		mContext = context;
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... params) {
		mData = params[0];
		Resources resources = mContext.getResources();
		int reqWidth = resources.getDimensionPixelSize(R.dimen.place_image_width);
		int reqHeight = resources.getDimensionPixelSize(R.dimen.place_image_width);
		return decodeSampledBitmapFromAssets(mContext, mData, reqWidth, reqHeight);
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
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
			Log.e(TAG, "Could not load main place photo from file: " + path, e);
			return null;
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close stream for main place photo from file: " + path, e);
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
}