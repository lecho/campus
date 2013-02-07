package lecho.app.campus.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.content.Category;
import lecho.app.campus.content.Faculty;
import lecho.app.campus.content.Place;
import lecho.app.campus.content.PlaceCategory;
import lecho.app.campus.content.PlaceFaculty;
import lecho.app.campus.content.PlaceUnit;
import lecho.app.campus.content.Unit;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * 
 * @author lecho
 * 
 */
public class XmlParser {
	public static final String TAG = XmlParser.class.getSimpleName();

	/**
	 * Parsing campus_data_xx.xml file from raw resources.
	 * 
	 * @param ctx
	 * @return true if success, false otherwise
	 */
	public static boolean loadCampusData(Context ctx) {
		try {
			Log.i(TAG, "Loading data from xml");
			InputStream in;
			in = ctx.getResources().openRawResource(R.raw.campus_data_pl);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(in, "UTF-8");
			int eventType = xpp.getEventType();
			List<ContentValues> places = new ArrayList<ContentValues>();
			List<ContentValues> categories = new ArrayList<ContentValues>();
			List<ContentValues> units = new ArrayList<ContentValues>();
			List<ContentValues> faculties = new ArrayList<ContentValues>();
			List<ContentValues> placesCategories = new ArrayList<ContentValues>();
			List<ContentValues> placesUnits = new ArrayList<ContentValues>();
			List<ContentValues> placesFaculties = new ArrayList<ContentValues>();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (Place.TABLE_NAME.equals(xpp.getName())) {
						places.add(parsePlace(xpp));
					} else if (Faculty.TABLE_NAME.equals(xpp.getName())) {
						faculties.add(parseFaculty(xpp));
					} else if (Unit.TABLE_NAME.equals(xpp.getName())) {
						units.add(parseUnit(xpp));
					} else if (Category.TABLE_NAME.equals(xpp.getName())) {
						categories.add(parseCategory(xpp));
					} else if (PlaceFaculty.TABLE_NAME.equals(xpp.getName())) {
						placesFaculties.add(parsePlaceFaculty(xpp));
					} else if (PlaceUnit.TABLE_NAME.equals(xpp.getName())) {
						placesUnits.add(parsePlaceUnit(xpp));
					} else if (PlaceCategory.TABLE_NAME.equals(xpp.getName())) {
						placesCategories.add(parsePlaceCategory(xpp));
					}
				}
				eventType = xpp.next();
			}

