package lecho.app.campus.utils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ImagesDirAsyncTask extends AsyncTask<String, Void, String[]> {
	private static final String TAG = "ImagesDirAsyncTask";
	private final Context mContext;
	private final WeakReference<OnImagesDirListener> mListenerReference;

	public ImagesDirAsyncTask(Context context, OnImagesDirListener listener) {
		mContext = context;
		mListenerReference = new WeakReference<OnImagesDirListener>(listener);
	}

	@Override
	protected String[] doInBackground(String... params) {
		final String symbol = params[0];// crush crush crush
		try {
			final String placeImagesDir = Utils.getPlaceImagesDir(symbol);
			String[] paths;
			paths = mContext.getAssets().list(placeImagesDir);
			return paths;
		} catch (IOException e) {
			Log.e(TAG, "Could not list photos for place with symbol: " + symbol, e);
			return null;
		}

	}

	@Override
	protected void onPostExecute(String[] result) {
		callListener(result);
	}

	private void callListener(String[] paths) {
		if (mListenerReference != null) {
			final OnImagesDirListener listener = mListenerReference.get();
			if (null != listener) {
				listener.onImagesDir(paths);
			}

		}
	}

	public interface OnImagesDirListener {
		public void onImagesDir(String[] paths);
	}
}