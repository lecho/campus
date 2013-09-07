package lecho.app.campus.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;

public final class Utils {

	/**
	 * Check if first position is close enough to second position
	 * 
	 * @param first
	 * @param second
	 * @param maxDistance
	 *            for example 0.000001
	 * @return
	 */
	public static boolean compareLocation(LatLng first, LatLng second, double maxDistance) {
		if (first.latitude > second.latitude - maxDistance && first.latitude < second.latitude + maxDistance) {
			if (first.longitude > second.longitude - maxDistance && first.longitude < second.longitude + maxDistance) {
				return true;
			}

		}
		return false;
	}

	public static boolean launchNavigation(Context context, double latitude, double longitude) {
		final String GNAVI = "google.navigation:q=";
		StringBuilder sb = new StringBuilder().append(GNAVI).append(latitude).append(",").append(longitude);

		try {
			Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));

			context.startActivity(navIntent);
			Log.i(Utils.class.getSimpleName(), "Launching navigation to location: lat:" + latitude + ", lng:"
					+ longitude);
			return true;

		} catch (Exception e) {
			Log.e(Utils.class.getSimpleName(), "Could not start google navigation", e);
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
			Log.i(Utils.class.getSimpleName(), "Launching browser with url: " + url);
			return true;
		} catch (Exception e) {
			Log.e(Utils.class.getSimpleName(), "Could not start web browser", e);
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
}
