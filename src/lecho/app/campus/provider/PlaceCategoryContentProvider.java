package lecho.app.campus.provider;

import lecho.app.campus.content.PlaceCategory;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlaceCategoryContentProvider extends ContentProvider {
	public static final String ITEM_CONTENT_TYPE = " vnd.android.cursor.item/vnd.lecho.app.campus.place_category";
	public static final String DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lecho.app.campus.place_category";
	public static final int PLACE_CATEGORY_DIR = 1;
	public static final int PLACE_CATEGORY_ITEM = 2;
	protected DatabaseHelper mDbHelper;
	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(PlaceCategory.AUTHORITY, PlaceCategory.TABLE_NAME, PLACE_CATEGORY_DIR);
		sUriMatcher.addURI(PlaceCategory.AUTHORITY, PlaceCategory.TABLE_NAME + "/#", PLACE_CATEGORY_ITEM);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowsDeleted = 0;
		switch (sUriMatcher.match(uri)) {
		case PLACE_CATEGORY_DIR:
			rowsDeleted = db.delete(PlaceCategory.TABLE_NAME, selection, selectionArgs);
			break;
		case PLACE_CATEGORY_ITEM:
			StringBuilder sb = new StringBuilder().append(PlaceCategory._ID).append("=?");
			String[] args = new String[] { uri.getLastPathSegment() };
			rowsDeleted = db.delete(PlaceCategory.TABLE_NAME, sb.toString(), args);
			break;
		default:
			throw new IllegalArgumentException("Invalid URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case PLACE_CATEGORY_DIR:
			return DIR_CONTENT_TYPE;
		case PLACE_CATEGORY_ITEM:
			return ITEM_CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Invalid URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Uri newRow = null;
		switch (sUriMatcher.match(uri)) {
		case PLACE_CATEGORY_DIR:
			long id = db.insert(PlaceCategory.TABLE_NAME, null, values);
			newRow = ContentUris.withAppendedId(PlaceCategory.CONTENT_URI, id);
			break;
		default:
			throw new IllegalArgumentException("Invalid URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return newRow;
	}

	@Override
	public boolean onCreate() {
		try {
			mDbHelper = DatabaseHelper.getInstance(getContext());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c;
		switch (sUriMatcher.match(uri)) {
		case PLACE_CATEGORY_DIR:
			c = db.query(PlaceCategory.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
			break;
		case PLACE_CATEGORY_ITEM:
			StringBuilder sb = new StringBuilder().append(PlaceCategory._ID).append("=?");
			String[] args = new String[] { uri.getLastPathSegment() };
			c = db.query(PlaceCategory.TABLE_NAME, projection, sb.toString(), args, null, null, null);
		default:
			throw new IllegalArgumentException("Invalid URI " + uri);
		}
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		switch (sUriMatcher.match(uri)) {
		case PLACE_CATEGORY_DIR:
			rowsUpdated = db.update(PlaceCategory.TABLE_NAME, values, selection, selectionArgs);
			break;
		case PLACE_CATEGORY_ITEM:
			StringBuilder sb = new StringBuilder().append(PlaceCategory._ID).append("=?");
			String[] args = new String[] { uri.getLastPathSegment() };
			rowsUpdated = db.update(PlaceCategory.TABLE_NAME, values, sb.toString(), args);
			break;
		default:
			throw new IllegalArgumentException("Invalid URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowsAffected = 0;
		switch (sUriMatcher.match(uri)) {
		case PLACE_CATEGORY_DIR:
			try {
				db.beginTransaction();
				for (ContentValues cv : values) {
					if (db.insert(PlaceCategory.TABLE_NAME, null, cv) > 0) {
						++rowsAffected;
					}
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

}
