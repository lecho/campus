package lecho.app.campus.activity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.app.campus.R;
import lecho.app.campus.adapter.MarkerInfoWindowAdapter;
import lecho.app.campus.adapter.NavigationDrawerAdapter;
import lecho.app.campus.adapter.SearchResultFragmentAdapter;
import lecho.app.campus.adapter.SearchSuggestionAdapter;
import lecho.app.campus.dao.Place;
import lecho.app.campus.fragment.SearchResultFragment.OnSearchResultClickListener;
import lecho.app.campus.fragment.dialog.NoInternetConnectionDialogFragment;
import lecho.app.campus.fragment.dialog.PlayServicesErrorDialogFragment;
import lecho.app.campus.loader.PlacesLoader;
import lecho.app.campus.utils.ABSMenuItemConverter;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.NavigationDrawerItem;
import lecho.app.campus.utils.PlacesList;
import lecho.app.campus.utils.Utils;
import net.simonvt.messagebar.MessageBar;
import net.simonvt.messagebar.MessageBar.OnMessageClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.common.ConnectionResult;
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
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1;
	private static final int PLACES_LOADER = 1;
	private static final int CAMERA_ANIMATION_DURATION = 500;
	private static final String EXTRA_CURRENT_PLACE_ID = "lecho.app.campus:CURRENT_PLACE_ID";
	private static final String EXTRA_CURRENT_LOADER_ACTION = "lecho.app.campus:CURRENT_LOADER_ACTION";
	private static final String EXTRA_CURRENT_LOADER_ARGUMENT = "lecho.app.campus:CURRENT_LOADER_ARGUMENT";
	private static final String EXTRA_CURRENT_DRAWER_ITEM = "lecho.app.campus:CURRENT_DRAWER_ITEM";
	private ViewPager mViewPager;
	private GoogleMap mMap;
	private MenuItem mSearchMenuItem;
	private MessageBar mMessageBar;
	private SearchSuggestionAdapter mSearchSuggestionAdapter;
	private SearchResultFragmentAdapter mSearchResultAdapter;
	// Maps place ID to Marker
	private Map<Long, Marker> mMarkers = new HashMap<Long, Marker>();
	// Maps marker to Place
	private Map<Marker, Place> mMarkersData = new HashMap<Marker, Place>();
	private Long mCurrentPlaceId;
	private int mCurrentLoaderAction;
	private String mCurrentLoaderArgument;
	private Animation mSearchResultsPagerShowAnim;
	private Animation mSearchResultsPagerHideAnim;
	private int mSearchResultSize;

	// Nav-Drawer related
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private int mCurrentDrawerItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus_map);
		if (null == savedInstanceState) {
			// Check Play Services availability.
			if (checkPlayServices()) {
				// If device was online before there should be some map cache.
				boolean deviceWasOnline = checkIfMapWasCached();
				if (!deviceWasOnline) {
					// Check connection only if app wasn't online before and if Play Services are available.
					checkInternetConnection();
				}
			}
		}

		// *** Navi-Drawer
		setUpNavigationDrawer();
		// ***
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(new SearchResultChangeListener());
		mSearchResultsPagerShowAnim = AnimationUtils.loadAnimation(this, R.anim.slide_show);
		mSearchResultsPagerHideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_hide);
		mSearchResultsPagerHideAnim.setAnimationListener(new HideSearchResultAnimationListener());
		mMessageBar = new MessageBar(this);
		mMessageBar.setOnClickListener(new MessageBarButtonListener());
		mSearchResultSize = getResources().getDimensionPixelSize(R.dimen.search_result_height);

		if (savedInstanceState == null) {
			mCurrentPlaceId = Long.MIN_VALUE;
			mCurrentLoaderAction = PlacesLoader.LOAD_ALL_PLACES;
			mCurrentDrawerItem = Integer.MIN_VALUE;
		} else {
			mCurrentPlaceId = savedInstanceState.getLong(EXTRA_CURRENT_PLACE_ID);
			mCurrentLoaderAction = savedInstanceState.getInt(EXTRA_CURRENT_LOADER_ACTION);
			mCurrentLoaderArgument = savedInstanceState.getString(EXTRA_CURRENT_LOADER_ARGUMENT);
			mCurrentDrawerItem = savedInstanceState.getInt(EXTRA_CURRENT_DRAWER_ITEM);
		}

		setUpMapIfNeeded();
	}

	private boolean checkPlayServices() {
		final int playServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (playServicesStatus == ConnectionResult.SUCCESS) {
			Log.i(TAG, "Play Services status SUCCESS");
			return true;
		} else {
			Log.i(TAG, "Play Services status ERROR: " + playServicesStatus);
			if (GooglePlayServicesUtil.isUserRecoverableError(playServicesStatus)) {
				Log.i(TAG, "Play Services user recoverable - proceed by calling error dialog");
				DialogFragment dialog = PlayServicesErrorDialogFragment.newInstance(playServicesStatus,
						REQUEST_CODE_RECOVER_PLAY_SERVICES);
				FragmentManager fm = getSupportFragmentManager();
				dialog.show(fm, "play-services-dialog");
			} else {
				Log.i(TAG, "Play Services not user recoverable - finishing app");
				Toast.makeText(getApplicationContext(), R.string.play_services_missing, Toast.LENGTH_SHORT).show();
				finish();
			}

		}
		return false;
	}

	private boolean checkIfMapWasCached() {
		SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		// Value will be true only if GoogleMap was initialized successful before;
		boolean deviceWasOnline = prefs.getBoolean(Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE, false);
		if (!deviceWasOnline) {
			// Hackish way to check if map was cached.
			StringBuilder cacheFileNameBuilder = new StringBuilder("cache_vts_").append(Config.APP_PACKAGE)
					.append(".0");
			if (new File(getExternalCacheDir(), cacheFileNameBuilder.toString()).exists()) {
				setDeviceWasOnlineFlag();
				deviceWasOnline = true;
			}
		}
		return deviceWasOnline;
	}

	private void checkInternetConnection() {
		if (!Utils.isOnline(getApplicationContext())) {
			Log.w(TAG, "Device is offline");
			FragmentManager fm = getSupportFragmentManager();
			DialogFragment dialog = NoInternetConnectionDialogFragment.newInstance();
			dialog.show(fm, "no-internet-dialog");
		} else {
			setDeviceWasOnlineFlag();
		}
	}

	private void setDeviceWasOnlineFlag() {
		SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		// Value will be true only if GoogleMap was initialized successful before;
		final boolean deviceWasOnline = prefs.getBoolean(Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE, false);
		if (!deviceWasOnline) {
			prefs.edit().putBoolean(Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE, true).commit();
		}
	}

	private void setUpNavigationDrawer() {
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		// Find navi-drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// set custom drawer shadow
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new NavigationDrawerAdapter(getApplicationContext(), 0, Config.NAVIGATION_DRAWER_ITEMS));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new DrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_RECOVER_PLAY_SERVICES:
			// User doesn't resolve Play Services problem
			if (resultCode == RESULT_CANCELED) {
				Log.w(TAG, "Google Play Services resolution activity canceled!, finishing app");
				Toast.makeText(getApplicationContext(), R.string.play_services_recovery_canceled, Toast.LENGTH_SHORT)
						.show();
				finish();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_CURRENT_LOADER_ACTION, mCurrentLoaderAction);
		outState.putString(EXTRA_CURRENT_LOADER_ARGUMENT, mCurrentLoaderArgument);
		outState.putLong(EXTRA_CURRENT_PLACE_ID, mCurrentPlaceId);
		outState.putInt(EXTRA_CURRENT_DRAWER_ITEM, mCurrentDrawerItem);
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
				initLoader(false, mCurrentLoaderAction, mCurrentLoaderArgument);
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
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);
		zoomMapOnStart();
	}

	private void initLoader(boolean isRestart, int action, String argument) {
		Bundle args = new Bundle();
		mCurrentLoaderAction = action;
		mCurrentLoaderArgument = argument;
		args.putInt(PlacesLoader.EXTRA_ACTION, action);
		args.putString(PlacesLoader.EXTRA_ARGUMENT, argument);
		if (isRestart) {
			getSupportLoaderManager().restartLoader(PLACES_LOADER, args, this);
		} else {
			getSupportLoaderManager().initLoader(PLACES_LOADER, args, this);
		}
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
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					zoomMapToDefault();
				}
			});
		}
	}

	private void zoomMapToDefault() {
		LatLng latLangS = new LatLng(Config.START_LAT1, Config.START_LNG1);
		LatLng latLangN = new LatLng(Config.START_LAT2, Config.START_LNG2);
		LatLngBounds bounds = new LatLngBounds(latLangS, latLangN);
		mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0), CAMERA_ANIMATION_DURATION, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_campus_map, menu);
		setUpSearchView(menu);
		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(ABSMenuItemConverter.create(item))) {
			return true;
		} else if (item.getItemId() == R.id.about) {
			Intent intent = new Intent(this, AboutAppActivity.class);
			startActivity(intent);
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
			mCurrentPlaceId = Long.MIN_VALUE;
			clearCurrentDrawerItem();
			initLoader(true, PlacesLoader.LOAD_PLACES_BY_SEARCH, query);
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// Handle a suggestions click (because all the suggestions use ACTION_VIEW)
			try {
				Uri data = intent.getData();
				Long placeId = Long.parseLong(data.getLastPathSegment());
				Marker marker = mMarkers.get(placeId);
				if (null == marker && mCurrentDrawerItem >= 0) {
					// Marker is not currently visible, probably drawer item is selected, clear drawer item selection
					// and
					// load all places.
					mCurrentPlaceId = placeId;
					clearCurrentDrawerItem();
					initLoader(true, PlacesLoader.LOAD_ALL_PLACES, "");
				} else {
					// Just go to selected marker.
					goToMarker(mMarkers.get(placeId));
				}
			} catch (NumberFormatException e) {
				Log.e(TAG, "Could not find marker for place", e);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mCurrentPlaceId > 0) {
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
			LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
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
		if (null == marker) {
			Log.e(TAG, "Cannot go to NULL smarker");
			return;
		}
		if (mMap.getCameraPosition().target.equals(marker.getPosition())) {
			handleMarker(marker);
			marker.showInfoWindow();
		} else {
			mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), CAMERA_ANIMATION_DURATION,
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
			Log.e(TAG, "Cannot handle marker, NULL place associated with marker: " + marker.getTitle());
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
			mMap.setPadding(0, 0, 0, mSearchResultSize);
		}
	}

	/**
	 * Hides search results pager with animation only if pager is visible.
	 */
	private void hideSearchResultsPager() {
		if (mViewPager.getVisibility() == View.VISIBLE) {
			mViewPager.startAnimation(mSearchResultsPagerHideAnim);
			mMap.setPadding(0, 0, 0, 0);
		}
	}

	@Override
	public void onSearchResultClick(Long placeId) {
		Intent intent = new Intent(this, PlaceDetailsActivity.class);
		intent.putExtra(Config.EXTRA_PLACE_ID, placeId);
		startActivity(intent);
	}

	private void selectDrawerItem(int position) {
		mDrawerLayout.closeDrawer(mDrawerList);
		mCurrentPlaceId = Long.MIN_VALUE;
		NavigationDrawerItem item = Config.NAVIGATION_DRAWER_ITEMS[position];
		if (position == mCurrentDrawerItem) {
			clearCurrentDrawerItem();
			initLoader(true, PlacesLoader.LOAD_ALL_PLACES, "");
		} else {
			initLoader(true, item.action, item.argument);
			mDrawerList.setItemChecked(position, true);
			mCurrentDrawerItem = position;
		}
	}

	private void clearCurrentDrawerItem() {
		mDrawerList.setItemChecked(mCurrentDrawerItem, false);
		mCurrentDrawerItem = Integer.MIN_VALUE;
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
				handleLoaderResult(data);
			} else if (PlacesLoader.LOAD_PLACES_BY_SEARCH == action) {
				handleLoaderResult(data);
				if (data.mPlaces.size() > 0) {
					if (mCurrentPlaceId < 0) {
						mCurrentPlaceId = data.mPlaces.get(0).getId();
					}
				} else {
					mMessageBar.show(getString(R.string.search_no_results), getString(R.string.search_no_results_back));
				}
			} else if (PlacesLoader.LOAD_PLACES_BY_CATEGORY == action) {
				handleLoaderResult(data);
			} else if (PlacesLoader.LOAD_PLACES_BY_FACULTY == action) {
				handleLoaderResult(data);
			} else {
				Log.e(TAG, "Invalid PlacesLoader action: " + action);
				throw new IllegalArgumentException("Invalid PlacesLoader action: " + action);
			}
			// If marker should be "clicked" after load finished.
			if (mCurrentPlaceId > 0 && !mMarkers.isEmpty()) {
				goToMarker(mMarkers.get(mCurrentPlaceId));
			} else {
				if (null != mViewPager) {
					hideSearchResultsPager();
					final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
					if (mapView.getWidth() > 0 && mapView.getHeight() > 0) {
						zoomMapToDefault();
					}
				}
			}
		}

	}

	private void handleLoaderResult(PlacesList data) {
		setUpMarkers(data.mPlaces);
		mSearchResultAdapter = new SearchResultFragmentAdapter(getSupportFragmentManager(), data.mPlaces);
		if (null != mViewPager) {
			mViewPager.setAdapter(mSearchResultAdapter);
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
		}

		@Override
		public void onFinish() {
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
			if (null == place) {
				Log.e(TAG,
						"Cannot handle marker onInfoWindowClick, null place associated with marker: "
								+ marker.getTitle());
				return;
			}
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
			// If user collapse search view *after search was performed* return to all places.
			if (mCurrentLoaderAction == PlacesLoader.LOAD_PLACES_BY_SEARCH) {
				mCurrentPlaceId = Long.MIN_VALUE;
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

	/**
	 * The click listner for ListView in the navigation drawer
	 * */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectDrawerItem(position);
		}
	}

	private class DrawerToggle extends ActionBarDrawerToggle {

		public DrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes,
				int openDrawerContentDescRes, int closeDrawerContentDescRes) {
			super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			super.onDrawerStateChanged(newState);
		}

		@Override
		public void onDrawerOpened(View drawerView) {
			super.onDrawerOpened(drawerView);
			if (mSearchMenuItem.isActionViewExpanded()) {
				// Collapse search view for supportInvalidateOptionsMenu() to work properly.
				mSearchMenuItem.collapseActionView();
			}
			supportInvalidateOptionsMenu();
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			super.onDrawerClosed(drawerView);
			supportInvalidateOptionsMenu();
		}
	}

}
