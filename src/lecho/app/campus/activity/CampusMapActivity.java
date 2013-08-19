package lecho.app.campus.activity;

import lecho.app.campus.R;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class CampusMapActivity extends SherlockFragmentActivity {
	private static final String TAG = CampusMapActivity.class.getSimpleName();

	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light_DarkActionBar);
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
		// mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		// mMap.setOnInfoWindowClickListener(this);
		// mMap.setMapType(MAP_TYPE_HYBRID);
		mMap.setMyLocationEnabled(true);
		// setUpAllMarkers();
		// zoomMapOnStart();
	}
}
