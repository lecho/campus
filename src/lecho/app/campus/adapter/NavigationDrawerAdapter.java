package lecho.app.campus.adapter;

import lecho.app.campus.R;
import lecho.app.campus.utils.NavigationDrawerItem;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

	public NavigationDrawerAdapter(Context context, int resource, NavigationDrawerItem[] objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			if (0 == getItemViewType(position)) {
				convertView = View.inflate(getContext(), R.layout.item_title_navigation_drawer, null);
			} else {
				convertView = View.inflate(getContext(), R.layout.item_navigation_drawer, null);
			}
		}

		TextView tv = (TextView) convertView.findViewById(R.id.text);
		tv.setText(getItem(position).label);
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		NavigationDrawerItem item = getItem(position);
		if (item.isTitle) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		if (0 == getItemViewType(position)) {
			return false;
		} else {
			return true;
		}
	}

}
