package org.common.lib.analytics;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public abstract class AnalyticBasePreferenceActivity extends PreferenceActivity implements AnalyticCallback{

	private ActivityLifecycleAction action = new ActivityLifecycleAction(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action.onCreate(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		action.onStart(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		action.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		action.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		action.onStop(this);
	}
}