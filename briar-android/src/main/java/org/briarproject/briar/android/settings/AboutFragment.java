package org.briarproject.briar.android.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.briar.BuildConfig;
import org.briarproject.briar.R;

import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.widget.Toast.LENGTH_LONG;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;
import static org.briarproject.bramble.util.LogUtils.logException;

@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class AboutFragment extends Fragment {

	final static String TAG = AboutFragment.class.getName();
	private static final Logger LOG = getLogger(TAG);

	private TextView briarVersion;
	private TextView briarWebsite;
	private TextView briarSourceCode;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_about, container,
				false);
	}

	@Override
	public void onStart() {
		super.onStart();
		requireActivity().setTitle(R.string.about_title);
		briarVersion = requireActivity().findViewById(R.id.BriarVersion);
		briarVersion.setText(
				getString(R.string.briar_version, BuildConfig.VERSION_NAME));
		briarWebsite = requireActivity().findViewById(R.id.BriarWebsite);
		briarSourceCode = requireActivity().findViewById(R.id.BriarSourceCode);
		briarWebsite.setOnClickListener(View -> {
			String url = "https://briarproject.org/";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			try {
				startActivity(i);
			} catch (ActivityNotFoundException e) {
				logException(LOG, WARNING, e);
				Toast.makeText(requireContext(),
						R.string.error_start_activity, LENGTH_LONG).show();
			}
		});
		briarSourceCode.setOnClickListener(View -> {
			String url = "https://code.briarproject.org/briar/briar";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			try {
				startActivity(i);
			} catch (ActivityNotFoundException e) {
				logException(LOG, WARNING, e);
				Toast.makeText(requireContext(),
						R.string.error_start_activity, LENGTH_LONG).show();
			}
		});
	}

}