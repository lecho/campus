package lecho.app.campus.activity;

import lecho.app.campus.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CampusMapActivity extends SherlockFragmentActivity {
	private static final String TAG = CampusMapActivity.class.getSimpleName();

	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus_map);
		int playServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		Log.i(TAG, "connection result: " + playServicesStatus);

		setUpMapIfNeeded();

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * Sets up markers and map listeners
	 */
	private void setUpMap() {
		mMap.setInfoWindowAdapter(new CampusInfoWindowAdapter());
		// mMap.setOnInfoWindowClickListener(this);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		setUpMarkers();
		// zoomMapOnStart();
	}

	private void setUpMarkers() {
		double lat = 51.754645;
		double lng = 19.452780;
	
		for (int i = 0; i < 5; ++i) {
			LatLng ll = new LatLng(lat, lng);
			mMap.addMarker(new MarkerOptions().position(ll)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title("B9").snippet("Trolololo"));
			lat += 0.1;
			lng += 0.1;
		}
	}

	/**
	 * Adapter for custom marker info window
	 * 
	 * @author lecho
	 * 
	 */
	private static class CampusInfoWindowAdapter implements InfoWindowAdapter {

		@Override
		public View getInfoContents(com.google.android.gms.maps.model.Marker arg0) {
			return null;
		}

		@Override
		public View getInfoWindow(com.google.android.gms.maps.model.Marker arg0) {
			return null;
		}

	}
}
