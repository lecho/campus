package lecho.app.campus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Main map activity. Shows area of whole campus.
 * 
 * @author lecho
 * 
 */
public class MapActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {
    private static final int PLACES_LOADER_ID = 1;
    private static final String TAG = MapActivity.class.getSimpleName();
    private static final String LOADER_SELECTION = "lecho.app.campus:LOADER_SELECTION";
    private static final String LOADER_SELECTION_ARGS = "lecho.app.campus:LOADER_SELECTION_ARGS";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // TODO setup google map
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == PLACES_LOADER_ID) {
            // TODO create cursor loader and apply filters
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (loader.getId() == PLACES_LOADER_ID) {
            // TODO setup map(markers etc)
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == PLACES_LOADER_ID) {
            mMap.clear();
        }

    }

}
