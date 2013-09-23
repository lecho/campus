package lecho.app.campus.activity;

import lecho.app.campus.fragment.PlaceDetailsFragment;
import lecho.app.campus.utils.Config;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PlaceDetailsActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		// TODO Validate placeId.
		Long placeId = getIntent().getLongExtra(Config.EXTRA_PLACE_ID, Long.MIN_VALUE);
		PlaceDetailsFragment fragment = PlaceDetailsFragment.newInstance(placeId);
		getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (android.R.id.home == itemId) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
