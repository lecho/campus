package lecho.app.campus.activity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.app.campus.R;
import lecho.app.campus.adapter.MarkerInfoWindowAdapter;
import lecho.app.campus.adapter.NavigationDrawerAdapter;
import lecho.app.campus.adapter.SearchResultFragmentAdapter;
import lecho.app.campus.adapter.SearchSuggestionAdapter;
import lecho.app.campus.dao.DaoMaster;
import lecho.app.campus.dao.Place;
import lecho.app.campus.fragment.SearchResultFragment.OnSearchResultClickListener;
import lecho.app.campus.fragment.dialog.NoInternetConnectionDialogFragment;
import lecho.app.campus.fragment.dialog.PlayServicesErrorDialogFragment;
import lecho.app.campus.loader.PlacesLoader;
import lecho.app.campus.service.PopulateDBIntentService;
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
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Main application activity with map view.
 * 
 * @author Lecho
 * 
 */
public class CampusMapActivity extends SherlockFragmentActivity implements LoaderCallbacks<PlacesList>,
		OnSearchResultClickListener {
	private static final String TAG = "CampusMapActivity";
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1;
	private static final int REQUEST_CODE_PLACE_DETAILS = 2;
	private static final int PLACES_LOADER = 1;
	private static final String EXTRA_CURRENT_PLACE_ID = "lecho.app.campus:CURRENT_PLACE_ID";
	private static final String EXTRA_CURRENT_LOADER_ACTION = "lecho.app.campus:CURRENT_LOADER_ACTION";
	private static final String EXTRA_CURRENT_LOADER_ARGUMENT = "lecho.app.campus:CURRENT_LOADER_ARGUMENT";
	private static final String EXTRA_CURRENT_DRAWER_ITEM = "lecho.app.campus:CURRENT_DRAWER_ITEM";
	private static final String EXTRA_CURRENT_MAP_TYPE = "lecho.app.campus:CURRENT_MAP_TYPE";
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
	// Array of currently visible places identifiers, passed to details activity
	private long[] mVisiblePlaces;
	private Long mCurrentPlaceId;
	private int mCurrentLoaderAction;
	private String mCurrentLoaderArgument;
	private int mCurrentMapType;
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
		// *** Navi-Drawer
		setUpNavigationDrawer();
		// ***
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(new SearchResultChangeListener());
		mSearchResultsPagerShowAnim = AnimationUtils.loadAnimation(this, R.anim.slide_show);
		mSearchResultsPagerHideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_hide);
		mMessageBar = new MessageBar(this);
		mMessageBar.setOnClickListener(new MessageBarButtonListener());
		mSearchResultSize = getResources().getDimensionPixelSize(R.dimen.search_result_height);
		// Pre-checks
		if (null == savedInstanceState) {
			mCurrentPlaceId = Long.MIN_VALUE;
			mCurrentLoaderAction = PlacesLoader.LOAD_ALL_PLACES;
			mCurrentLoaderArgument = "";
			mCurrentDrawerItem = Integer.MIN_VALUE;
			mCurrentMapType = GoogleMap.MAP_TYPE_NORMAL;
			// Check if database has to be updated.
			final SharedPreferences prefs = getSharedPreferences(Config.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
			updateCampusDataIfNeeded(prefs);
			// Check Play Services availability.
			if (checkPlayServices()) {
				// If device was online before there should be some map cache.
				boolean deviceWasOnline = checkIfMapWasCached(prefs);
				if (!deviceWasOnline) {
					// Check connection only if app wasn't online before and if Play Services are available.
					checkInternetConnection(prefs);
					// Open drawer if this is first start.
					mDrawerLayout.openDrawer(mDrawerList);
				}
			}
			// }
		} else {
			mCurrentPlaceId = savedInstanceState.getLong(EXTRA_CURRENT_PLACE_ID);
			mCurrentLoaderAction = savedInstanceState.getInt(EXTRA_CURRENT_LOADER_ACTION);
			mCurrentLoaderArgument = savedInstanceState.getString(EXTRA_CURRENT_LOADER_ARGUMENT);
			mCurrentDrawerItem = savedInstanceState.getInt(EXTRA_CURRENT_DRAWER_ITEM);
			mCurrentMapType = savedInstanceState.getInt(EXTRA_CURRENT_MAP_TYPE);
		}
		// Finally set up map.
		setUpMapIfNeeded();
	}

	/**
	 * If there is new data version starts service that parse data xml and insert new data into database.
	 * 
	 * @param prefs
	 */
	private void updateCampusDataIfNeeded(final SharedPreferences prefs) {
		final int schemaVersion = prefs.getInt(Config.APP_SHARED_PREFS_SCHEMA_VERSION, 0);
		final String language = prefs.getString(Config.APP_SHARED_PREFS_LANGUAGE, ".");
		final String currentLanguage = Locale.getDefault().getLanguage();
		if (DaoMaster.SCHEMA_VERSION != schemaVersion) {
			// Database has to be upgraded.
			prefs.edit().putString(Config.APP_SHARED_PREFS_LANGUAGE, currentLanguage).commit();
			startPopulateDBService(prefs);
		} else if (!language.equals(currentLanguage)) {
			prefs.edit().putString(Config.APP_SHARED_PREFS_LANGUAGE, currentLanguage).commit();
			startPopulateDBService(prefs);
		}
	}

	/**
	 * Starts intent service in case when database has to be upgraded.
	 * 
	 * @param prefs
	 */
	private void startPopulateDBService(final SharedPreferences prefs) {
		prefs.edit().putBoolean(Config.APP_SHARED_PREFS_DATA_PARSING_ONGOING, true).commit();
		Intent serviceIntent = new Intent(getApplicationContext(), PopulateDBIntentService.class);
		startService(serviceIntent);
	}

	private boolean checkPlayServices() {
		final int playServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (playServicesStatus == ConnectionResult.SUCCESS) {
			Log.i(TAG, "Play Services status SUCCESS");
			return true;
		} else {
			Log.w(TAG, "Play Services status ERROR: " + playServicesStatus);
			if (GooglePlayServicesUtil.isUserRecoverableError(playServicesStatus)) {
				Log.i(TAG, "Play Services user recoverable - proceed by calling error dialog");
				DialogFragment dialog = PlayServicesErrorDialogFragment.newInstance(playServicesStatus,
						REQUEST_CODE_RECOVER_PLAY_SERVICES);
				FragmentManager fm = getSupportFragmentManager();
				dialog.show(fm, "play-services-dialog");
			} else {
				Log.w(TAG, "Play Services not user recoverable - finishing app");
				Toast.makeText(getApplicationContext(), R.string.play_services_missing, Toast.LENGTH_SHORT).show();
				finish();
			}

		}
		return false;
	}

	/**
	 * If Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE is false - checks if there is map cache in
	 * Android/data/app_package/cache directory.
	 * 
	 * @param prefs
	 * @return true if Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE is true or there is map cache.
	 */
	private boolean checkIfMapWasCached(final SharedPreferences prefs) {
		// Value will be true only if GoogleMap was initialized successful before;
		boolean deviceWasOnline = prefs.getBoolean(Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE, false);
		if (!deviceWasOnline) {
			// Hackish way to check if map was cached.
			StringBuilder cacheFileNameBuilder = new StringBuilder("cache_vts_").append(Config.APP_PACKAGE)
					.append(".0");
			if (new File(getExternalCacheDir(), cacheFileNameBuilder.toString()).exists()) {
				setDeviceWasOnlineFlag(prefs);
				deviceWasOnline = true;
			}
		}
		return deviceWasOnline;
	}

	/**
	 * Check if device is online(only if connection is available, it not checking for timouts etc.
	 * 
	 * @param prefs
	 * @return true if device is online, false otherwise. If false it also shows dialog asking user to go to settings
	 *         window.
	 */
	private boolean checkInternetConnection(final SharedPreferences prefs) {
		if (!Utils.isOnline(getApplicationContext())) {
			Log.w(TAG, "Device is offline");
			FragmentManager fm = getSupportFragmentManager();
			DialogFragment dialog = NoInternetConnectionDialogFragment.newInstance();
			dialog.show(fm, "no-internet-dialog");
			return false;
		} else {
			setDeviceWasOnlineFlag(prefs);
			return true;
		}
	}

	/**
	 * Sets Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE flag to true;
	 * 
	 * @param prefs
	 */
	private void setDeviceWasOnlineFlag(final SharedPreferences prefs) {
		// Value will be true only if GoogleMap was initialized successful before;
		final boolean deviceWasOnline = prefs.getBoolean(Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE, false);
		if (!deviceWasOnline) {
			prefs.edit().putBoolean(Config.APP_SHADER_PREFS_DEVICE_WAS_ONLINE, true).commit();
		}
	}

	/**
	 * Prepares navigation drawer.
	 */
	private void setUpNavigationDrawer() {
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		// Find navi-drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
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
			if (resultCode == Activity.RESULT_CANCELED) {
				Log.w(TAG, "Google Play Services resolution activity canceled!, finishing app");
				Toast.makeText(getApplicationContext(), R.string.play_services_recovery_canceled, Toast.LENGTH_SHORT)
						.show();
				finish();
			}
			break;
		case REQUEST_CODE_PLACE_DETAILS:
			// Handle case when user returned from details view and changed current place.
			if (resultCode == Activity.RESULT_OK) {
				int placePosition = data.getExtras().getInt(Config.EXTRA_PLACE_POSITION);
				Long placeId = data.getExtras().getLong(Config.EXTRA_PLACE_ID);
				if (placeId != mCurrentPlaceId) {
					if (mMarkers.isEmpty()) {
						// data is not initialized - probably activity recreation on orientation change.
						mCurrentPlaceId = placeId;
					} else {
						// data is initialized - safe to goToMarker.
						goToMarker(placePosition);
					}
				}
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
		outState.putInt(EXTRA_CURRENT_MAP_TYPE, mCurrentMapType);
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
	 * Sets up map properties and map listeners.
	 */
	private void setUpMap() {
		mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(getApplicationContext()));
		mMap.setOnMapClickListener(new MapClickListener());
		mMap.setOnMarkerClickListener(new MarkerClickListener());
		mMap.setOnInfoWindowClickListener(new MarkerInfoWindowClickListener());
		mMap.setOnCameraChangeListener(new MapCameraChangeListener());
		mMap.setMapType(mCurrentMapType);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);
		zoomMapOnStart();
	}

	/**
	 * Restrts or initializes places loader.
	 * 
	 * @param isRestart
	 *            if true loader will be restarted.
	 * @param action
	 *            action constant from PlacesLoader.
	 * @param argument
	 *            search argument for search action or null/empty string for loading all places.
	 */
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

	/**
	 * Zooming map on app start.
	 */
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

	/**
	 * Zooming map to default position where almost all places are visible.
	 */
	private void zoomMapToDefault() {
		if (null != mMap) {
			LatLng latLng = new LatLng(Config.DEFAULT_LAT, Config.DEFAULT_LNG);
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM_LEVEL),
					Config.CAMERA_ANIMATION_DURATION, null);
		}
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
		boolean drawerOpen = mDrawerLayout.isDrawerVisible(mDrawerList);
		menu.findItem(R.id.search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if (mDrawerToggle.onOptionsItemSelected(ABSMenuItemConverter.create(item))) {
			return true;
		} else if (itemId == R.id.about) {
			Intent intent = new Intent(this, AboutAppActivity.class);
			startActivity(intent);
		} else if (itemId == R.id.map_normal) {
			setMapType(GoogleMap.MAP_TYPE_NORMAL);
		} else if (itemId == R.id.map_satellite) {
			setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else if (itemId == R.id.map_hybrid) {
			setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}
		return super.onOptionsItemSelected(item);
	}

	private void setMapType(int mapType) {
		if (null != mMap && mCurrentMapType != mapType) {
			mMap.setMapType(mapType);
			mCurrentMapType = mapType;
		}
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
			switchDrawerItem(mCurrentDrawerItem, false);
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
					switchDrawerItem(mCurrentDrawerItem, false);
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
			clearCurrentMarker();
		} else {
			super.onBackPressed();
		}
	}

	private void clearCurrentMarker() {
		if (mCurrentPlaceId > 0) {
			// If there is selected marker - clear that selection.
			Marker marker = mMarkers.get(mCurrentPlaceId);
			if (null == marker) {
				Log.e(TAG, "Could not clear current marker - marker is null");
				return;
			}
			marker.hideInfoWindow();
			mCurrentPlaceId = Long.MIN_VALUE;
			hideSearchResultsPager();
		}
	}

	/**
	 * Fills up mMarkers, mMarkersData, mVisiblePlaces collections.
	 * 
	 * @param places
	 */
	private void setUpMarkers(List<Place> places) {
		if (null == mMap) {
			Log.e(TAG, "Could not set up markers - GoogleMap is null");
			return;
		}
		mMap.clear();
		mMarkers.clear();
		mMarkersData.clear();
		mVisiblePlaces = new long[places.size()];
		int visiblePlacesIndex = 0;
		for (Place place : places) {
			LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
			Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_small)).title(place.getSymbol())
					.snippet(place.getDescription()));
			mMarkers.put(place.getId(), marker);
			mMarkersData.put(marker, place);
			mVisiblePlaces[visiblePlacesIndex] = place.getId();
			++visiblePlacesIndex;
		}
	}

	/**
	 * Move map camera to selected marker if animation is needed. Show marker infoWindow.
	 * 
	 * @param marker
	 */
	private void goToMarker(final Marker marker) {
		if (null == marker) {
			Log.e(TAG, "Cannot go to NULL marker");
			return;
		}
		if (mMap.getCameraPosition().target.equals(marker.getPosition())) {
			handleMarker(marker);
			marker.showInfoWindow();
		} else {
			mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), Config.CAMERA_ANIMATION_DURATION,
					new MapCameraAnimationCalback(marker));
		}
	}

	/**
	 * Go to marker at given position on search results adapter.
	 * 
	 * @param position
	 *            in search results adapter.
	 */
	private void goToMarker(final int position) {
		Long placeId = mSearchResultAdapter.getItemId(position);
		if (placeId != mCurrentPlaceId) {
			Marker marker = mMarkers.get(placeId);
			goToMarker(marker);
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
			mViewPager.setVisibility(View.GONE);
			mViewPager.startAnimation(mSearchResultsPagerHideAnim);
			mMap.setPadding(0, 0, 0, 0);
		}
	}

	@Override
	public void onSearchResultClick(Long placeId) {
		Intent intent = new Intent(this, PlaceDetailsActivity.class);
		Bundle extras = new Bundle();
		extras.putLongArray(Config.EXTRA_VISIBLE_PLACES, mVisiblePlaces);
		extras.putInt(Config.EXTRA_PLACE_POSITION, mViewPager.getCurrentItem());
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_CODE_PLACE_DETAILS);
	}

	/**
	 * Selects/deselect item on navigation drawer and restarting loader if needed.
	 * 
	 * @param position
	 */
	private void selectDrawerItem(final int position) {
		mDrawerLayout.closeDrawer(mDrawerList);
		mCurrentPlaceId = Long.MIN_VALUE;
		final NavigationDrawerItem item = Config.NAVIGATION_DRAWER_ITEMS[position];
		if (position == mCurrentDrawerItem) {
			switchDrawerItem(mCurrentDrawerItem, false);
			// Delay restart loader to prevent Drawer animation lag
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					initLoader(true, PlacesLoader.LOAD_ALL_PLACES, "");
				}
			}, Config.DRAWER_RESTART_LOADER_DELAY);
		} else {
			switchDrawerItem(position, true);
			// Delay restart loader to prevent Drawer animation lag
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					initLoader(true, item.action, item.argument);
				}
			}, Config.DRAWER_RESTART_LOADER_DELAY);
		}
	}

	/**
	 * Changes single navigation drawer item selection state.
	 * 
	 * @param drawerItemPosition
	 * @param isCheck
	 */
	private void switchDrawerItem(int drawerItemPosition, boolean isCheck) {
		mDrawerList.setItemChecked(drawerItemPosition, isCheck);
		if (isCheck) {
			mCurrentDrawerItem = drawerItemPosition;
		} else {
			mCurrentDrawerItem = Integer.MIN_VALUE;
		}
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
			int action = data.action;
			if (PlacesLoader.LOAD_ALL_PLACES == action) {
				handleLoaderResult(data);
			} else if (PlacesLoader.LOAD_PLACES_BY_SEARCH == action) {
				handleLoaderResult(data);
				if (data.places.size() > 0) {
					if (mCurrentPlaceId < 0) {
						mCurrentPlaceId = data.places.get(0).getId();
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

	/**
	 * Handles data loaded by loader - setting up markers and ViewPager.
	 * 
	 * @param data
	 */
	private void handleLoaderResult(PlacesList data) {
		setUpMarkers(data.places);
		mSearchResultAdapter = new SearchResultFragmentAdapter(getSupportFragmentManager(), data.places);
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
	 * Used to change markers icons on small zoom.
	 */
	private class MapCameraChangeListener implements GoogleMap.OnCameraChangeListener {
		private boolean mIsMarkerSmall = true;

		@Override
		public void onCameraChange(CameraPosition position) {
			final float zoom = position.zoom;
			if (zoom <= Config.DEFAULT_ZOOM_LEVEL && !mIsMarkerSmall) {
				if (Config.DEBUG) {
					Log.d(TAG, "Change markers to small");
				}
				for (Map.Entry<Long, Marker> entry : mMarkers.entrySet()) {
					entry.getValue().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_small));
					mIsMarkerSmall = true;
				}
				showMarkerInfoWindow();
			} else if (zoom > Config.DEFAULT_ZOOM_LEVEL && mIsMarkerSmall) {
				if (Config.DEBUG) {
					Log.d(TAG, "Change markers to default");
				}
				for (Map.Entry<Long, Marker> entry : mMarkers.entrySet()) {
					entry.getValue().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_default));
				}
				mIsMarkerSmall = false;
				showMarkerInfoWindow();
			}

		}

		private void showMarkerInfoWindow() {
			if (mCurrentPlaceId > 0) {
				final Marker marker = mMarkers.get(mCurrentPlaceId);
				if (null != marker) {
					marker.showInfoWindow();
				}
			}
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
			goToMarker(position);
		}
	}

	/**
	 * Perform onSearchResultClick action when marker InfoWindow is clicked.
	 * 
	 * @author Lecho
	 * 
	 */
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
			if (mDrawerLayout.isDrawerVisible(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			}
			return true;
		}

		@Override
		public boolean onMenuItemActionCollapse(MenuItem item) {
			// If user collapse search view *after search was performed* return to all places.
			if (mCurrentLoaderAction == PlacesLoader.LOAD_PLACES_BY_SEARCH) {
				mCurrentPlaceId = Long.MIN_VALUE;
				initLoader(true, PlacesLoader.LOAD_ALL_PLACES, "");
			} else if (mCurrentPlaceId > 0) {
				clearCurrentMarker();
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

	/**
	 * Toggle for navigation drawer.
	 * 
	 * @author Lecho
	 * 
	 */
	private class DrawerToggle extends ActionBarDrawerToggle {

		public DrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes,
				int openDrawerContentDescRes, int closeDrawerContentDescRes) {
			super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
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
