package lecho.app.campus.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * For example administration, sport, faculty etc
 * 
 * @author lecho
 * 
 */
public abstract class Category implements BaseColumns {

    public static final String TABLE_NAME = "category";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    // table DLL
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " integer primary key, "
            + NAME + " text, " + DESCRIPTION + " text);";

    // provider relative stuff
    public static final String AUTHORITY = "lecho.app.campus.content.CategoryProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

}
