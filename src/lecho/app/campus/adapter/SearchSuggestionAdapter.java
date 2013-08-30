package lecho.app.campus.adapter;

import lecho.app.campus.R;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.widget.SearchView;

public class SearchSuggestionAdapter extends ResourceCursorAdapter {
	private static final String TAG = SearchSuggestionAdapter.class.getSimpleName();
	private SearchableInfo mSearchableInfo;
	private SearchView mSearchView;

	public SearchSuggestionAdapter(Context context, SearchView searchView, SearchableInfo searchableInfo) {
		super(context, R.layout.search_dropdown_item, null, FLAG_REGISTER_CONTENT_OBSERVER);
		mSearchableInfo = searchableInfo;
		mSearchView = searchView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// String symbol =
		// cursor.getString(cursor.getColumnIndexOrThrow(PlaceDao.Properties.Symbol.columnName));
		String name = cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1));
		String description = cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_2));

		TextView text1 = (TextView) view.findViewById(android.R.id.text1);
		TextView text2 = (TextView) view.findViewById(android.R.id.text2);

		text2.setText(description);
		text2.setVisibility(View.VISIBLE);
		text1.setText(name);
		text1.setSingleLine(true);
		text1.setMaxLines(1);
		text1.setVisibility(View.VISIBLE);

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

	public void close() {
		changeCursor(null);
	}
}
