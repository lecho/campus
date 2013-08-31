package lecho.app.campus.adapter;

import java.util.List;

import lecho.app.campus.dao.Place;
import lecho.app.campus.fragment.SearchResultFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SearchResultFragmentAdapter extends FragmentPagerAdapter {
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

	@Override
	public long getItemId(int position) {
		return mPlaces.get(position).getId();
	}
}
