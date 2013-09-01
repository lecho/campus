package lecho.app.campus.fragment;

import lecho.app.campus.R;
import lecho.app.campus.activity.PlaceDetailsActivity;
import lecho.app.campus.utils.Config;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SearchResultFragment extends SherlockFragment {
	private static final String ARG_ID = "lecho.app.campus:ID";
	private static final String ARG_SYMBOL = "lecho.app.campus:SYMBOL";
	private static final String ARG_NAME = "lecho.app.campus:NAME";
	private static final String ARG_DESCRIPTION = "lecho.app.campus:DESCRIPTION";

	public static SearchResultFragment newInstance(Long id, String symbol, String name, String description) {
		SearchResultFragment fragment = new SearchResultFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_ID, id);
		args.putString(ARG_SYMBOL, symbol);
		args.putString(ARG_NAME, name);
		args.putString(ARG_DESCRIPTION, description);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_result, container, false);
		TextView tvSymbol = (TextView) view.findViewById(R.id.symbol);
		TextView tvName = (TextView) view.findViewById(R.id.name);
		TextView tvDescription = (TextView) view.findViewById(R.id.description);

		String symbol = getArguments().getString(ARG_SYMBOL);
		String name = getArguments().getString(ARG_NAME);
		String description = getArguments().getString(ARG_DESCRIPTION);

		tvSymbol.setText(symbol);

		if (TextUtils.isEmpty(description)) {
			tvName.setSingleLine(false);
			tvName.setMaxLines(2);
			tvName.setText(name);
			tvDescription.setVisibility(View.GONE);
		} else {
			tvName.setSingleLine(true);
			tvName.setMaxLines(1);
			tvName.setText(name);
			tvDescription.setVisibility(View.VISIBLE);
			tvDescription.setText(description);
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), PlaceDetailsActivity.class);
				i.putExtra(Config.ARG_PLACE_ID, 1L);
				startActivity(i);
			}
		});
		return view;
	}

}
