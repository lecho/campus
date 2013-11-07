package lecho.app.campus.utils;

import lecho.app.campus.R;
import lecho.app.campus.loader.PlacesLoader;

/**
 * App configuration.
 * 
 * @author Lecho
 * 
 */
public abstract class Config {
	public static final boolean DEBUG = false;

	// Increment to reload data after app update, should be always greater than 0
	public static final String APP_SHARED_PREFS_NAME = "lecho.app.campus:SHARED_PREFS";
	public static final String APP_SHADER_PREFS_DEVICE_WAS_ONLINE = "lecho.app.campus:DEVICE_WAS_ONLINE";
	public static final String APP_SHARED_PREFS_SCHEMA_VERSION = "lecho.app.campus:CAMPUS_DATA_VERSION";
	public static final String APP_SHARED_PREFS_DATA_PARSING_ONGOING = "lecho.app.campus:DATA_PARSING_ONGOING";
	public static final String APP_SHARED_PREFS_LANGUAGE = "lecho.app.campus:LANGUAGE";

	public static final String APP_ASSETS_DIR = "campus";
	public static final String APP_TARGET = "";
	public static final String APP_PACKAGE = "lecho.app.campus" + APP_TARGET;

	public static final String EXTRA_PLACE_ID = "lecho.app.campus:ARG_PLACE_ID";
	public static final String EXTRA_PLACE_SYMBOL = "lecho.app.campus:ARG_PLACE_SYMBOL";
	public static final String EXTRA_VISIBLE_PLACES = "lecho.app.campus:VISIBLE_PLACES";
	public static final String EXTRA_PLACE_POSITION = "lecho.app.campus:PLACE_POSITION";
	public static final String EXTRA_LANGUAGE_CHANGED = "lecho.app.campus:LANGUAGE_CHANGED";
	public static final String PLACE_MAIN_PHOTO_NAME = "1.jpg";

	public static final String SEARCH_SUGGESTION_AUTHORITY = "lecho.app.campus" + APP_TARGET
			+ ".provider.SearchSuggestionProvider";

	public static final double DEFAULT_LAT = 51.7505298;
	public static final double DEFAULT_LNG = 19.4551516;
	public static final float DEFAULT_ZOOM_LEVEL = 14.5f;

	public static final int CAMERA_ANIMATION_DURATION = 500;
	public static final int DRAWER_RESTART_LOADER_DELAY = 250;

	public static final NavigationDrawerItem[] NAVIGATION_DRAWER_ITEMS = new NavigationDrawerItem[] {
			new NavigationDrawerItem(R.string.drawer_title_faculties, 0, null, NavigationDrawerItem.TYPE_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_mech, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W1",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_eeia, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W2",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_chem, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W3",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_tmwt, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W4",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_binoz, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W5",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_bais, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W6",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_ftims, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W7",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_oiz, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W9",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_ipos, PlacesLoader.LOAD_PLACES_BY_FACULTY, "W10",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_title_campuses, 0, null, NavigationDrawerItem.TYPE_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_a, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "CAMPUS_A",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_b, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "CAMPUS_B",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_c, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "CAMPUS_C",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_d, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "CAMPUS_D",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_title_buildings, 0, null, NavigationDrawerItem.TYPE_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_dormitory, PlacesLoader.LOAD_PLACES_BY_CATEGORY,
					"DORMITORY", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_administration,
					PlacesLoader.LOAD_PLACES_BY_CATEGORY, "ADMINISTRATION", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_sport, PlacesLoader.LOAD_PLACES_BY_CATEGORY,
					"SPORT", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE), };

}
