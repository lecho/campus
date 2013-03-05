package lecho.app.campus.fragment;

import lecho.app.campus.contract.Category;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class CategoryFilterDialogFragment extends DialogFragment implements LoaderCallbacks<Cursor> {
    private static final int CATEGORY_LOADER = 1;
    public static final String CATEGORY_ID = "lecho.app.campus:CATEGORY_ID";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CATEGORY_LOADER) {
            return new CursorLoader(getActivity().getApplicationContext(), Category.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (loader.getId() == CATEGORY_LOADER) {
            // TODO
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CATEGORY_LOADER) {
            // TODO
        }

    }

}
