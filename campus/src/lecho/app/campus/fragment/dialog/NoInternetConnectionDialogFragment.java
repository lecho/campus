package lecho.app.campus.fragment.dialog;

import lecho.app.campus.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;

public class NoInternetConnectionDialogFragment extends DialogFragment {

	public static NoInternetConnectionDialogFragment newInstance() {
		NoInternetConnectionDialogFragment fragment = new NoInternetConnectionDialogFragment();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.connection_no_internet);
		builder.setNegativeButton(R.string.connection_dont_go_to_settings, null);
		builder.setPositiveButton(R.string.connection_go_to_settings, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Settings.ACTION_SETTINGS));
			}

		});
		Dialog dialog = builder.create();
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}
}
