package lecho.app.campus.adapter;

import lecho.app.campus.R;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements InfoWindowAdapter {
	private Context mContext;

	public MarkerInfoWindowAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return View.inflate(mContext, R.layout.custom_info_window, null);
	}
}
