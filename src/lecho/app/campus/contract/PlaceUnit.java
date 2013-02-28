package lecho.app.campus.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * @author lecho
 * 
 */
public abstract class PlaceUnit implements BaseColumns {

    public static final String TABLE_NAME = "place_unit";
    public static final String PLACE_ID = "place_id";
    public static final String UNIT_ID = "unit_id";

    // table DDL
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " integer primary key autoincrement, " + PLACE_ID + " integer, " + UNIT_ID + " integer);";

    // provider relative stuff
    public static final String AUTHORITY = "lecho.app.campus.content.PlaceUnitProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
}
