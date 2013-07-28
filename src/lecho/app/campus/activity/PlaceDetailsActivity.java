package lecho.app.campus.activity;

import lecho.app.campus.fragment.PlaceDetailsFragment;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PlaceDetailsActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		PlaceDetailsFragment fragment = PlaceDetailsFragment.newInstance(1L);
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
	}

}
