package lecho.app.campus;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_place_details);
		// getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_button_normal));
		// XmlParser.loadCampusData(getApplicationContext());
		View v = findViewById(R.id.info_layout);
		View i = View.inflate(getApplicationContext(), R.layout.group_view, null);
		((LinearLayout) v).addView(i);
		View ii = View.inflate(getApplicationContext(), R.layout.item_view, null);
		((LinearLayout) v).addView(ii);
		View iii = View.inflate(getApplicationContext(), R.layout.item_view, null);
		((LinearLayout) v).addView(iii);
		View i2 = View.inflate(getApplicationContext(), R.layout.group_view, null);
		((LinearLayout) v).addView(i2);
		View i3 = View.inflate(getApplicationContext(), R.layout.item_view, null);
		((LinearLayout) v).addView(i3);
		View i4 = View.inflate(getApplicationContext(), R.layout.more_view, null);
		((LinearLayout) v).addView(i4);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
