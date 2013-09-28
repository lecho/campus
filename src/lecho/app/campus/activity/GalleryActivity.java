package lecho.app.campus.activity;

import lecho.app.campus.R;
import lecho.app.campus.adapter.GalleryPagesFragmentAdapter;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.ImagesDirAsyncTask;
import lecho.app.campus.utils.ImagesDirAsyncTask.OnImagesDirListener;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

/**
 * 
 * @author Lecho
 * 
 */
public class GalleryActivity extends SherlockFragmentActivity implements OnImagesDirListener {
	private static final String TAG = "PlaceDetailsActivity";
	private ViewPager mPager;
	private ProgressBar mProgressBar;
	private PageIndicator mIndicator;
	private String mSymbol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		mPager = (ViewPager) findViewById(R.id.view_pager);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
		mSymbol = getIntent().getStringExtra(Config.EXTRA_PLACE_SYMBOL);
		ImagesDirAsyncTask task = new ImagesDirAsyncTask(getApplicationContext(), this);
		task.execute(mSymbol);
	}

	@Override
	public void onImagesDir(String[] paths) {
		if (paths.length > 0) {
			mPager.setAdapter(new GalleryPagesFragmentAdapter(getSupportFragmentManager(), mSymbol, paths));
			mPager.setOffscreenPageLimit(1);
			mIndicator.setViewPager(mPager);
			mProgressBar.setVisibility(View.GONE);
		} else {
			// That's not normal and should never happen, if user starts this activity there should be at least one
			// image!
			Log.e(TAG, "Could not load any image for symbol: " + mSymbol);
			finish();
		}

	}

}
