package lecho.app.campus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Displays single place details. On xlarge screens displays detailed map also.
 * 
 * @author lecho
 * 
 */
public class PlaceDetailsActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {
    private static final int PLACE_LOADER_ID = 1;
    public static final String PLACE_ID = "lecho.app.campus:PLACE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

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
