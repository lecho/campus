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

public class AboutAppActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_app);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		TextView version = (TextView) findViewById(R.id.version);
		TextView build = (TextView) findViewById(R.id.build);
		Pair<String, Integer> versionAndBuild = Utils.getAppVersionAndBuild(getApplicationContext());
		version.setText(versionAndBuild.first);
		build.setText(Integer.toString(versionAndBuild.second));

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
