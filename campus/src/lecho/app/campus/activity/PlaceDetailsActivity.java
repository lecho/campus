package lecho.app.campus.activity;

import lecho.app.campus.R;
import lecho.app.campus.adapter.PlaceDetailsFragmentAdapter;
import lecho.app.campus.utils.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * It has to be started with intent containing array of longs(identifiers of all visible places) and integer(current
 * place position in search result adapter).
 * 
 * As results activity will set id and position of place currently displayed on details view.
 * 
 * @author Lecho
 * 
 */
public class PlaceDetailsActivity extends SherlockFragmentActivity {

	private ViewPager mViewPager;
	private PlaceDetailsFragmentAdapter mDetailsFragmentAdapter;

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_place_details);
		long[] visiblePlaces = getIntent().getExtras().getLongArray(Config.EXTRA_VISIBLE_PLACES);
		int currentPlacePostion = getIntent().getExtras().getInt(Config.EXTRA_PLACE_POSITION);
		mDetailsFragmentAdapter = new PlaceDetailsFragmentAdapter(getSupportFragmentManager(), visiblePlaces);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setAdapter(mDetailsFragmentAdapter);
		mViewPager.setCurrentItem(currentPlacePostion);
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
			setReturnResult();
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		setReturnResult();
		super.onBackPressed();
	}

	private void setReturnResult() {
		Bundle bundle = new Bundle();
		bundle.putInt(Config.EXTRA_PLACE_POSITION, mViewPager.getCurrentItem());
		bundle.putLong(Config.EXTRA_PLACE_ID, mDetailsFragmentAdapter.getItemId(mViewPager.getCurrentItem()));
		Intent data = new Intent();
		data.putExtras(bundle);
		setResult(Activity.RESULT_OK, data);
	}
}
