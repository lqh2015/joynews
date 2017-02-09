package org.common.lib.analytics;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by liuqing on 2015/4/16.
 */
public abstract class AnalyticBaseDialogFragment extends DialogFragment implements AnalyticCallback {

	private FragmentLifecycleAction fragmentLifecycleAction = new FragmentLifecycleAction(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentLifecycleAction.onCreate(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		fragmentLifecycleAction.onStart(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		fragmentLifecycleAction.onResume(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		fragmentLifecycleAction.onPause(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		fragmentLifecycleAction.onStop(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		fragmentLifecycleAction.onDestroy(this);
	}

}