package lecho.app.campus.activity;

import java.util.HashMap;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.adapter.MarkerInfoWindowAdapter;
import lecho.app.campus.adapter.SearchResultFragmentAdapter;
import lecho.app.campus.adapter.SearchSuggestionAdapter;
import lecho.app.campus.dao.Place;
import lecho.app.campus.fragment.PlaceDetailsFragment;
import lecho.app.campus.fragment.SearchResultFragment.OnSearchResultClickListener;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

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

public class CampusMapActivity extends SherlockFragmentActivity implements LoaderCallbacks<PlacesList>,
		OnSearchResultClickListener {
	private static final String TAG = "CampusMapActivity";
	private static final int PLACES_LOADER = CampusMapActivity.class.hashCode();
	private static final int CAMERA_ANIMATION_LENGTH = 500;
	private static final String EXTRA_CURRENT_PLACE_ID = "lecho.app.campus:CURRENT_PLACE_ID";
	private static final String EXTRA_CURRENT_LOADER_ACTION = "lecho.app.campus:CURRENT_LOADER_ACTION";
	private static final String EXTRA_CURRENT_LOADER_ARGUMENT = "lecho.app.campus:CURRENT_LOADER_ARGUMENT";
	private static final String EXTRA_DETAILS_VISIBLE = "lecho.app.campus:DETAILS_VISIBLE";
	private ViewPager mViewPager;
	private GoogleMap mMap;
	private MenuItem mSearchMenuItem;
	private MessageBar mMessageBar;
	private SearchSuggestionAdapter mSearchSuggestionAdapter;
	private SearchResultFragmentAdapter mSearchResultAdapter;
	// Maps place ID to Marker
	private HashMap<Long, Marker> mMarkers = new HashMap<Long, Marker>();
	// Maps marker to Place
	private HashMap<Marker, Place> mMarkersData = new HashMap<Marker, Place>();
	private Long mCurrentPlaceId;
	private int mCurrentLoaderAction;
	private String mCurrentLoaderArgument;
	private Animation mSearchResultsPagerShowAnim;
	private Animation mSearchResultsPagerHideAnim;
	private boolean mDetailsVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus_map);

		int playServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		Log.i(TAG, "connection result: " + playServicesStatus);

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(new SearchResultChangeListener());
		mSearchResultsPagerShowAnim = AnimationUtils.loadAnimation(this, R.anim.slide_show);
		mSearchResultsPagerHideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_hide);
		mSearchResultsPagerHideAnim.setAnimationListener(new HideSearchResultAnimationListener());
		mMessageBar = new MessageBar(this);
		mMessageBar.setOnClickListener(new MessageBarButtonListener());
		// Listen when user hides details fragment to show search menu on action bar
		getSupportFragmentManager().addOnBackStackChangedListener(new BackStackChangeListener());

		// SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		if (savedInstanceState == null) {
			// mapFragment.setRetainInstance(true);
			mCurrentPlaceId = Long.MIN_VALUE;
			mCurrentLoaderAction = PlacesLoader.LOAD_ALL_PLACES;
			mDetailsVisible = false;
		} else {
			// mMap = mapFragment.getMap();
			mCurrentPlaceId = savedInstanceState.getLong(EXTRA_CURRENT_PLACE_ID);
			mCurrentLoaderAction = savedInstanceState.getInt(EXTRA_CURRENT_LOADER_ACTION);
			mCurrentLoaderArgument = savedInstanceState.getString(EXTRA_CURRENT_LOADER_ARGUMENT);
			mDetailsVisible = savedInstanceState.getBoolean(EXTRA_DETAILS_VISIBLE);
		}

		setUpMapIfNeeded();
		initLoader(false, mCurrentLoaderAction, mCurrentLoaderArgument);
	}

	private void initLoader(boolean isRestart, int action, String argument) {
		Bundle args = new Bundle();
		mCurrentLoaderAction = action;
		mCurrentLoaderArgument = argument;
		args.putInt(PlacesLoader.EXTRA_ACTION, action);
		args.putString(PlacesLoader.EXTRA_ARGUMENT, argument);
		if (isRestart) {
			mCurrentPlaceId = Long.MIN_VALUE;
			getSupportLoaderManager().restartLoader(PLACES_LOADER, args, this);
		} else {
			getSupportLoaderManager().initLoader(PLACES_LOADER, args, this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// setUpMapIfNeeded();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_CURRENT_LOADER_ACTION, mCurrentLoaderAction);
		outState.putString(EXTRA_CURRENT_LOADER_ARGUMENT, mCurrentLoaderArgument);
		outState.putLong(EXTRA_CURRENT_PLACE_ID, mCurrentPlaceId);
		outState.putBoolean(EXTRA_DETAILS_VISIBLE, mDetailsVisible);
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
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		zoomMapOnStart();
	}

	private void zoomMapOnStart() {
		if (mCurrentPlaceId > 0) {
			// Map should animate to current marker, no need to zoom to bounds.
			return;
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_campus_map, menu);
		setUpSearchView(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		// Only if place details are visible.
		if (id == android.R.id.home) {
			getSupportFragmentManager().popBackStack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Prepares SearchView on ActionBar.
	 * 
	 * @param menu
	 */
	private void setUpSearchView(Menu menu) {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		// Search view preparation Workaround for but http://code.google.com/p/android/issues/detail?id=25758
		mSearchMenuItem = menu.findItem(R.id.search);
		mSearchMenuItem.setOnActionExpandListener(new SearchViewExpandListener());

		SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		searchView.setOnQueryTextFocusChangeListener(new SearchViewFocusChangeListener());
		// Set custom adapter for custom dropdown view
		mSearchSuggestionAdapter = new SearchSuggestionAdapter(getApplicationContext(), searchView, searchableInfo);
		searchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// Handle search button click
			String query = intent.getStringExtra(SearchManager.QUERY);
			initLoader(true, PlacesLoader.LOAD_PLACES_BY_SEARCH, query);
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// Handle a suggestions click (because all the suggestions use ACTION_VIEW)
			try {
				Uri data = intent.getData();
				Long id = Long.parseLong(data.getLastPathSegment());
				goToMarker(mMarkers.get(id));
			} catch (NumberFormatException e) {
				Log.e(TAG, "Could not find marker for place", e);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (!mDetailsVisible && mCurrentPlaceId > 0) {
			// If there is selected marker - clear that selection.
			Marker marker = mMarkers.get(mCurrentPlaceId);
			marker.hideInfoWindow();
			mCurrentPlaceId = Long.MIN_VALUE;
			hideSearchResultsPager();
		} else {
			super.onBackPressed();
		}
	}

	private void setUpMarkers(List<Place> places) {
		if (null == mMap) {
			Log.e(TAG, "Could not set up markers - GoogleMap is null");
			return;
		}
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

	/**
	 * Move map camera to selected marker.
	 * 
	 * @param marker
	 */
	private void goToMarker(final Marker marker) {
		if (mMap.getCameraPosition().target.equals(marker.getPosition())) {
			handleMarker(marker);
			marker.showInfoWindow();
		} else {
			mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), CAMERA_ANIMATION_LENGTH,
					new MapCameraAnimationCalback(marker));
		}
	}

	/**
	 * Performs necessary actions when user click on the marker or camera is moved to that marker.
	 * 
	 * @param marker
	 */
	private void handleMarker(final Marker marker) {
		Place place = mMarkersData.get(marker);
		if (null == place) {
			// TODO Investigate this.
			Log.e(TAG, "Cannot handle marker, null place associated with marker: " + marker.getTitle());
			return;
		}
		mCurrentPlaceId = place.getId();
		int pos = mSearchResultAdapter.getItemPosition(place);
		mViewPager.setCurrentItem(pos);
		showSearchResultsPager();
	}

	/**
	 * Shows search results pager with animation only if pager is not yet visible.
	 */
	private void showSearchResultsPager() {
		if (mViewPager.getVisibility() != View.VISIBLE) {
			mViewPager.setVisibility(View.VISIBLE);
			mViewPager.startAnimation(mSearchResultsPagerShowAnim);
		}
	}

	/**
	 * Hides search results pager with animation only if pager is visible.
	 */
	private void hideSearchResultsPager() {
		if (mViewPager.getVisibility() == View.VISIBLE) {
			mViewPager.startAnimation(mSearchResultsPagerHideAnim);
		}
	}

	@Override
	public void onSearchResultClick(Long placeId) {
		mDetailsVisible = true;
		Fragment fragment = PlaceDetailsFragment.newInstance(placeId);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_show, R.anim.slide_hide, R.anim.slide_show, R.anim.slide_hide);
		transaction.add(R.id.details, fragment);
		transaction.addToBackStack(PlaceDetailsFragment.TAG);
		transaction.commit();
		mSearchMenuItem.collapseActionView();
		mSearchMenuItem.setVisible(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public Loader<PlacesList> onCreateLoader(int id, Bundle bundle) {
		if (PLACES_LOADER == id) {
			int action = bundle.getInt(PlacesLoader.EXTRA_ACTION);
			String argument = bundle.getString(PlacesLoader.EXTRA_ARGUMENT);
			return new PlacesLoader(getApplicationContext(), action, argument);
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
					hideSearchResultsPager();
					mViewPager.setAdapter(mSearchResultAdapter);
				}
			} else if (PlacesLoader.LOAD_PLACES_BY_SEARCH == action) {
				setUpMarkers(data.mPlaces);
				mSearchResultAdapter = new SearchResultFragmentAdapter(getSupportFragmentManager(), data.mPlaces);
				if (null != mViewPager) {
					showSearchResultsPager();
					mViewPager.setAdapter(mSearchResultAdapter);
					// Zoom to show all results, chose first one as active
					if (data.mPlaces.size() > 0) {
						// final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
						// if (mapView.getWidth() > 0 & mapView.getHeight() > 0) {
						// Long placeId = data.mPlaces.get(0).getId();
						// Marker marker = mMarkers.get(placeId);
						// goToMarker(marker);
						// }
						if (mCurrentPlaceId < 0) {
							// TODO NPE check.
							mCurrentPlaceId = data.mPlaces.get(0).getId();
						}
					} else {
						mMessageBar.show(getString(R.string.search_no_results),
								getString(R.string.search_no_results_back));
					}
				}
			} else if (PlacesLoader.LOAD_PLACES_BY_CATEGORY == action) {

			} else if (PlacesLoader.LOAD_PLACES_BY_FACULTY == action) {

			} else {
				Log.e(TAG, "Invalid PlacesLoader action: " + action);
				throw new IllegalArgumentException("Invalid PlacesLoader action: " + action);
			}

			// If marker should be "clicked" after load finished.
			if (mCurrentPlaceId > 0 && !mMarkers.isEmpty()) {
				goToMarker(mMarkers.get(mCurrentPlaceId));
			}
		}

	}

	@Override
	public void onLoaderReset(Loader<PlacesList> loader) {
		if (PLACES_LOADER == loader.getId()) {
			mCurrentPlaceId = Long.MIN_VALUE;
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
	private class MapCameraAnimationCalback implements GoogleMap.CancelableCallback {
		private Marker mMarker;

		public MapCameraAnimationCalback(Marker marker) {
			mMarker = marker;
		}

		@Override
		public void onCancel() {
			Log.e(TAG, "Animation canceled");
		}

		@Override
		public void onFinish() {
			Log.e(TAG, "Animation finished");
			handleMarker(mMarker);
			mMarker.showInfoWindow();
		}
	}

	/**
	 * Listen to changes of current selected search result.
	 * 
	 * @author Lecho
	 * 
	 */
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
			Place place = mMarkersData.get(marker);
			onSearchResultClick(place.getId());

		}

	}

	/**
	 * Selects marker.
	 * 
	 * @author Lecho
	 * 
	 */
	private class MarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker marker) {
			handleMarker(marker);
			return false;
		}

	}

	/**
	 * Clears marker selection.
	 * 
	 * @author Lecho
	 * 
	 */
	private class MapClickListener implements OnMapClickListener {

		@Override
		public void onMapClick(LatLng latLng) {
			mCurrentPlaceId = Long.MIN_VALUE;
			hideSearchResultsPager();

		}

	}

	/**
	 * Clears search results after SearchView collapse. All methods have to return true for SearchView to work properly.
	 * 
	 * @author Lecho
	 * 
	 */
	private class SearchViewExpandListener implements OnActionExpandListener {

		@Override
		public boolean onMenuItemActionExpand(MenuItem item) {
			// No action here.
			return true;
		}

		@Override
		public boolean onMenuItemActionCollapse(MenuItem item) {
			// If user collapse search view return to all places.
			if (!mDetailsVisible) {
				initLoader(true, PlacesLoader.LOAD_ALL_PLACES, "");
			}
			return true;
		}
	}

	/**
	 * Listen to click on MessageBar button.
	 * 
	 * @author Lecho
	 * 
	 */
	private class MessageBarButtonListener implements OnMessageClickListener {

		@Override
		public void onMessageClick(Parcelable token) {
			if (null != mSearchMenuItem) {
				mSearchMenuItem.collapseActionView();
			}
		}

	}

	/**
	 * Restores SearchView and hides home menu when user go back from details.
	 * 
	 * @author Lecho
	 * 
	 */
	private class BackStackChangeListener implements OnBackStackChangedListener {

		@Override
		public void onBackStackChanged() {
			int count = getSupportFragmentManager().getBackStackEntryCount();
			if (0 == count) {
				mDetailsVisible = false;
				mSearchMenuItem.setVisible(true);
				getSupportActionBar().setHomeButtonEnabled(false);
				getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			}
		}
	}

	private class HideSearchResultAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			mViewPager.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

	}

	private class SearchViewFocusChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				mSearchMenuItem.collapseActionView();
			}
		}

	}

}
