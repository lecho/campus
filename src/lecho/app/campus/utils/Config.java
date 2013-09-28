package lecho.app.campus.utils;

import lecho.app.campus.R;
import lecho.app.campus.loader.PlacesLoader;

public abstract class Config {
	public static final int CAMPUS_DATA_VERSION = 1;
	public static final String APP_SHARED_PREFS_NAME = "lecho.app.campus:SHARED_PREFS";
	public static final String APP_SHARED_PREFS_APP_WAS_STARTED = "lecho.app.campus:APP_WAS_STARTED";
	public static final String APP_SHADER_PREFS_DEVICE_WAS_ONLINE = "lecho.app.campus:DEVICE_WAS_ONLINE";
	public static final String APP_SHARED_PREFS_CAMPUS_DATA_VERSION = "lecho.app.campus:CAMPUS_DATA_VERSION";
	public static final String APP_SHARED_PREFS_DATA_PARSING_ONGOING = "lecho.app.campus:DATA_PARSING_ONGOING";

	public static final String APP_ASSETS_DIR = "campus";
	public static final String APP_TARGET = "";
	public static final String APP_PACKAGE = "lecho.app.campus" + APP_TARGET;

	public static final String EXTRA_PLACE_ID = "lecho.app.campus:ARG_PLACE_ID";
	public static final String EXTRA_PLACE_SYMBOL = "lecho.app.campus:ARG_PLACE_SYMBOL";
	public static final String PLACE_MAIN_PHOTO_NAME = "main.jpg";

	public static final String SEARCH_SUGGESTION_AUTHORITY = "lecho.app.campus." + APP_TARGET
			+ ".provider.SearchSuggestionProvider";

	public static final double START_LAT1 = 51.740000;
	public static final double START_LNG1 = 19.440000;
	public static final double START_LAT2 = 51.770000;
	public static final double START_LNG2 = 19.480000;

	// TODO Get this configuration from XML/database
	public static final NavigationDrawerItem[] NAVIGATION_DRAWER_ITEMS = new NavigationDrawerItem[] {
			new NavigationDrawerItem(R.string.drawer_title_campuses, 0, null, NavigationDrawerItem.TYPE_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_a, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "A",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_b, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "B",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_c, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "C",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_campus_d, PlacesLoader.LOAD_PLACES_BY_CATEGORY, "D",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_title_faculties, 0, null, NavigationDrawerItem.TYPE_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_eeia, PlacesLoader.LOAD_PLACES_BY_FACULTY, "EEIA",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_ftims, PlacesLoader.LOAD_PLACES_BY_FACULTY, "FTIMS",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_faculty_oiz, PlacesLoader.LOAD_PLACES_BY_FACULTY, "OIZ",
					NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_title_buildings, 0, null, NavigationDrawerItem.TYPE_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_dormitory, PlacesLoader.LOAD_PLACES_BY_CATEGORY,
					"DORMITORY", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_educational, PlacesLoader.LOAD_PLACES_BY_CATEGORY,
					"EDUCATIONAL", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_administration,
					PlacesLoader.LOAD_PLACES_BY_CATEGORY, "ADMINISTRATION", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_sport, PlacesLoader.LOAD_PLACES_BY_CATEGORY,
					"SPORT", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE),
			new NavigationDrawerItem(R.string.drawer_item_building_other, PlacesLoader.LOAD_PLACES_BY_CATEGORY,
					"OTHER", NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE), };

}
