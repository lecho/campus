package lecho.app.campus.provider;

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
		// db.execSQL(TABLE.CREATE_TABLE);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		// db.execSQL(DROP_TABLE + TABLENAME);

		onCreate(db);
	}

}
