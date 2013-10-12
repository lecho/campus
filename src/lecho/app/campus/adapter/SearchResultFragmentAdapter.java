package lecho.app.campus.adapter;

import java.util.List;

import lecho.app.campus.dao.Place;
import lecho.app.campus.fragment.SearchResultFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class SearchResultFragmentAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = "SearchResultFragmentAdapter";
	List<Place> mPlaces;

	public SearchResultFragmentAdapter(FragmentManager fm, List<Place> places) {
		super(fm);
		if (null == places) {
			throw new IllegalArgumentException("Places list cannot be null for SearchResultFragmentAdapter");
		}
		mPlaces = places;
	}

	@Override
	public Fragment getItem(int position) {
		Place place = mPlaces.get(position);
		return SearchResultFragment.newInstance(place.getId(), place.getSymbol(), place.getName(),
				place.getDescription());
	}

	@Override
	public int getCount() {
		return mPlaces.size();
	}

	public Long getItemId(int position) {
		return mPlaces.get(position).getId();
	}

	@Override
	public int getItemPosition(Object object) {
		if (null == object) {
			Log.w(TAG, "Trying to get item position for null");
			return -1;
		}
		// Sort and binary search for smaller list take longer then iteration.
		Place dest = (Place) object;
		int position = 0;
		for (Place place : mPlaces) {
			if (place.getId().equals(dest.getId())) {
				return position;
			}
			++position;
		}
		Log.e(TAG, "Could not find given object");
		throw new IllegalArgumentException("Could not find given object in SearchResultFragmentAdapter");
	}
}
