package lecho.app.campus.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * @author lecho
 * 
 */
public abstract class Faculty implements BaseColumns {

	public static final String TABLE_NAME = "faculty";
	public static final String NAME = "name";
	public static final String SHORT_NAME = "short_name";
	public static final String WEB_PAGE = "web_page";
	public static final String DESCRIPTION = "description";

	// table DLL
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " integer primary key, "
			+ NAME + " text, " + SHORT_NAME + " varchar(16), " + WEB_PAGE + " text, " + DESCRIPTION + " text);";

	// provider relative stuff
	public static final String AUTHORITY = "lecho.app.campus.provider.FacultyContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

}
