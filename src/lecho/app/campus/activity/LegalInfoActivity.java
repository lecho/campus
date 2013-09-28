package lecho.app.campus.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lecho.app.campus.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LegalInfoActivity extends SherlockActivity {
	private static final String TAG = "LegalInfoActivity";
	public static final String EXTRA_LEGAL_INFO_LIBRARY = "lecho.app.campus:LEGAL_INFO_LIBRARY";
	public static final int EXTRA_LEGAL_INFO_PLAY_SERVICES = 1;
	public static final int EXTRA_LEGAL_INFO_ACTIONBARSHERLOCK = 2;
	public static final int EXTRA_LEGAL_INFO_VIEWPAGERINDICATOR = 3;
	public static final int EXTRA_LEGAL_INFO_MESSAGEBAR = 4;
	public static final int EXTRA_LEGAL_INFO_ZOOMIMAGEVIEW = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_legal_info);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		TextView legalInfo = (TextView) findViewById(R.id.legal_info);
		String legalInfoText = "";
		int legalInfoLibrary = getIntent().getIntExtra(EXTRA_LEGAL_INFO_LIBRARY, 0);
		switch (legalInfoLibrary) {
		case EXTRA_LEGAL_INFO_PLAY_SERVICES:
			String openSourceSoftwareLicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
			if (openSourceSoftwareLicenseInfo != null) {
				legalInfoText = openSourceSoftwareLicenseInfo;
			} else {
				legalInfoText = getResources().getString(R.string.about_play_services_missing);
			}
			break;
		case EXTRA_LEGAL_INFO_ACTIONBARSHERLOCK:
			legalInfoText = readLicence(R.raw.licence_abs);
			break;
		case EXTRA_LEGAL_INFO_VIEWPAGERINDICATOR:
			legalInfoText = readLicence(R.raw.licence_vpi);
			break;
		case EXTRA_LEGAL_INFO_MESSAGEBAR:
			legalInfoText = readLicence(R.raw.licence_mb);
			break;
		case EXTRA_LEGAL_INFO_ZOOMIMAGEVIEW:
			legalInfoText = readLicence(R.raw.licence_ziv);
			break;
		}

		legalInfo.setText(legalInfoText);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private String readLicence(int rawResource) {
		InputStream in = getResources().openRawResource(rawResource);
		InputStreamReader inReader = new InputStreamReader(in);
		BufferedReader bInReader = new BufferedReader(inReader);
		try {
			StringBuilder text = new StringBuilder();
			String line = null;
			while ((line = bInReader.readLine()) != null) {
				text.append(line);
				text.append("\n");
			}
			return text.toString();
		} catch (IOException e) {
			Log.e(TAG, "Could not read licence", e);
			return "";
		} finally {
			try {
				bInReader.close();
			} catch (IOException e) {
				Log.e(TAG, "Could not close licence stream reader", e);
			}
		}
	}
}
