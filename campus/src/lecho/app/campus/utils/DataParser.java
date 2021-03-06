package lecho.app.campus.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.app.campus.dao.Category;
import lecho.app.campus.dao.CategoryDao;
import lecho.app.campus.dao.DaoSession;
import lecho.app.campus.dao.Faculty;
import lecho.app.campus.dao.FacultyDao;
import lecho.app.campus.dao.Place;
import lecho.app.campus.dao.PlaceCategory;
import lecho.app.campus.dao.PlaceCategoryDao;
import lecho.app.campus.dao.PlaceDao;
import lecho.app.campus.dao.PlaceFaculty;
import lecho.app.campus.dao.PlaceFacultyDao;
import lecho.app.campus.dao.PlaceUnit;
import lecho.app.campus.dao.PlaceUnitDao;
import lecho.app.campus.dao.Unit;
import lecho.app.campus.dao.UnitDao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Parses data and inserts it into database in single transaction.
 * 
 * @author lecho
 * 
 */
public class DataParser {
	public static final String TAG = "DataParser";

	// These constants must correspond to xml tags.
	private static final String SEPARATOR = ",";
	private static final String ID = "id";
	private static final String PLACE = "place";
	private static final String FACULTY = "faculty";
	private static final String UNIT = "unit";
	private static final String CATEGORY = "category";
	private static final String NAME = "name";
	private static final String SHORT_NAME = "short_name";
	private static final String FILTERABLE_NAME = "filterable_name";
	private static final String DESCRIPTION = "description";
	private static final String SYMBOL = "symbol";
	private static final String LATITUDE = "latitude";
	private static final String LONGTITUDE = "longitude";
	private static final String PLACE_CATEGORY = "place_category";
	private static final String PLACE_FACULTY = "place_faculty";
	private static final String PLACE_UNIT = "place_unit";
	private static final String UNIT_FACULTY = "unit_faculty";
	private static final String HAS_IMAGE = "has_image";

