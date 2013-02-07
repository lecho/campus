package lecho.app.campus.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Some location on map(POI)
 * 
 * @author lecho
 * 
 */
public abstract class Place implements BaseColumns {

	public static final String TABLE_NAME = "place";
	public static final String NAME = "name";
	public static final String SYMBOL = "symbol";
	public static final String WEB_PAGE = "web_page";
	public static final String DESCRIPTION = "description";
	public static final String KEYWORDS = "keywords";
	public static final String LATITUDE = "latitude";
	public static final String LONGTITUDE = "longtitude";
	public static final String ADDRESS = "address";

	// table DLL
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement, " + NAME + " text, " + SYMBOL + " varchar(16), " + WEB_PAGE
			+ " text, " + DESCRIPTION + " text, " + KEYWORDS + " text, " + LATITUDE + " real, " + LONGTITUDE
			+ " real, " + ADDRESS + " text);";

	// provider relative stuff
	public static final String AUTHORITY = "lecho.app.campus.provider.PlaceContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

}
