package lecho.app.campus.activity;

import java.util.HashMap;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.adapter.MarkerInfoWindowAdapter;
import lecho.app.campus.adapter.SearchResultFragmentAdapter;
import lecho.app.campus.adapter.SearchSuggestionAdapter;
import lecho.app.campus.dao.Place;
import lecho.app.campus.loader.PlacesLoader;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.PlacesList;
import net.simonvt.messagebar.MessageBar;
import net.simonvt.messagebar.MessageBar.OnMessageClickListener;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CampusMapActivity extends SherlockFragmentActivity implements LoaderCallbacks<PlacesList> {
	private static final String TAG = CampusMapActivity.class.getSimpleName();
	private static final int PLACES_LOADER = CampusMapActivity.class.hashCode();
	private ViewPager mViewPager;
	private GoogleMap mMap;
	private MenuItem mSearchMenuItem;
	private MessageBar mMessageBar;
	private SearchSuggestionAdapter mSearchSuggestionAdapter;
	private SearchResultFragmentAdapter mSearchResultAdapter;
	// TODO check WeakHashMap
	private HashMap<Long, Marker> mMarkers = new HashMap<Long, Marker>();
	private HashMap<Marker, Place> mMarkersData = new HashMap<Marker, Place>();
	private Marker mCurrentMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus_map);

		int playServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		Log.i(TAG, "connection result: " + playServicesStatus);

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(new SearchResultChangeListener());
		mMessageBar = new MessageBar(this);
		mMessageBar.setOnClickListener(new MessageBarButtonListener());
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
		mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(getApplicationContext()));
		mMap.setOnMapClickListener(new MapClickListener());
		mMap.setOnMarkerClickListener(new MarkerClickListener());
		mMap.setOnInfoWindowClickListener(new MarkerInfoWindowClickListener());
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		zoomMapOnStart();
	}

	private void zoomMapOnStart() {
		final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					LatLng latLangS = new LatLng(Config.START_LAT1, Config.START_LNG1);
					LatLng latLangN = new LatLng(Config.START_LAT2, Config.START_LNG2);
					LatLngBounds bounds = new LatLngBounds(latLangS, latLangN);
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
				}
			});
		}
	}

	@Override
	protected void onPause() {
		if (null != mSearchSuggestionAdapter) {
			mSearchSuggestionAdapter.changeCursor(null);
		}
		super.onPause();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_campus_map, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		// Search view preparation
		// Workaround for but
		// http://code.google.com/p/android/issues/detail?id=25758
		mSearchMenuItem = menu.findItem(R.id.search);
		mSearchMenuItem.setOnActionExpandListener(new SearchViewExpandListener());

		SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		// Set custom adapter for custom dropdown view
		mSearchSuggestionAdapter = new SearchSuggestionAdapter(getApplicationContext(), searchView, searchableInfo);
		searchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Bundle args = new Bundle();
			args.putInt(PlacesLoader.ARG_ACTION, PlacesLoader.LOAD_PLACES_BY_SEARCH);
			args.putString(PlacesLoader.ARG_ARG, query);
			getSupportLoaderManager().restartLoader(PLACES_LOADER, args, this);
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// Handle a suggestions click (because all the suggestions use
			// ACTION_VIEW)
			try {
				Uri data = intent.getData();
				Long id = Long.parseLong(data.getLastPathSegment());
				goToMarker(mMarkers.get(id));
			} catch (NumberFormatException e) {
				Log.e(TAG, "Could not find marker for place", e);
			}
		}
	}

	private void setUpMarkers(List<Place> places) {
		mMap.clear();
		mMarkers.clear();
		mMarkersData.clear();
		for (Place place : places) {
			LatLng latLng = new LatLng(place.getLatitude(), place.getLongtitude());
			Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_default)).title(place.getSymbol())
					.snippet(place.getDescription()));
			mMarkers.put(place.getId(), marker);
			mMarkersData.put(marker, place);
		}
	}

	private void goToMarker(final Marker marker) {
		mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), new ZoomAnimationCalback(marker));
	}

	private void handleMarker(Marker marker) {
		mCurrentMarker = marker;
		mViewPager.setVisibility(View.VISIBLE);
		Place place = mMarkersData.get(marker);
		int pos = mSearchResultAdapter.getItemPosition(place);
		mViewPager.setCurrentItem(pos);
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
			int action = data.mAction;
			if (PlacesLoader.LOAD_ALL_PLACES == action) {
				setUpMarkers(data.mPlaces);
				mSearchResultAdapter = new SearchResultFragmentAdapter(getSupportFragmentManager(), data.mPlaces);
				if (null != mViewPager) {
					mViewPager.setVisibility(View.GONE);
					mViewPager.setAdapter(mSearchResultAdapter);
				}
			} else if (PlacesLoader.LOAD_PLACES_BY_SEARCH == action) {
				setUpMarkers(data.mPlaces);
				mSearchResultAdapter = new SearchResultFragmentAdapter(getSupportFragmentManager(), data.mPlaces);
				if (null != mViewPager) {
					mViewPager.setVisibility(View.VISIBLE);
					mViewPager.setAdapter(mSearchResultAdapter);
					if (data.mPlaces.size() > 0) {
						Long placeId = data.mPlaces.get(0).getId();
						Marker marker = mMarkers.get(placeId);
						goToMarker(marker);
					} else {
						mMessageBar.show(getString(R.string.search_no_results),
								getString(R.string.search_no_results_back));
					}
				}
			} else if (PlacesLoader.LOAD_PLACES_BY_CATEGORY == action) {

			} else if (PlacesLoader.LOAD_PLACES_BY_FACULTY == action) {

			} else {
				Log.e(TAG, "Invalid PlacesLoader action: " + action);
			}
		}

	}

	@Override
	public void onLoaderReset(Loader<PlacesList> loader) {
		if (PLACES_LOADER == loader.getId()) {
			mMap.clear();
			mMarkers.clear();
			mMarkersData.clear();
		}
	}

	/**
	 * Callback called when map animate to given marker
	 * 
	 * @author lecho
	 * 
	 */
	private class ZoomAnimationCalback implements GoogleMap.CancelableCallback {
		private Marker mMarker;

		public ZoomAnimationCalback(Marker marker) {
			mMarker = marker;
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onFinish() {
			handleMarker(mMarker);
			mMarker.showInfoWindow();
		}
	}

	private class SearchResultChangeListener extends SimpleOnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			Long id = mSearchResultAdapter.getItemId(position);
			Marker marker = mMarkers.get(id);
			goToMarker(marker);
		}
	}

	private class MarkerInfoWindowClickListener implements OnInfoWindowClickListener {

		@Override
		public void onInfoWindowClick(Marker marker) {
			Intent i = new Intent(CampusMapActivity.this, PlaceDetailsActivity.class);
			i.putExtra(Config.ARG_PLACE_ID, mMarkersData.get(marker).getId());
			startActivity(i);

		}

	}

	private class MarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker marker) {
			handleMarker(marker);
			return false;
		}

	}

	private class MapClickListener implements OnMapClickListener {

		@Override
		public void onMapClick(LatLng latLng) {
			mCurrentMarker = null;
			mViewPager.setVisibility(View.GONE);

		}

	}

	private class SearchViewExpandListener implements OnActionExpandListener {

		@Override
		public boolean onMenuItemActionExpand(MenuItem item) {
			return true;
		}

		@Override
		public boolean onMenuItemActionCollapse(MenuItem item) {
			Bundle args = new Bundle();
			args.putInt(PlacesLoader.ARG_ACTION, PlacesLoader.LOAD_ALL_PLACES);
			getSupportLoaderManager().restartLoader(PLACES_LOADER, args, CampusMapActivity.this);
			return true;
		}
	}

	private class MessageBarButtonListener implements OnMessageClickListener {

		@Override
		public void onMessageClick(Parcelable token) {
			// Bundle args = new Bundle();
			// args.putInt(PlacesLoader.ARG_ACTION,
			// PlacesLoader.LOAD_ALL_PLACES);
			// getSupportLoaderManager().restartLoader(PLACES_LOADER, args,
			// CampusMapActivity.this);
			if (null != mSearchMenuItem) {
				mSearchMenuItem.collapseActionView();
			}
		}

	}

}
