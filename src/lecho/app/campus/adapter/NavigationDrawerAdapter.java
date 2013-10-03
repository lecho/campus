package lecho.app.campus.adapter;

import lecho.app.campus.R;
import lecho.app.campus.utils.NavigationDrawerItem;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {
	private static final String TAG = "NavigationDrawerAdapter";

	public NavigationDrawerAdapter(Context context, int resource, NavigationDrawerItem[] objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			int itemType = getItemViewType(position);
			if (NavigationDrawerItem.TYPE_TITLE == itemType) {
				convertView = View.inflate(getContext(), R.layout.item_title_navigation_drawer, null);
			} else if (NavigationDrawerItem.TYPE_ITEM_UNDER_TITLE == itemType) {
				convertView = View.inflate(getContext(), R.layout.item_under_title_navigation_drawer, null);
			} else {
				Log.e(TAG, "Returning default title view - Invalid drawer item type" + itemType);
				convertView = View.inflate(getContext(), R.layout.item_title_navigation_drawer, null);
			}
		}
		// TODO if there will be more views use ViewHolder.
		TextView tv = (TextView) convertView.findViewById(R.id.text);
		tv.setText(getContext().getString(getItem(position).stringRes));
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).type;
	}

	@Override
	public int getViewTypeCount() {
		return NavigationDrawerItem.NUMBER_OF_ITEM_TYPES;
	}

	@Override
	public boolean isEnabled(int position) {
		Log.e(TAG, "pos: " + position + " and size " + getCount());
		return NavigationDrawerItem.isItemClickable(getItemViewType(position));
	}

}
