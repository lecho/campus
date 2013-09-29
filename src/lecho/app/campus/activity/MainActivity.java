package lecho.app.campus.activity;

import lecho.app.campus.R;
import lecho.app.campus.utils.Config;
import lecho.app.campus.utils.DataParser;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent i = new Intent(MainActivity.this, CampusMapActivity.class);
				// i.putExtra(Config.EXTRA_PLACE_ID, 1L);
				// startActivity(i);

				// Intent intent = new Intent(MainActivity.this, ProductGuideActivity.class);
				// startActivity(intent);
				// finish();

				Intent intent = new Intent(MainActivity.this, StartActivity.class);
				startActivity(intent);
				finish();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
