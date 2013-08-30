package lecho.app.campus.activity;

import java.util.HashMap;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.adapter.SearchSuggestionAdapter;
import lecho.app.campus.dao.Place;
import lecho.app.campus.loader.PlacesLoader;
import lecho.app.campus.utils.PlacesList;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CampusMapActivity extends SherlockFragmentActivity implements LoaderCallbacks<PlacesList>,
		OnQueryTextListener {
	private static final String TAG = CampusMapActivity.class.getSimpleName();
	private static final int PLACES_LOADER = CampusMapActivity.class.hashCode();
	private GoogleMap mMap;
	private HashMap<Long, Marker> mMarkers = new HashMap<Long, Marker>();
	private SearchSuggestionAdapter mSearchSuggestionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus_map);
		int playServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		Log.i(TAG, "connection result: " + playServicesStatus);
		setUpMapIfNeeded();
		Bundle args = new Bundle();
		args.putInt(PlacesLoader.ARG_ACTION, PlacesLoader.LOAD_ALL_PLACES);
		getSupportLoaderManager().initLoader(PLACES_LOADER, args, this);
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
		// setUpMarkers();
		// zoomMapOnStart();
	}

	@Override
	protected void onPause() {
		if (null != mSearchSuggestionAdapter) {
			mSearchSuggestionAdapter.close();
		}
		super.onPause();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_campus_map, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		// Set custom adapter for custom dropdown view
		mSearchSuggestionAdapter = new SearchSuggestionAdapter(getApplicationContext(), searchView, searchableInfo);
		searchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// String query = intent.getStringExtra(SearchManager.QUERY);
			// query = query.toLowerCase();
			// StringBuilder sb = new
			// StringBuilder().append(PoiContract.KEY_WORDS).append(" like ?");
			// String[] args = new String[] { "%" + query + "%" };
			// Cursor c = getContentResolver().query(PoiContract.CONTENT_URI,
			// null, sb.toString(), args, null);
			// searchOnMap(c, query);
			// c.close();
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// Handle a suggestions click (because all the suggestions use
			// ACTION_VIEW)
			try {
				Uri data = intent.getData();
				Long id = Long.parseLong(data.getLastPathSegment());
				searchOnMap(mMarkers.get(id));
			} catch (NumberFormatException e) {
				Log.e(TAG, "Could not find marker for place", e);
			}
		}
	}

	private void setUpMarkers(List<Place> places) {
		mMarkers.clear();
		for (Place place : places) {
			LatLng latLng = new LatLng(place.getLatitude(), place.getLongtitude());
			Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).title(place.getSymbol())
					.snippet(place.getDescription()));
			mMarkers.put(place.getId(), marker);
		}
	}

	private void searchOnMap(Marker marker) {
		mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), new ZoomAnimationCalback(marker));
	}

	@Override
	public Loader<PlacesList> onCreateLoader(int id, Bundle bundle) {
		if (PLACES_LOADER == id) {
			int action = bundle.getInt(PlacesLoader.ARG_ACTION);
			String arg = bundle.getString(PlacesLoader.ARG_ARG);
			return new PlacesLoader(getApplicationContext(), action, arg);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<PlacesList> loader, PlacesList data) {
		if (PLACES_LOADER == loader.getId()) {
			setUpMarkers(data.places);
		}

	}

	@Override
	public void onLoaderReset(Loader<PlacesList> loader) {
		if (PLACES_LOADER == loader.getId()) {
			mMap.clear();
			mMarkers.clear();
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		Cursor c = mSearchSuggestionAdapter.runQueryOnBackgroundThread(newText);
		mSearchSuggestionAdapter.changeCursor(c);
		return true;
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

	/**
	 * Callback called when map animate to given marker
	 * 
	 * @author lecho
	 * 
	 */
	private static class ZoomAnimationCalback implements GoogleMap.CancelableCallback {
		private Marker mMarker;

		public ZoomAnimationCalback(Marker marker) {
			mMarker = marker;
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onFinish() {
			mMarker.showInfoWindow();
		}
	}

}
