package lecho.app.campus.view;

import lecho.app.campus.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheckableDrawerItem extends RelativeLayout implements Checkable {

	private boolean mIsChecked = false;
	private TextView mTextView;
	private ImageView mImageView;

	public CheckableDrawerItem(Context context) {
		super(context);
	}

	public CheckableDrawerItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableDrawerItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTextView = (TextView) findViewById(R.id.text);
		mImageView = (ImageView) findViewById(R.id.checkmark);
	}

	@Override
	public boolean isChecked() {
		return mIsChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		mIsChecked = checked;
		toggleBackground();
	}

	@Override
	public void toggle() {
		mIsChecked = !mIsChecked;
		toggleBackground();
	}

	private void toggleBackground() {
		if (mIsChecked) {
			mTextView.setTextColor(getContext().getResources().getColor(R.color.holo_blue_dark));
			if (null != mImageView) {
				mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_checkmark_on));
			}
		} else {
			mTextView.setTextColor(getContext().getResources().getColor(R.color.dark_text));
			if (null != mImageView) {
				mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_checkmark_off));
			}
		}
	}
}
