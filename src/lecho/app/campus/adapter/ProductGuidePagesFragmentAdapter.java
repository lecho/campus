package lecho.app.campus.adapter;

import java.util.List;

import lecho.app.campus.fragment.ProductGuidePageFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.Pair;

/**
 * 
 * @author Lecho
 * 
 */
public class ProductGuidePagesFragmentAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = "PlaceImageFragmentAdapter";
	// first - image raw resources, second - text
	private List<Pair<Integer, Integer>> mResources;

	public ProductGuidePagesFragmentAdapter(FragmentManager fm, List<Pair<Integer, Integer>> resources) {
		super(fm);
		if (null == resources) {
			Log.e(TAG, "Null rawResources arrat");
			throw new IllegalArgumentException("Null rawResources array");
		}
		mResources = resources;
	}

	@Override
	public Fragment getItem(int position) {
		Pair<Integer, Integer> pair = mResources.get(position);
		return ProductGuidePageFragment.newInstance(pair.first, pair.second);
	}

	@Override
	public int getCount() {
		return mResources.size();
	}

}
