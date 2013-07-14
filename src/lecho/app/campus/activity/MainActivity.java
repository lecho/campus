package lecho.app.campus.activity;

import lecho.app.campus.R;
import lecho.app.campus.R.layout;
import lecho.app.campus.R.menu;
import lecho.app.campus.R.raw;
import lecho.app.campus.utils.DataParser;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DataParser.loadCampusData(getApplicationContext(), R.raw.campus_data_pl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
