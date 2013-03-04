package lecho.app.campus.content;

import lecho.app.campus.contract.Category;
import lecho.app.campus.contract.Faculty;
import lecho.app.campus.contract.Place;
import lecho.app.campus.contract.PlaceCategory;
import lecho.app.campus.contract.PlaceFaculty;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlaceProvider extends ContentProvider {
    private static final String ITEM_CONTENT_TYPE = " vnd.android.cursor.item/vnd.lecho.app.campus.place";
    private static final String DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lecho.app.campus.place";
    private static final String DIR_CONTENT_TYPE_BY_CATEGORY = "vnd.android.cursor.dir/vnd.lecho.app.campus.place_by_category";
    private static final String DIR_CONTENT_TYPE_BY_FACULTY = "vnd.android.cursor.dir/vnd.lecho.app.campus.place_by_faculty";
    private static final int PLACE_DIR = 1;
    private static final int PLACE_ITEM = 2;
    private static final int PLACE_DIR_BY_CATEGORY = 3;
    private static final int PLACE_DIR_BY_FACULTY = 4;
    private DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Place.AUTHORITY, Place.TABLE_NAME, PLACE_DIR);
        sUriMatcher.addURI(Place.AUTHORITY, Place.TABLE_NAME + "/#", PLACE_ITEM);
        // filters by category id
        sUriMatcher.addURI(Place.AUTHORITY, Place.TABLE_NAME + "/" + Category.TABLE_NAME + "/#", PLACE_DIR_BY_CATEGORY);
        // filter by faculty id
        sUriMatcher.addURI(Place.AUTHORITY, Place.TABLE_NAME + "/" + Faculty.TABLE_NAME + "/#", PLACE_DIR_BY_FACULTY);
    }

    // TODO test
    private static final String SELECTION_PLACE_BY_CATEGORY = Place._ID + " in (select " + PlaceCategory.PLACE_ID
            + " from " + PlaceCategory.TABLE_NAME + " where " + PlaceCategory.CATEGORY_ID + "=?)";

    private static final String SELECTION_PLACE_BY_FACULTY = Place._ID + " in (select " + PlaceFaculty.PLACE_ID
            + " from " + PlaceFaculty.TABLE_NAME + " where " + PlaceFaculty.FACULTY_ID + "=?)";

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (sUriMatcher.match(uri)) {
        case PLACE_DIR:
            rowsDeleted = db.delete(Place.TABLE_NAME, selection, selectionArgs);
            break;
        case PLACE_ITEM:
            StringBuilder sb = new StringBuilder().append(Place._ID).append("=?");
            String[] args = new String[] { uri.getLastPathSegment() };
            rowsDeleted = db.delete(Place.TABLE_NAME, sb.toString(), args);
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
        case PLACE_DIR:
            return DIR_CONTENT_TYPE;
        case PLACE_ITEM:
            return ITEM_CONTENT_TYPE;
        case PLACE_DIR_BY_CATEGORY:
            return DIR_CONTENT_TYPE_BY_CATEGORY;
        case PLACE_DIR_BY_FACULTY:
            return DIR_CONTENT_TYPE_BY_FACULTY;
        default:
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri newRow = null;
        switch (sUriMatcher.match(uri)) {
        case PLACE_DIR:
            long id = db.insert(Place.TABLE_NAME, null, values);
            newRow = ContentUris.withAppendedId(Place.CONTENT_URI, id);
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
        case PLACE_DIR: {
            c = db.query(Place.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
            break;
        }
        case PLACE_ITEM: {
            StringBuilder sb = new StringBuilder().append(Place._ID).append("=?");
            String[] args = new String[] { uri.getLastPathSegment() };
            c = db.query(Place.TABLE_NAME, projection, sb.toString(), args, null, null, null);
            break;
        }
        case PLACE_DIR_BY_CATEGORY: {
            String[] args = new String[] { uri.getLastPathSegment() };
            c = db.query(Place.TABLE_NAME, projection, SELECTION_PLACE_BY_CATEGORY, args, null, null, null);
            break;
        }
        case PLACE_DIR_BY_FACULTY: {
            String[] args = new String[] { uri.getLastPathSegment() };
            c = db.query(Place.TABLE_NAME, projection, SELECTION_PLACE_BY_FACULTY, args, null, null, null);
            break;
        }
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
        case PLACE_DIR:
            rowsUpdated = db.update(Place.TABLE_NAME, values, selection, selectionArgs);
            break;
        case PLACE_ITEM:
            StringBuilder sb = new StringBuilder().append(Place._ID).append("=?");
            String[] args = new String[] { uri.getLastPathSegment() };
            rowsUpdated = db.update(Place.TABLE_NAME, values, sb.toString(), args);
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
        case PLACE_DIR:
            try {
                db.beginTransaction();
                for (ContentValues cv : values) {
                    if (db.insert(Place.TABLE_NAME, null, cv) > 0) {
                        ++rowsAffected;
                    }
                    db.yieldIfContendedSafely();
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
