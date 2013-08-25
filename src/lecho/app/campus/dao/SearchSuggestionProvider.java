package lecho.app.campus.dao;

import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DatabaseHelper;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * 
 * @author lecho
 * 
 */
public class SearchSuggestionProvider extends ContentProvider {
	public static final String DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lecho.app.campus";
	public static final int CAMPUS_MAP_DIR = 1;
	private static final int MAX_SUGGESTIONS = 6;

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

	/**
	 * Returns matrix cursor for suggestions
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		MatrixCursor mc = new MatrixCursor(new String[] { PlaceDao.Properties.Id.columnName,
				SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2,
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID });

		for (int i = 0; i < 5; ++i) {
			mc.newRow().add(1L).add("A1").add("LODEX").add(1);
		}

		return mc;
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
