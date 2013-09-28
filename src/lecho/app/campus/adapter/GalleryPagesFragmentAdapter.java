package lecho.app.campus.adapter;

import java.io.File;

import lecho.app.campus.fragment.GalleryPageFragment;
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
public class GalleryPagesFragmentAdapter extends FragmentStatePagerAdapter {
	private static final String TAG = "PlaceImageFragmentAdapter";
	private String mSymbol;
	private String[] mImagesNames;

	public GalleryPagesFragmentAdapter(FragmentManager fm, String symbol, String[] paths) {
		super(fm);
		if (TextUtils.isEmpty(symbol)) {
			Log.e(TAG, "Null symbol");
			throw new IllegalArgumentException("Null symbol");
		}
		mSymbol = symbol;
		if (null == paths) {
			Log.e(TAG, "Null paths");
			throw new IllegalArgumentException("Null paths");
		}
		mImagesNames = paths;
	}

	@Override
	public Fragment getItem(int position) {
		String imageName = mImagesNames[position];
		final StringBuilder path = new StringBuilder(Utils.getPlaceImagesDir(mSymbol)).append(File.separator).append(
				imageName);
		return GalleryPageFragment.newInstance(path.toString());
	}

	@Override
	public int getCount() {
		return mImagesNames.length;
	}

}
