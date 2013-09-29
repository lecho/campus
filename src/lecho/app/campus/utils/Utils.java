package lecho.app.campus.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public final class Utils {
	private static final String TAG = "Utils";

	public static boolean launchGMaps(Context context, double latitude, double longitude) {
		final String GMAPS = "geo:";
		final String ZOOM = "?z=18";
		StringBuilder sb = new StringBuilder().append(GMAPS).append(Double.toString(latitude)).append(",")
				.append(Double.toString(longitude)).append(ZOOM);
		try {
			Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
			context.startActivity(mapsIntent);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not start google navigation", e);
			return false;
		}
	}

	@SuppressLint("DefaultLocale")
	public static boolean launchWebBrowser(Context context, String url) {
		try {
			url = url.toLowerCase();
			if (!url.startsWith("http://") || !url.startsWith("https://")) {
				url = "http://" + url;
			}

			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			context.startActivity(i);
			Log.i(TAG, "Launching browser with url: " + url);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not start web browser", e);
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Pair<Integer, Integer> getScreendDimension(Context ctx) {
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			Pair<Integer, Integer> dimens = new Pair<Integer, Integer>(display.getWidth(), display.getHeight());
			return dimens;
		} else {
			Point size = new Point();
			display.getSize(size);
			Pair<Integer, Integer> dimens = new Pair<Integer, Integer>(size.x, size.y);
			return dimens;
		}

	}

	public static int dp2px(Context context, int dp) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (dp * scale + 0.5f);

	}

	/**
	 * Returns current ActionBar height for given activity.
	 * 
	 * @param activity
	 * @return
	 */
	public static int getActionBarHeight(Activity activity) {
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (activity.getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources()
					.getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public static String getPlaceImagesDir(String symbol) {
		return new StringBuilder(Config.APP_ASSETS_DIR).append(File.separator).append(symbol).toString();
	}

	public static boolean isOnline(Context context) {
		// TODO check timeout
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return new Pair<String, Integer>(pInfo.versionName, pInfo.versionCode);
		} catch (Exception e) {
			Log.e(TAG, "Could not get version number");
			return new Pair<String, Integer>("", 0);
		}
	}
}
