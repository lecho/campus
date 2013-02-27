package lecho.app.campus.content;

import lecho.app.campus.contract.PlaceFaculty;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlaceFacultyProvider extends ContentProvider {
    public static final String ITEM_CONTENT_TYPE = " vnd.android.cursor.item/vnd.lecho.app.campus.place_faculty";
    public static final String DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lecho.app.campus.place_faculty";
    public static final int PLACE_FACULTY_DIR = 1;
    public static final int PLACE_FACULTY_ITEM = 2;
    protected DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(PlaceFaculty.AUTHORITY, PlaceFaculty.TABLE_NAME, PLACE_FACULTY_DIR);
        sUriMatcher.addURI(PlaceFaculty.AUTHORITY, PlaceFaculty.TABLE_NAME + "/#", PLACE_FACULTY_ITEM);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (sUriMatcher.match(uri)) {
        case PLACE_FACULTY_DIR:
            rowsDeleted = db.delete(PlaceFaculty.TABLE_NAME, selection, selectionArgs);
            break;
        case PLACE_FACULTY_ITEM:
            StringBuilder sb = new StringBuilder().append(PlaceFaculty._ID).append("=?");
            String[] args = new String[] { uri.getLastPathSegment() };
            rowsDeleted = db.delete(PlaceFaculty.TABLE_NAME, sb.toString(), args);
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
        case PLACE_FACULTY_DIR:
            return DIR_CONTENT_TYPE;
        case PLACE_FACULTY_ITEM:
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
        case PLACE_FACULTY_DIR:
            long id = db.insert(PlaceFaculty.TABLE_NAME, null, values);
            newRow = ContentUris.withAppendedId(PlaceFaculty.CONTENT_URI, id);
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
        case PLACE_FACULTY_DIR:
            c = db.query(PlaceFaculty.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
            break;
        case PLACE_FACULTY_ITEM:
            StringBuilder sb = new StringBuilder().append(PlaceFaculty._ID).append("=?");
            String[] args = new String[] { uri.getLastPathSegment() };
            c = db.query(PlaceFaculty.TABLE_NAME, projection, sb.toString(), args, null, null, null);
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
        case PLACE_FACULTY_DIR:
            rowsUpdated = db.update(PlaceFaculty.TABLE_NAME, values, selection, selectionArgs);
            break;
        case PLACE_FACULTY_ITEM:
            StringBuilder sb = new StringBuilder().append(PlaceFaculty._ID).append("=?");
            String[] args = new String[] { uri.getLastPathSegment() };
            rowsUpdated = db.update(PlaceFaculty.TABLE_NAME, values, sb.toString(), args);
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
        case PLACE_FACULTY_DIR:
            try {
                db.beginTransaction();
                for (ContentValues cv : values) {
                    if (db.insert(PlaceFaculty.TABLE_NAME, null, cv) > 0) {
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