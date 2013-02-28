package lecho.app.campus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MapActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {
    private static final int PLACES_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_place_details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == PLACES_LOADER_ID) {
            // TODO
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (loader.getId() == PLACES_LOADER_ID) {
            // TODO
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == PLACES_LOADER_ID) {
            // TODO
        }

    }

}
