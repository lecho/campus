package lecho.app.campus.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

/**
 * Loads Bitmap from given path and sets that bitmap as background of given
 * ImageView.
 * 
 * @author Lecho
 * 
 */
public class PhotoBitmapLoader implements Runnable {
	private static final String TAG = "PhotoBitmapLoader";
	private final Context mContext;
	private final ImageView mZoomImageView;
	private final String mPath;

	public PhotoBitmapLoader(final Context context, ImageView zoomImageView, final String path) {
		mContext = context;
		mPath = path;
		mZoomImageView = zoomImageView;
	}

	@Override
	public void run() {
		InputStream inputStream = null;
		try {
			inputStream = mContext.getAssets().open(mPath);
			// placePhoto.setImageBitmap(BitmapFactory.decodeStream(inputStream));
			final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			mZoomImageView.setImageBitmap(bitmap);

		} catch (IOException e) {
			Log.e(TAG, "Could not load main place photo from file: " + mPath, e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close stream for main place photo from file: " + mPath, e);
				}
			}
		}

	}

}