			ctx.getContentResolver().bulkInsert(Place.CONTENT_URI, places.toArray(new ContentValues[places.size()]));
			ctx.getContentResolver().bulkInsert(Category.CONTENT_URI,
					categories.toArray(new ContentValues[categories.size()]));
			ctx.getContentResolver().bulkInsert(Faculty.CONTENT_URI,
					faculties.toArray(new ContentValues[faculties.size()]));
			ctx.getContentResolver().bulkInsert(Unit.CONTENT_URI, units.toArray(new ContentValues[units.size()]));
			ctx.getContentResolver().bulkInsert(PlaceCategory.CONTENT_URI,
					placesCategories.toArray(new ContentValues[placesCategories.size()]));
			ctx.getContentResolver().bulkInsert(PlaceFaculty.CONTENT_URI,
					placesFaculties.toArray(new ContentValues[placesFaculties.size()]));
			ctx.getContentResolver().bulkInsert(PlaceUnit.CONTENT_URI,
					placesUnits.toArray(new ContentValues[placesUnits.size()]));
		} catch (Exception e) {
			Log.e(TAG, "Could not parse data file", e);
		}
		return false;
	}

	private static ContentValues parsePlace(XmlPullParser xpp) throws XmlPullParserException, IOException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (Place._ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place._ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (Place.NAME.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.NAME, xpp.getText().trim());
					}
				} else if (Place.SYMBOL.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.SYMBOL, xpp.getText().trim());
					}
				} else if (Place.WEB_PAGE.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.WEB_PAGE, xpp.getText().trim());
					}
				} else if (Place.DESCRIPTION.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.DESCRIPTION, xpp.getText().trim());
					}
				} else if (Place.KEYWORDS.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.KEYWORDS, xpp.getText().trim());
					}
				} else if (Place.ADDRESS.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.ADDRESS, xpp.getText().trim());
					}
				} else if (Place.LATITUDE.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.LATITUDE, Double.parseDouble(xpp.getText().trim()));
					}
				} else if (Place.LONGTITUDE.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Place.LONGTITUDE, Double.parseDouble(xpp.getText().trim()));
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (Place.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}

	private static ContentValues parseCategory(XmlPullParser xpp) throws IOException, XmlPullParserException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (Category._ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Category._ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (Category.NAME.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Category.NAME, xpp.getText().trim());
					}
				} else if (Category.DESCRIPTION.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Category.DESCRIPTION, xpp.getText().trim());
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (Category.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}

	private static ContentValues parseFaculty(XmlPullParser xpp) throws XmlPullParserException, IOException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (Faculty._ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Faculty._ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (Faculty.NAME.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Faculty.NAME, xpp.getText().trim());
					}
				} else if (Faculty.SHORT_NAME.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Faculty.SHORT_NAME, xpp.getText().trim());
					}
				} else if (Faculty.WEB_PAGE.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Faculty.WEB_PAGE, xpp.getText().trim());
					}
				} else if (Faculty.DESCRIPTION.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Faculty.DESCRIPTION, xpp.getText().trim());
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (Faculty.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}

	private static ContentValues parseUnit(XmlPullParser xpp) throws XmlPullParserException, IOException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (Unit._ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Unit._ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (Unit.NAME.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Unit.NAME, xpp.getText().trim());
					}
				} else if (Unit.SHORT_NAME.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Unit.SHORT_NAME, xpp.getText().trim());
					}
				} else if (Unit.WEB_PAGE.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Unit.WEB_PAGE, xpp.getText().trim());
					}
				} else if (Unit.DESCRIPTION.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Unit.DESCRIPTION, xpp.getText().trim());
					}
				} else if (Unit.FACULTY_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(Unit.FACULTY_ID, Long.valueOf(xpp.getText().trim()));
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (Unit.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}

	private static ContentValues parsePlaceCategory(XmlPullParser xpp) throws XmlPullParserException, IOException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (PlaceCategory.PLACE_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(PlaceCategory.PLACE_ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (PlaceCategory.CATEGORY_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(PlaceCategory.CATEGORY_ID, Long.valueOf(xpp.getText().trim()));
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (PlaceCategory.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}

	private static ContentValues parsePlaceUnit(XmlPullParser xpp) throws NumberFormatException,
			XmlPullParserException, IOException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (PlaceUnit.PLACE_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(PlaceUnit.PLACE_ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (PlaceUnit.UNIT_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(PlaceUnit.UNIT_ID, Long.valueOf(xpp.getText().trim()));
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (PlaceUnit.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}

	private static ContentValues parsePlaceFaculty(XmlPullParser xpp) throws NumberFormatException,
			XmlPullParserException, IOException {
		ContentValues cv = new ContentValues();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (PlaceFaculty.PLACE_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(PlaceFaculty.PLACE_ID, Long.valueOf(xpp.getText().trim()));
					}
				} else if (PlaceFaculty.FACULTY_ID.equals(xpp.getName())) {
					if (xpp.nextToken() == XmlPullParser.TEXT) {
						cv.put(PlaceFaculty.FACULTY_ID, Long.valueOf(xpp.getText().trim()));
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (PlaceFaculty.TABLE_NAME.equals(xpp.getName())) {
					return cv;
				}
			}
			eventType = xpp.next();
		}
		throw new IllegalStateException("Invalid xml file format");
	}
}
