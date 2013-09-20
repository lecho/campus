package lecho.app.campus.utils;

public final class NavigationDrawerItem {
	public static final int TYPE_TITLE = 0;
	public static final int TYPE_ITEM_UNDER_TITLE = 1;
	public static final int TYPE_INDEPENDENT_ITEM = 2;

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
}
