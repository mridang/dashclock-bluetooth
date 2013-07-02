package com.mridang.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class BluetoothWidget extends DashClockExtension {

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("BluetoothWidget", "Created");
		BugSenseHandler.initAndStartSession(this, "17259530");

	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	protected void onUpdateData(int arg0) {

		setUpdateWhenScreenOn(true);

		Log.d("BluetoothWidget", "Fetching bluetooth connectivity information");
		ExtensionData edtInformation = new ExtensionData();
		edtInformation.visible(false);

		try {

			Log.d("BluetoothWidget", "Checking if the bluetooth is on");
			if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {

				Log.d("BluetoothWidget", "Bluetooth is on");
				edtInformation.visible(true);
				edtInformation.clickIntent(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
				edtInformation.status(BluetoothAdapter.getDefaultAdapter().getName());
				edtInformation.expandedBody(BluetoothAdapter.getDefaultAdapter().getAddress());

			} else {
				Log.d("BluetoothWidget", "Bluetooth is off");
			}

		} catch (Exception e) {
			Log.e("BluetoothWidget", "Encountered an error", e);
			BugSenseHandler.sendException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("BluetoothWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();
		Log.d("BluetoothWidget", "Destroyed");
		BugSenseHandler.closeSession(this);

	}

}