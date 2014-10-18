package lecho.app.campus.activity;

import lecho.app.campus.R;
import lecho.app.campus.utils.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Displays informations about app - author, version, legal info.
 * 
 * @author Lecho
 * 
 */
public class AboutAppActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		TextView version = (TextView) findViewById(R.id.version);
		Pair<String, Integer> versionAndBuild = Utils.getAppVersionAndBuild(getApplicationContext());
		version.setText(versionAndBuild.first);

		TextView playServicesLink = (TextView) findViewById(R.id.legal_info_play_services);
		playServicesLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutAppActivity.this, LegalInfoActivity.class);
				intent.putExtra(LegalInfoActivity.EXTRA_LEGAL_INFO_LIBRARY,
						LegalInfoActivity.EXTRA_LEGAL_INFO_PLAY_SERVICES);
				startActivity(intent);
			}
		});

		TextView absLink = (TextView) findViewById(R.id.legal_info_abs);
		absLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutAppActivity.this, LegalInfoActivity.class);
				intent.putExtra(LegalInfoActivity.EXTRA_LEGAL_INFO_LIBRARY,
						LegalInfoActivity.EXTRA_LEGAL_INFO_ACTIONBARSHERLOCK);
				startActivity(intent);
			}
		});

		TextView vpiLink = (TextView) findViewById(R.id.legal_info_vpi);
		vpiLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutAppActivity.this, LegalInfoActivity.class);
				intent.putExtra(LegalInfoActivity.EXTRA_LEGAL_INFO_LIBRARY,
						LegalInfoActivity.EXTRA_LEGAL_INFO_VIEWPAGERINDICATOR);
				startActivity(intent);
			}
		});

		TextView mbLink = (TextView) findViewById(R.id.legal_info_mb);
		mbLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutAppActivity.this, LegalInfoActivity.class);
				intent.putExtra(LegalInfoActivity.EXTRA_LEGAL_INFO_LIBRARY,
						LegalInfoActivity.EXTRA_LEGAL_INFO_MESSAGEBAR);
				startActivity(intent);
			}
		});

		TextView zivLink = (TextView) findViewById(R.id.legal_info_ziv);
		zivLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutAppActivity.this, LegalInfoActivity.class);
				intent.putExtra(LegalInfoActivity.EXTRA_LEGAL_INFO_LIBRARY,
						LegalInfoActivity.EXTRA_LEGAL_INFO_ZOOMIMAGEVIEW);
				startActivity(intent);
			}
		});
		
		TextView gdLink = (TextView) findViewById(R.id.legal_info_gd);
		gdLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutAppActivity.this, LegalInfoActivity.class);
				intent.putExtra(LegalInfoActivity.EXTRA_LEGAL_INFO_LIBRARY,
						LegalInfoActivity.EXTRA_LEGAL_INFO_GREENDAO);
				startActivity(intent);
			}
		});
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
}