	/**
	 * Parses campus_data_xx.xml file from raw resources.
	 * 
	 */
	public static boolean loadCampusData(Context context, int rawResource) {
		Log.i(TAG, "Loading data from xml");
		final long time = System.nanoTime();
		// lists with entities to persist.
		final List<Place> places = new ArrayList<Place>();
		final List<Category> categories = new ArrayList<Category>();
		final List<Faculty> faculties = new ArrayList<Faculty>();
		final List<Unit> units = new ArrayList<Unit>();
		final List<PlaceCategory> placeCategories = new ArrayList<PlaceCategory>();
		final List<PlaceFaculty> placeFaculties = new ArrayList<PlaceFaculty>();
		final List<PlaceUnit> placeUnits = new ArrayList<PlaceUnit>();

		BufferedInputStream bufferedInput = null;
		try {
			InputStream in = context.getResources().openRawResource(rawResource);
			bufferedInput = new BufferedInputStream(in);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(bufferedInput, "UTF-8");
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (CATEGORY.equals(xpp.getName())) {
						parseCategory(xpp, categories);
					} else if (FACULTY.equals(xpp.getName())) {
						parseFaculty(xpp, faculties);
					} else if (UNIT.equals(xpp.getName())) {
						parseUnit(xpp, units);
					} else if (PLACE.equals(xpp.getName())) {
						parsePlace(xpp, places, placeCategories, placeFaculties, placeUnits);
					}
				}
				eventType = xpp.next();
			}
			// Insert data into database in single transaction.
			final DaoSession daoSession = DatabaseHelper.getDaoSession(context);
			final CategoryDao categoryDao = daoSession.getCategoryDao();
			final FacultyDao facultyDao = daoSession.getFacultyDao();
			final UnitDao unitDao = daoSession.getUnitDao();
			final PlaceDao placeDao = daoSession.getPlaceDao();
			final PlaceCategoryDao placeCategoryDao = daoSession.getPlaceCategoryDao();
			final PlaceFacultyDao placeFacultyDao = daoSession.getPlaceFacultyDao();
			final PlaceUnitDao placeUnitDao = daoSession.getPlaceUnitDao();
			daoSession.runInTx(new Runnable() {

				@Override
				public void run() {
					for (Category category : categories) {
						categoryDao.insert(category);
					}
					for (Faculty faculty : faculties) {
						facultyDao.insert(faculty);
					}
					for (Unit unit : units) {
						unitDao.insert(unit);
					}
					for (Place place : places) {
						placeDao.insert(place);
					}
					for (PlaceCategory placeCategory : placeCategories) {
						placeCategoryDao.insert(placeCategory);
					}
					for (PlaceFaculty placeFaculty : placeFaculties) {
						placeFacultyDao.insert(placeFaculty);
					}
					for (PlaceUnit placeUnit : placeUnits) {
						placeUnitDao.insert(placeUnit);
					}
				}
			});
			long elapsedTime = (System.nanoTime() - time) / 1000000L;
			Log.i(TAG, "Success, data loaded from xml in[miliseconds]: " + elapsedTime);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "Could not parse xml file.", e);
		} finally {
			if (null != bufferedInput) {
				try {
					bufferedInput.close();
				} catch (IOException e) {
					Log.e(TAG, "Could not close stream", e);
				}
			}
		}
		return false;
	}

	private static void parseCategory(XmlPullParser xpp, List<Category> categories) throws XmlPullParserException,
			IOException {
		Category category = new Category();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Parse CATEGORY attributes
			if (eventType == XmlPullParser.START_TAG) {
				if (ID.equals(xpp.getName())) {
					category.setId(Long.valueOf(xpp.nextText().trim()));
				} else if (NAME.equals(xpp.getName())) {
					category.setName(xpp.nextText().trim());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (CATEGORY.equals(xpp.getName())) {
					// This should be end of CATEGORY definition
					categories.add(category);
					return;
				}
			}
			eventType = xpp.next();
		}
		// Parser go through whole document and didn't find closing PLACE tag,
		// something's wrong with the xml file.
		throw new IllegalStateException("Invalid xml file format while parsint CATEGORY");
	}

	private static void parseFaculty(XmlPullParser xpp, List<Faculty> faculties) throws XmlPullParserException,
			IOException {
		Faculty faculty = new Faculty();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Parse FACULTY attributes
			if (eventType == XmlPullParser.START_TAG) {
				if (ID.equals(xpp.getName())) {
					faculty.setId(Long.valueOf(xpp.nextText().trim()));
				} else if (NAME.equals(xpp.getName())) {
					faculty.setName(xpp.nextText().trim());
				} else if (SHORT_NAME.equals(xpp.getName())) {
					faculty.setShortName(xpp.nextText().trim());
				} else if (FILTERABLE_NAME.equals(xpp.getName())) {
					faculty.setFilterableName(xpp.nextText().trim());
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (FACULTY.equals(xpp.getName())) {
					// This should be end of FACULTY definition
					faculties.add(faculty);
					return;
				}
			}
			eventType = xpp.next();
		}
		// Parser go through whole document and didn't find closing PLACE tag,
		// something's wrong with the xml file.
		throw new IllegalStateException("Invalid xml file format");
	}

	private static void parseUnit(XmlPullParser xpp, List<Unit> units) throws XmlPullParserException, IOException {
		Unit unit = new Unit();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Parse UNIT attributes
			if (eventType == XmlPullParser.START_TAG) {
				if (ID.equals(xpp.getName())) {
					unit.setId(Long.valueOf(xpp.nextText().trim()));
				} else if (NAME.equals(xpp.getName())) {
					unit.setName(xpp.nextText().trim());
				} else if (SHORT_NAME.equals(xpp.getName())) {
					unit.setShortName(xpp.nextText().trim());
				} else if (UNIT_FACULTY.equals(xpp.getName())) {
					String facultyId = xpp.nextText().trim();
					if (!TextUtils.isEmpty(facultyId)) {
						unit.setFacultyId(Long.valueOf(facultyId));
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (UNIT.equals(xpp.getName())) {
					// This should be end of UNIT definition
					units.add(unit);
					return;
				}
			}
			eventType = xpp.next();
		}
		// Parser go through whole document and didn't find closing PLACE tag,
		// something's wrong with the xml file.
		throw new IllegalStateException("Invalid xml file format");
	}

	private static void parsePlace(XmlPullParser xpp, List<Place> places, List<PlaceCategory> placeCategories,
			List<PlaceFaculty> placeFaculties, List<PlaceUnit> placeUnits) throws XmlPullParserException, IOException {
		Place place = new Place();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			// Parse PLACE attributes
			if (eventType == XmlPullParser.START_TAG) {
				if (ID.equals(xpp.getName())) {
					place.setId(Long.valueOf(xpp.nextText().trim()));
				} else if (NAME.equals(xpp.getName())) {
					place.setName(xpp.nextText().trim());
				} else if (SYMBOL.equals(xpp.getName())) {
					place.setSymbol(xpp.nextText().trim().toUpperCase(Locale.UK));
				} else if (DESCRIPTION.equals(xpp.getName())) {
					place.setDescription(xpp.nextText().trim());
				} else if (LATITUDE.equals(xpp.getName())) {
					place.setLatitude(Double.valueOf(xpp.nextText().trim()));
				} else if (LONGTITUDE.equals(xpp.getName())) {
					place.setLongitude(Double.valueOf(xpp.nextText().trim()));
				} else if (HAS_IMAGE.equals(xpp.getName())) {
					place.setHasImage(1 == Long.valueOf(xpp.nextText().trim()));
				} else if (PLACE_CATEGORY.equals(xpp.getName())) {
					if (null == place) {
						throw new IllegalStateException(
								"Invalid xml file format. Place cannot be null while parsing CATEGORY relation.");
					}
					String categories = xpp.nextText().trim();
					if (!TextUtils.isEmpty(categories)) {
						String[] categoriesArray = categories.split(SEPARATOR);
						for (String categoryId : categoriesArray) {
							placeCategories.add(new PlaceCategory(null, Long.valueOf(categoryId), place.getId()));
						}
					}
				} else if (PLACE_UNIT.equals(xpp.getName())) {
					if (null == place) {
						throw new IllegalStateException(
								"Invalid xml file format. Place cannot be null while parsing UNIT relation.");
					}
					String units = xpp.nextText().trim();
					if (!TextUtils.isEmpty(units)) {
						String[] unitsArray = units.split(SEPARATOR);
						for (String unitId : unitsArray) {
							placeUnits.add(new PlaceUnit(null, Long.valueOf(unitId), place.getId()));
						}
					}
				} else if (PLACE_FACULTY.equals(xpp.getName())) {
					if (null == place) {
						throw new IllegalStateException(
								"Invalid xml file format. Place cannot be null while parsing FACULTY relation.");
					}
					String faculties = xpp.nextText().trim();
					if (!TextUtils.isEmpty(faculties)) {
						String[] facultiesArray = faculties.split(SEPARATOR);
						for (String facultyId : facultiesArray) {
							placeFaculties.add(new PlaceFaculty(null, Long.valueOf(facultyId), place.getId()));
						}
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (PLACE.equals(xpp.getName())) {
					// This should be end of PLACE definition
					places.add(place);
					return;
				}
			}
			eventType = xpp.next();
		}
		// Parser go through whole document and didn't find closing PLACE tag,
		// something's wrong with the xml file.
		throw new IllegalStateException("Invalid xml file format while parsing PLACE");
	}

}
