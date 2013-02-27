package lecho.app.campus.view;

import lecho.app.campus.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @author lecho
 * 
 */
public class MoreInfoView extends LinearLayout {

    EditText mEditText;

    public MoreInfoView(Context context) {
        super(context);
        View.inflate(context, R.layout.faculty_view, this);
        initView();
    }

    public MoreInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.faculty_view, this);
        initView();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MoreInfoView, 0, 0);
        try {
            mEditText.setText(a.getString(R.styleable.MoreInfoView_moreInfoText));
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.text);
    }

    public void setText(final String text) {
        mEditText.setText(text);
    }

    public void setText(final int textId) {
        mEditText.setText(textId);
    }

}
