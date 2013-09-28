package lecho.app.campus.activity;

import java.util.ArrayList;
import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.adapter.ProductGuidePagesFragmentAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Pair;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.UnderlinePageIndicator;

public class ProductGuideActivity extends SherlockFragmentActivity {
	public static final String TAG = "ProductGuideActivity";
	private ViewPager mPager;
	private UnderlinePageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_guide);
		mPager = (ViewPager) findViewById(R.id.view_pager);
		mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);

		List<Pair<Integer, Integer>> resources = new ArrayList<Pair<Integer, Integer>>(3);
		resources.add(new Pair<Integer, Integer>(R.raw.guide_page_1, R.string.guide_page1_text));
		resources.add(new Pair<Integer, Integer>(R.raw.guide_page_2, R.string.guide_page2_text));
		resources.add(new Pair<Integer, Integer>(R.raw.guide_page_3, R.string.guide_page3_text));
		mPager.setAdapter(new ProductGuidePagesFragmentAdapter(getSupportFragmentManager(), resources));
		mPager.setOffscreenPageLimit(1);
		mIndicator.setViewPager(mPager);
	}

}
