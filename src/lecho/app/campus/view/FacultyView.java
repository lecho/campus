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
public class FacultyView extends LinearLayout {

    EditText mEditText;
    Button mButton;
    View mButtonLayout;

    public FacultyView(Context context) {
        super(context);
        View.inflate(context, R.layout.faculty_view, this);
        initView();
        mButton.setVisibility(View.INVISIBLE);
    }

    public FacultyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.faculty_view, this);
        initView();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FacultyView, 0, 0);
        try {
            mEditText.setText(a.getString(R.styleable.FacultyView_facultyName));
            final String url = a.getString(R.styleable.FacultyView_facultyWebPage);
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
            mButton.setOnClickListener(new FacultyButtonListener(url));
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    private static class FacultyButtonListener implements View.OnClickListener {

        String mUrl;

        public FacultyButtonListener(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View v) {
            Utils.launchWebBrowser(v.getContext(), mUrl);
        }

    }

}
