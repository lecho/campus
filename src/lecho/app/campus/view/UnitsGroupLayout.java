package lecho.app.campus.view;

import lecho.app.campus.R;
import lecho.app.campus.utils.Utils;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UnitsGroupLayout extends LinearLayout {

	private boolean mHasFaculty = false;
	private int mUnitsCount = 0;

	public UnitsGroupLayout(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundResource(R.drawable.details_box_shadow);
		// android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(
		// android.widget.AbsListView.LayoutParams.MATCH_PARENT,
		// android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		// int padding = Utils.dp2px(getContext(), 8);
		// setPadding(padding, padding, padding, padding);

	}

	public void setFaculty(String facultyName) {
		TextView tv = new TextView(getContext());
		tv.setText(facultyName);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int margin = Utils.dp2px(getContext(), 16);
		lp.setMargins(margin, 0, margin, 0);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.RIGHT);
		tv.setTypeface(null, Typeface.BOLD);
		this.addView(tv);
		mHasFaculty = true;

	}

	public void addUnit(String unitName) {
		TextView tv = new TextView(getContext());
		tv.setText(unitName);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.LEFT);
		tv.setTextColor(getContext().getResources().getColor(R.color.black));
		tv.setTextAppearance(getContext(), android.R.attr.textAppearanceSmall);
		this.addView(tv);
		++mUnitsCount;
	}

	public void addSeparator() {
		View v = new View(getContext());
		v.setBackgroundResource(R.color.background);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dp2px(getContext(), 1));
		int margin = Utils.dp2px(getContext(), 8);
		lp.setMargins(margin, margin, margin, margin);
		v.setLayoutParams(lp);
		this.addView(v);
	}

	public boolean hasFaculty() {
		return mHasFaculty;
	}

	public int getUnitsCount() {
		return mUnitsCount;
	}
}
