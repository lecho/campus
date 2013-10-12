package lecho.app.campus.adapter;

import lecho.app.campus.fragment.PlaceDetailsFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class PlaceDetailsFragmentAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = "PlaceDetailsFragmentAdapter";
	long[] mPlaceIdList;

	public PlaceDetailsFragmentAdapter(FragmentManager fm, long[] placeIdList) {
		super(fm);
		if (null == placeIdList) {
			Log.e(TAG, "Id list cannot be null for PlaceDetailsFragmentAdapter");
			throw new IllegalArgumentException("Id list cannot be null for PlaceDetailsFragmentAdapter");
		}
		mPlaceIdList = placeIdList;
	}

	@Override
	public Fragment getItem(int position) {
		Long placeId = mPlaceIdList[position];
		return PlaceDetailsFragment.newInstance(placeId);
	}

	@Override
	public int getCount() {
		return mPlaceIdList.length;
	}
}
