package lecho.app.campus.adapter;

import java.util.List;

import lecho.app.campus.R;
import lecho.app.campus.dao.Place;
import lecho.app.campus.utils.Config;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchResultViewAdapter extends PagerAdapter {
	private static final String TAG = "SearchResultViewAdapter";

	private Context mContext;
	private OnSearchResultClickListener mListener;
	List<Place> mPlaces;

	public SearchResultViewAdapter(Context context, OnSearchResultClickListener listener, List<Place> places) {
		mContext = context;
		mListener = listener;
		mPlaces = places;
	}

	@Override
	public int getCount() {
		return mPlaces.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object view) {
		if(Config.DEBUG){
			Log.d(TAG, "Remove search result view");
		}
		container.removeView((View) view);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(Config.DEBUG){
			Log.d(TAG, "Creating search result view");
		}
		Place place = mPlaces.get(position);

		View view = View.inflate(mContext, R.layout.fragment_search_result, null);
		TextView tvSymbol = (TextView) view.findViewById(R.id.symbol);
		tvSymbol.setText(place.getSymbol());
		TextView tvDescription = (TextView) view.findViewById(R.id.description);
		TextView tvName = (TextView) view.findViewById(R.id.name);
		final String description = place.getDescription();
		final String name = place.getName();
		if (TextUtils.isEmpty(description)) {
			tvName.setSingleLine(false);
			tvName.setMaxLines(2);
			tvName.setText(name);
			tvDescription.setVisibility(View.GONE);
		} else {
			tvName.setSingleLine(true);
			tvName.setMaxLines(1);
			tvName.setText(name);
			tvDescription.setVisibility(View.VISIBLE);
			tvDescription.setText(description);
		}
		view.setOnClickListener(new OnSearchResultViewClickListener(place.getId(), mListener));
		container.addView(view);
		return view;

	}

	public Long getItemId(int position) {
		return mPlaces.get(position).getId();
	}

	@Override
	public int getItemPosition(Object object) {
		if (null == object) {
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
		throw new IllegalArgumentException("Could not find given object in SearchResultFragmentAdapter");
	}

	private static class OnSearchResultViewClickListener implements View.OnClickListener {
		private OnSearchResultClickListener mListener;
		private Long mPlaceId;

		public OnSearchResultViewClickListener(Long placeId, OnSearchResultClickListener listener) {
			mPlaceId = placeId;
			mListener = listener;
		}

		@Override
		public void onClick(View v) {
			mListener.onSearchResultClick(mPlaceId);
		}

	}
	
	public interface OnSearchResultClickListener {
		/**
		 * Should show place details view.
		 * 
		 * @param placeId
		 */
		public void onSearchResultClick(Long placeId);
	}

}