package lecho.app.campus.fragment.dialog;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PlayServicesErrorDialogFragment extends DialogFragment {
	private static final String EXTRA_PLAY_SERVICES_STATUS = "lecho.app.campus:PLAY_SERVICES_STATUS";
	private static final String EXTRA_REQUEST_CODE_RECOVER_PLAY_SERVICES = "lecho.app.campus:REQUEST_CODE_RECOVER_PLAY_SERVICES";

	public static PlayServicesErrorDialogFragment newInstance(int playServicesStatus, int requestCode) {
		PlayServicesErrorDialogFragment fragment = new PlayServicesErrorDialogFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_PLAY_SERVICES_STATUS, playServicesStatus);
		args.putInt(EXTRA_REQUEST_CODE_RECOVER_PLAY_SERVICES, requestCode);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return GooglePlayServicesUtil.getErrorDialog(getArguments().getInt(EXTRA_PLAY_SERVICES_STATUS), getActivity(),
				getArguments().getInt(EXTRA_REQUEST_CODE_RECOVER_PLAY_SERVICES));
	}
}
