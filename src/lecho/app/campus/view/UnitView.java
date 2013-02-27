package lecho.app.campus.view;

import lecho.app.campus.R;
import lecho.app.campus.utils.Utils;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @author lecho
 * 
 */
public class UnitView extends LinearLayout {

    EditText mEditText;
    Button mButton;
    View mButtonLayout;

    public UnitView(Context context) {
        super(context);
        View.inflate(context, R.layout.faculty_view, this);
        initView();
        mButton.setVisibility(View.INVISIBLE);
    }

    public UnitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.faculty_view, this);
        initView();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UnitView, 0, 0);
        try {
            mEditText.setText(a.getString(R.styleable.UnitView_unitName));
            final String url = a.getString(R.styleable.UnitView_unitWebPage);
            setWebPage(url);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.text);
        mButton = (Button) findViewById(R.id.button_layout);
        mButtonLayout = findViewById(R.id.button);
    }

    public void setText(final String text) {
        mEditText.setText(text);
    }

    public void setText(final int textId) {
        mEditText.setText(textId);
    }

    private void setWebPage(final String url) {
        if (TextUtils.isEmpty(url)) {
            mButtonLayout.setVisibility(View.INVISIBLE);
        } else {
            mButton.setOnClickListener(new UnitButtonListener(url));
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    private static class UnitButtonListener implements View.OnClickListener {

        String mUrl;

        public UnitButtonListener(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View v) {
            Utils.launchWebBrowser(v.getContext(), mUrl);
        }

    }

}
