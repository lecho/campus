package lecho.app.campus.activity;

import java.util.ArrayList;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.adapter.ProductGuidePagesFragmentAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.UnderlinePageIndicator;

public class ProductGuideActivity extends SherlockFragmentActivity {
	public static final String TAG = "ProductGuideActivity";
	private ViewPager mPager;
	private UnderlinePageIndicator mIndicator;
	private int mPagesNumber = 3;
	private Button mCloseButton;
	private Button mNextButton;
	private View mSeparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_guide);
		mPager = (ViewPager) findViewById(R.id.view_pager);
		mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		mCloseButton = (Button) findViewById(R.id.close_guide);
		mNextButton = (Button) findViewById(R.id.next_page);
		mSeparator = findViewById(R.id.separator);

		List<Pair<Integer, Integer>> resources = new ArrayList<Pair<Integer, Integer>>(3);
		resources.add(new Pair<Integer, Integer>(R.raw.guide_page1, R.string.guide_page1_text));
		resources.add(new Pair<Integer, Integer>(R.raw.guide_page2, R.string.guide_page2_text));
		resources.add(new Pair<Integer, Integer>(R.raw.guide_page3, R.string.guide_page3_text));
		mPager.setAdapter(new ProductGuidePagesFragmentAdapter(getSupportFragmentManager(), resources));
		mPager.setOffscreenPageLimit(1);
		mPager.setCurrentItem(0);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new PageSelectedListener());

		mCloseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		mNextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(mPager.getCurrentItem() + 1);
			}
		});
	}

	private class PageSelectedListener extends SimpleOnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			Log.e(TAG, "Page " + position);
			if (position == mPagesNumber - 1) {
				mNextButton.setVisibility(View.GONE);
				mSeparator.setVisibility(View.GONE);
			} else {
				mNextButton.setVisibility(View.VISIBLE);
				mSeparator.setVisibility(View.VISIBLE);
			}
		}
	}

}
