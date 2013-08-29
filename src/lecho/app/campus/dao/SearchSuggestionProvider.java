package lecho.app.campus.dao;

import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DatabaseHelper;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * 
 * @author lecho
 * 
 */
public class SearchSuggestionProvider extends ContentProvider {
	public static final String DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lecho.app.campus";
	public static final int CAMPUS_MAP_DIR = 1;
	private static final String MAX_SUGGESTIONS = "10";

	// Search for places by symbol, name or description.
	private static final String QUERY_SEARCH_BY_PLACE = "select P." + PlaceDao.Properties.Id.columnName + ", P."
			+ PlaceDao.Properties.Symbol.columnName + ", P." + PlaceDao.Properties.Name.columnName + " as "
			+ SearchManager.SUGGEST_COLUMN_TEXT_1 + ", P." + PlaceDao.Properties.Description.columnName + " as "
			+ SearchManager.SUGGEST_COLUMN_TEXT_2 + ", P." + PlaceDao.Properties.Id.columnName + " as "
			+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + " from " + PlaceDao.TABLENAME + " P where P."
			+ PlaceDao.Properties.Symbol.columnName + " like ? or P." + PlaceDao.Properties.Name.columnName
			+ " like ? or P." + PlaceDao.Properties.Description.columnName + " like ?";

	// Search for place by units names and short names.
	private static final String QUERY_SEARCH_BY_UNIT = "select P." + PlaceDao.Properties.Id.columnName + ", P."
			+ PlaceDao.Properties.Symbol.columnName + ", U." + UnitDao.Properties.Name.columnName + " as "
			+ SearchManager.SUGGEST_COLUMN_TEXT_1 + ", F." + FacultyDao.Properties.ShortName.columnName + " as "
			+ SearchManager.SUGGEST_COLUMN_TEXT_2 + ", P." + PlaceDao.Properties.Id.columnName + " as "
			+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + " from " + PlaceDao.TABLENAME + " P left join "
			+ PlaceUnitDao.TABLENAME + " PU on PU." + PlaceUnitDao.Properties.PlaceId.columnName + "=P."
			+ PlaceDao.Properties.Id.columnName + " left join " + UnitDao.TABLENAME + " U on PU."
			+ PlaceUnitDao.Properties.UnitId.columnName + "=U." + UnitDao.Properties.Id.columnName + " left join "
			+ FacultyDao.TABLENAME + " F on F." + FacultyDao.Properties.Id.columnName + "=U."
			+ UnitDao.Properties.FacultyId.columnName + " where U." + UnitDao.Properties.Name.columnName
			+ " like ? or U." + UnitDao.Properties.ShortName.columnName + " like ?";

	private static final UriMatcher sUriMatcher;
	private DaoSession mDaoSession;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case CAMPUS_MAP_DIR:
			return DIR_CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public boolean onCreate() {
		mDaoSession = DatabaseHelper.getDaoSession(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDaoSession.getDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String query = qb.buildUnionQuery(new String[] { QUERY_SEARCH_BY_PLACE, QUERY_SEARCH_BY_UNIT }, null,
				MAX_SUGGESTIONS);
		String arg = new StringBuilder("%").append(selectionArgs[0]).append("%").toString();
		Cursor c = db.rawQuery(query, new String[] { arg, arg, arg, arg, arg });
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		throw new UnsupportedOperationException("Not implemented");
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Config.SEARCH_SUGGESTION_AUTHORITY, "", CAMPUS_MAP_DIR);
	}

}
