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
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int margin = Utils.dp2px(getContext(), 4);
		lp.setMargins(0, margin, 0, margin);
		setLayoutParams(lp);
	}

	public void setFaculty(String facultyName) {
		TextView tv = new TextView(getContext());
		tv.setText(facultyName);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int hMargin = Utils.dp2px(getContext(), 16);
		int vMargin = Utils.dp2px(getContext(), 16);
		lp.setMargins(hMargin, vMargin, hMargin, vMargin);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.RIGHT);
		tv.setTextAppearance(getContext(), android.R.attr.textAppearanceSmall);
		tv.setTextColor(getResources().getColor(R.color.black));
		tv.setTypeface(null, Typeface.BOLD);
		this.addView(tv);
		mHasFaculty = true;

	}

	public void addUnit(String unitName) {
		TextView tv = new TextView(getContext());
		tv.setText(unitName);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int hMargin = Utils.dp2px(getContext(), 16);
		int vMargin = Utils.dp2px(getContext(), 16);
		lp.setMargins(hMargin, vMargin, hMargin, vMargin);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.LEFT);
		tv.setTextAppearance(getContext(), android.R.attr.textAppearanceSmall);
		tv.setTextColor(getContext().getResources().getColor(R.color.black));
		this.addView(tv);
		++mUnitsCount;
	}

	public void addUnitSeparator() {
		View v = new View(getContext());
		v.setBackgroundResource(R.color.background);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		int margin = Utils.dp2px(getContext(), 16);
		lp.setMargins(margin, 0, margin, 0);
		v.setLayoutParams(lp);
		this.addView(v);
	}

	public void addFacultySeparator() {
		View v = new View(getContext());
		v.setBackgroundResource(R.color.background);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		int marginRight = Utils.dp2px(getContext(), 16);
		int marginLeft = Utils.dp2px(getContext(), 16);
		lp.setMargins(marginLeft, 0, marginRight, 0);
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
