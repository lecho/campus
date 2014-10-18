package lecho.app.campus.view;

import lecho.app.campus.R;
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
		setBackgroundResource(R.drawable.white_box_shadow);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int hMargin = getResources().getDimensionPixelSize(R.dimen.place_details_group_horizontal_margin);
		int vMargin = getResources().getDimensionPixelSize(R.dimen.place_details_group_vertical_margin);
		lp.setMargins(hMargin, vMargin, hMargin, vMargin);
		setLayoutParams(lp);
	}

	public void setFaculty(String facultyName) {
		TextView tv = new TextView(getContext());
		tv.setText(facultyName);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int hMargin = getResources().getDimensionPixelSize(R.dimen.place_details_faculty_horizontal_margin);
		int vMargin = getResources().getDimensionPixelSize(R.dimen.place_details_faculty_vertical_margin);
		lp.setMargins(hMargin, vMargin, hMargin, vMargin);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.RIGHT);
		tv.setTextAppearance(getContext(), android.R.attr.textAppearanceSmall);
		tv.setTextColor(getResources().getColor(R.color.black));
		tv.setTypeface(null, Typeface.BOLD);
		this.addView(tv);
		mHasFaculty = true;
		addSeparator();

	}

	public void addUnit(String unitName) {
		TextView tv = new TextView(getContext());
		tv.setText(unitName);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int hMargin = getResources().getDimensionPixelSize(R.dimen.place_details_unit_horizontal_margin);
		int vMargin = getResources().getDimensionPixelSize(R.dimen.place_details_unit_vertical_margin);
		lp.setMargins(hMargin, vMargin, hMargin, vMargin);
		tv.setLayoutParams(lp);
		tv.setGravity(Gravity.LEFT);
		tv.setTextAppearance(getContext(), android.R.attr.textAppearanceSmall);
		tv.setTextColor(getContext().getResources().getColor(R.color.black));
		this.addView(tv);
		++mUnitsCount;
	}

	public void addSeparator() {
		View v = new View(getContext());
		v.setBackgroundResource(R.color.background);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		int hMargin = getResources().getDimensionPixelSize(R.dimen.place_details_unit_horizontal_margin);
		lp.setMargins(hMargin, 0, hMargin, 0);
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
