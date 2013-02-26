package lecho.app.campus.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * @author lecho
 * 
 */
public abstract class PlaceFaculty implements BaseColumns {

	public static final String TABLE_NAME = "place_faculty";
	public static final String PLACE_ID = "place_id";
	public static final String FACULTY_ID = "faculty_id";

	// table DLL
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement, " + PLACE_ID + " integer, " + FACULTY_ID + " integer);";

	// provider relative stuff
	public static final String AUTHORITY = "lecho.app.campus.provider.PlaceFacultyContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

}
