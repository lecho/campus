package lecho.app.campus.adapter;

import lecho.app.campus.R;
import lecho.app.campus.dao.PlaceDao;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.widget.SearchView;

public class SearchSuggestionAdapter extends ResourceCursorAdapter {
	private static final String TAG = SearchSuggestionAdapter.class.getSimpleName();
	private SearchableInfo mSearchableInfo;
	private SearchView mSearchView;

	static final int INVALID_INDEX = -1;
	// Cached column indexes, updated when the cursor changes.
	private int mSymbolCol = INVALID_INDEX;
	private int mNameCol = INVALID_INDEX;
	private int mDescriptionCol = INVALID_INDEX;

	public SearchSuggestionAdapter(Context context, SearchView searchView, SearchableInfo searchableInfo) {
		super(context, R.layout.search_dropdown_item, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mSearchableInfo = searchableInfo;
		mSearchView = searchView;
	}

	/**
	 * Tags the view with cached child view look-ups.
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = super.newView(context, cursor, parent);
		v.setTag(new ChildViewCache(v));
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ChildViewCache views = (ChildViewCache) view.getTag();

		String symbol = cursor.getString(mSymbolCol);
		String name = cursor.getString(mNameCol);
		String description = cursor.getString(mDescriptionCol);

		views.mSymbol.setText(symbol);

		if (TextUtils.isEmpty(description)) {
			views.mName.setSingleLine(false);
			views.mName.setMaxLines(2);
			views.mName.setText(name);
			views.mDescription.setVisibility(View.GONE);
		} else {
			views.mName.setSingleLine(true);
			views.mName.setMaxLines(1);
			views.mName.setText(name);
			views.mDescription.setVisibility(View.VISIBLE);
			views.mDescription.setText(description);
		}
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		String query = (constraint == null) ? "" : constraint.toString();
		/**
		 * for in app search we show the progress spinner until the cursor is
		 * returned with the results.
		 */
		Cursor cursor = null;
		if (mSearchView.getVisibility() != View.VISIBLE || mSearchView.getWindowVisibility() != View.VISIBLE) {
			return null;
		}
		// mSearchView.getWindow().getDecorView().post(mStartSpinnerRunnable);
		// // TODO:
		try {
			cursor = getSuggestions(query);
			// trigger fill window so the spinner stays up until the results
			// are copied over and
			// closer to being ready
			if (cursor != null) {
				return cursor;
			}
		} catch (RuntimeException e) {
			Log.w(TAG, "Search suggestions query threw an exception.", e);
		}
		// If cursor is null or an exception was thrown, stop the spinner
		// and return null.
		// changeCursor doesn't get called if cursor is null
		// mSearchView.getWindow().getDecorView().post(mStopSpinnerRunnable);
		return null;
	}

	public Cursor getSuggestions(String query) {
		if (mSearchableInfo == null) {
			return null;
		}

		String authority = mSearchableInfo.getSuggestAuthority();
		if (authority == null) {
			return null;
		}

		Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(authority)
				.query("") // TODO: Remove, workaround for a bug in
							// Uri.writeToParcel()
				.fragment(""); // TODO: Remove, workaround for a bug in
								// Uri.writeToParcel()

		// if content path provided, insert it now
		final String contentPath = mSearchableInfo.getSuggestPath();
		if (contentPath != null) {
			uriBuilder.appendEncodedPath(contentPath);
		}

		// append standard suggestion query path
		uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

		// get the query selection, may be null
		String selection = mSearchableInfo.getSuggestSelection();
		// inject query, either as selection args or inline
		String[] selArgs = null;
		if (selection != null) { // use selection if provided
			selArgs = new String[] { query };
		} else { // no selection, use REST pattern
			uriBuilder.appendPath(query);
		}

		Uri uri = uriBuilder.build();

		// finally, make the query
		return mContext.getContentResolver().query(uri, null, selection, selArgs, null);
	}

	/**
	 * Cache columns.
	 */
	@Override
	public void changeCursor(Cursor c) {
		try {
			super.changeCursor(c);

			if (c != null) {
				mSymbolCol = c.getColumnIndex(PlaceDao.Properties.Symbol.columnName);
				mNameCol = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
				mDescriptionCol = c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
			}
		} catch (Exception e) {
			Log.e(TAG, "error changing cursor and caching columns", e);
		}
	}

	public void close() {
		changeCursor(null);
	}

	/**
	 * Cache of the child views of drop-drown list items, to avoid looking up
	 * the children each time the contents of a list item are changed.
	 */
	private final static class ChildViewCache {
		public final TextView mSymbol;
		public final TextView mName;
		public final TextView mDescription;

		public ChildViewCache(View v) {
			mSymbol = (TextView) v.findViewById(R.id.symbol);
			mName = (TextView) v.findViewById(R.id.name);
			mDescription = (TextView) v.findViewById(R.id.description);
		}
	}
}
