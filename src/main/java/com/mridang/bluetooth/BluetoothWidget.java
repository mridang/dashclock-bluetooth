package com.mridang.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;

import com.google.android.apps.dashclock.api.ExtensionData;

import org.acra.ACRA;

/*
 * This class is the main class that provides the widget
 */
public class BluetoothWidget extends ImprovedExtension {

	/**
	 * A boolean value indicating the status of the bluetooth connectivity
	 */
	private Boolean booConnected = false;

	/*
	 * (non-Javadoc)
	 * @see com.mridang.bluetooth.ImprovedExtension#getIntents()
	 */
	@Override
	protected IntentFilter getIntents() {

		IntentFilter itfIntents = new IntentFilter((BluetoothAdapter.ACTION_STATE_CHANGED));
		itfIntents.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		itfIntents.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		return itfIntents;

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.bluetooth.ImprovedExtension#getTag()
	 */
	@Override
	protected String getTag() {
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.bluetooth.ImprovedExtension#getUris()
	 */
	@Override
	protected String[] getUris() {
		return null;
	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	protected void onUpdateData(int intReason) {

		Log.d(getTag(), "Fetching near-field communication information");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(true);

		try {

			BluetoothAdapter bluAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluAdapter != null) {

				Log.d(getTag(), "Checking if the bluetooth radio is enabled");
				if (bluAdapter.isEnabled()) {

					Log.d(getTag(), "Bluetooth radio is enabled");
					if (booConnected) {
						edtInformation.expandedTitle(getString(R.string.device_connected));
					} else {
						edtInformation.expandedTitle(getString(R.string.not_connected));
					}

					edtInformation.visible(true);
					edtInformation.status(getString(R.string.enabled));
					edtInformation.expandedBody(getString(R.string.visible_as, bluAdapter.getName()));
					edtInformation.clickIntent(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

				} else {

					Log.d(getTag(), "Bluetooth radio is disabled");
					edtInformation.visible(getBoolean("always", true));
					edtInformation.status(getString(R.string.disabled));
					edtInformation.expandedTitle(getString(R.string.not_enabled));
					edtInformation.expandedBody(getString(R.string.tap_enable, bluAdapter.getName()));

				}

			} else {
				Log.d(getTag(), "Device doesn't have a bluetooth radio");
			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e(getTag(), "Encountered an error", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		doUpdate(edtInformation);

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.bluetooth.ImprovedExtension#onReceiveIntent(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void onReceiveIntent(Context ctxContext, Intent ittIntent) {

		Log.d(getTag(), "Received an intent about the bluetooth state");
		if (ittIntent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {

			Integer intState = ittIntent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
			if (intState == BluetoothAdapter.STATE_CONNECTED) {

				Log.v(getTag(), "Bluetooth connected");
				booConnected = true;

			} else if (intState == BluetoothAdapter.STATE_DISCONNECTED) {

				Log.v(getTag(), "Bluetooth disconnected");
				booConnected = false;

			}

		}

		onUpdateData(UPDATE_REASON_MANUAL);

	}

}