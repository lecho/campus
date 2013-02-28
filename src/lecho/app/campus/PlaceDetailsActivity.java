package lecho.app.campus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PlaceDetailsActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {
    private static final int PLACE_LOADER_ID = 1;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == PLACE_LOADER_ID) {
            // TODO
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (loader.getId() == PLACE_LOADER_ID) {
            // TODO
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == PLACE_LOADER_ID) {
            // TODO
        }

    }

}
