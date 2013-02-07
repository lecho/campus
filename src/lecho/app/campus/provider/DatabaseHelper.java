package lecho.app.campus.provider;

import lecho.app.campus.content.Category;
import lecho.app.campus.content.Faculty;
import lecho.app.campus.content.Place;
import lecho.app.campus.content.PlaceCategory;
import lecho.app.campus.content.PlaceFaculty;
import lecho.app.campus.content.PlaceUnit;
import lecho.app.campus.content.Unit;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author lecho
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();

	public static final String DATABASE_NAME = "campus_database";
	private static final int DATABASE_VERSION = 1;
	private static final String DROP_TABLE = "drop table if exists ";

	private static DatabaseHelper dbHelper;

	/**
	 * {@inheritDoc}
	 */
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseHelper getInstance(Context context) {
		if (null == dbHelper) {
			dbHelper = new DatabaseHelper(context);
		}
		return dbHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Place.CREATE_TABLE);
		db.execSQL(Unit.CREATE_TABLE);
		db.execSQL(Faculty.CREATE_TABLE);
		db.execSQL(Category.CREATE_TABLE);
		db.execSQL(PlaceUnit.CREATE_TABLE);
		db.execSQL(PlaceFaculty.CREATE_TABLE);
		db.execSQL(PlaceCategory.CREATE_TABLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL(DROP_TABLE + Place.TABLE_NAME);
		db.execSQL(DROP_TABLE + Unit.TABLE_NAME);
		db.execSQL(DROP_TABLE + Faculty.TABLE_NAME);
		db.execSQL(DROP_TABLE + Category.TABLE_NAME);
		db.execSQL(DROP_TABLE + PlaceUnit.TABLE_NAME);
		db.execSQL(DROP_TABLE + PlaceFaculty.TABLE_NAME);
		db.execSQL(DROP_TABLE + PlaceCategory.TABLE_NAME);

		onCreate(db);
	}

}
