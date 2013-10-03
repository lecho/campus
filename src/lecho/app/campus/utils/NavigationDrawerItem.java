package lecho.app.campus.utils;

import android.util.Log;

public final class NavigationDrawerItem {
	private static final String TAG = "NavigationDrawerItem";

	public static final int TYPE_TITLE = 1;// nonclickable
	public static final int TYPE_ITEM_UNDER_TITLE = 2;// clickabel
	public static final int NUMBER_OF_ITEM_TYPES = 2;

	public NavigationDrawerItem(int stringRes, int action, String argument, int type) {
		this.stringRes = stringRes;
		this.action = action;
		this.argument = argument;
		this.type = type;
	}

	public final int stringRes;
	public final int action;
	public final String argument;
	public final int type;

	public static final boolean isItemClickable(int itemType) {
		if (TYPE_TITLE == itemType) {
			return false;
		} else if (TYPE_ITEM_UNDER_TITLE == itemType) {
			return true;
		} else {
			Log.e(TAG, "Invalid item type " + itemType);
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + action;
		result = prime * result + ((argument == null) ? 0 : argument.hashCode());
		result = prime * result + stringRes;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NavigationDrawerItem other = (NavigationDrawerItem) obj;
		if (action != other.action)
			return false;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		if (stringRes != other.stringRes)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NavigationDrawerItem [stringRes=" + stringRes + ", action=" + action + ", argument=" + argument
				+ ", type=" + type + "]";
	}

}
