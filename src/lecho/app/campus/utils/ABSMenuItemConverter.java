package lecho.app.campus.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class ABSMenuItemConverter {

	public static MenuItem create(final com.actionbarsherlock.view.MenuItem menuItem) {
		return new MenuItem() {

			@Override
			public boolean collapseActionView() {
				return false;
			}

			@Override
			public boolean expandActionView() {
				return false;
			}

			@Override
			public ActionProvider getActionProvider() {
				return null;
			}

			@Override
			public View getActionView() {
				return null;
			}

			@Override
			public char getAlphabeticShortcut() {
				return 0;
			}

			@Override
			public int getGroupId() {
				return 0;
			}

			@Override
			public Drawable getIcon() {
				return null;
			}

			@Override
			public Intent getIntent() {
				return null;
			}

			@Override
			public int getItemId() {
				return menuItem.getItemId();
			}

			@Override
			public ContextMenuInfo getMenuInfo() {
				return null;
			}

			@Override
			public char getNumericShortcut() {
				return 0;
			}

			@Override
			public int getOrder() {
				return 0;
			}

			@Override
			public SubMenu getSubMenu() {
				return null;
			}

			@Override
			public CharSequence getTitle() {
				return null;
			}

			@Override
			public CharSequence getTitleCondensed() {
				return null;
			}

			@Override
			public boolean hasSubMenu() {
				return false;
			}

			@Override
			public boolean isActionViewExpanded() {
				return false;
			}

			@Override
			public boolean isCheckable() {
				return false;
			}

			@Override
			public boolean isChecked() {
				return false;
			}

			@Override
			public boolean isEnabled() {
				return false;
			}

			@Override
			public boolean isVisible() {
				return false;
			}

			@Override
			public MenuItem setActionProvider(ActionProvider actionProvider) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MenuItem setActionView(View view) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MenuItem setActionView(int resId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public MenuItem setAlphabeticShortcut(char alphaChar) {
				return null;
			}

			@Override
			public MenuItem setCheckable(boolean checkable) {
				return null;
			}

			@Override
			public MenuItem setChecked(boolean checked) {
				return null;
			}

			@Override
			public MenuItem setEnabled(boolean enabled) {
				return null;
			}

			@Override
			public MenuItem setIcon(Drawable icon) {
				return null;
			}

			@Override
			public MenuItem setIcon(int iconRes) {
				return null;
			}

			@Override
			public MenuItem setIntent(Intent intent) {
				return null;
			}

			@Override
			public MenuItem setNumericShortcut(char numericChar) {
				return null;
			}

			@Override
			public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
				return null;
			}

			@Override
			public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
				return null;
			}

			@Override
			public MenuItem setShortcut(char numericChar, char alphaChar) {
				return null;
			}

			@Override
			public void setShowAsAction(int actionEnum) {

			}

			@Override
			public MenuItem setShowAsActionFlags(int actionEnum) {
				return null;
			}

			@Override
			public MenuItem setTitle(CharSequence title) {
				return null;
			}

			@Override
			public MenuItem setTitle(int title) {
				return null;
			}

			@Override
			public MenuItem setTitleCondensed(CharSequence title) {
				return null;
			}

			@Override
			public MenuItem setVisible(boolean visible) {
				return null;
			}

		};
	}
}
