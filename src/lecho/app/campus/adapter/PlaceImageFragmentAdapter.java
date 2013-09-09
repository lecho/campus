package lecho.app.campus.adapter;

import java.io.File;

import lecho.app.campus.fragment.PlaceImageFragment;
import lecho.app.campus.utils.Utils;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author Lecho
 * 
 */
public class PlaceImageFragmentAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = "PlaceImageFragmentAdapter";
	private String mSymbol;
	private String[] mImagesNames;

	public PlaceImageFragmentAdapter(FragmentManager fm, String symbol, String[] paths) {
		super(fm);
		if (TextUtils.isEmpty(symbol)) {
			Log.e(TAG, "Place symbol cannot be empty for PlaceImageFragmentAdapter");
			throw new IllegalArgumentException("Place symbol cannot be empty for PlaceImageFragmentAdapter");
		}
		mSymbol = symbol;
		if (null == paths) {
			Log.e(TAG, "Images names cannot be null for PlaceImageFragmentAdapter");
			throw new IllegalArgumentException("Images names cannot be null for PlaceImageFragmentAdapter");
		}
		mImagesNames = paths;
	}

	@Override
	public Fragment getItem(int position) {
		String imageName = mImagesNames[position];
		final StringBuilder path = new StringBuilder(Utils.getPlaceImagesDir(mSymbol)).append(File.separator).append(
				imageName);
		return PlaceImageFragment.newInstance(path.toString());
	}

	@Override
	public int getCount() {
		return mImagesNames.length;
	}

}
