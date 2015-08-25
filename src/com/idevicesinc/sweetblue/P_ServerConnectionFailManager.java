package com.idevicesinc.sweetblue;

import android.bluetooth.BluetoothDevice;

import com.idevicesinc.sweetblue.utils.Interval;

import java.util.ArrayList;
import java.util.HashMap;

class P_ServerConnectionFailManager
{
	private static BleServer.ConnectionFailListener DEFAULT_CONNECTION_FAIL_LISTENER = new BleServer.DefaultConnectionFailListener();

	final BleServer m_server;

	private BleServer.ConnectionFailListener m_connectionFailListener = DEFAULT_CONNECTION_FAIL_LISTENER;

	private final HashMap<String, P_ServerConnectionFailEntry> m_entries = new HashMap<>();

	P_ServerConnectionFailManager(final BleServer server)
	{
		m_server = server;
	}

	private P_ServerConnectionFailEntry getOrCreateEntry(final String macAddress)
	{
		final P_ServerConnectionFailEntry entry_nullable = m_entries.get(macAddress);

		if( entry_nullable != null )
		{
			return entry_nullable;
		}
		else
		{
			final P_ServerConnectionFailEntry entry = new P_ServerConnectionFailEntry(this);

			m_entries.put(macAddress, entry);

			return entry;
		}
	}

	void onExplicitDisconnect(final String macAddress)
	{
		getOrCreateEntry(macAddress).onExplicitDisconnect();
	}

	void onExplicitConnectionStarted(final String macAddress)
	{
		getOrCreateEntry(macAddress).onExplicitConnectionStarted();
	}

	public void setListener(BleServer.ConnectionFailListener listener)
	{
		m_connectionFailListener = listener;
	}

	public BleServer.ConnectionFailListener getListener()
	{
		return m_connectionFailListener;
	}

	void onNativeConnectFail(final BluetoothDevice nativeDevice, final BleServer.ConnectionFailListener.Status status, final int gattStatus)
	{
		getOrCreateEntry(nativeDevice.getAddress()).onNativeConnectFail(nativeDevice, status, gattStatus);
	}

	BleServer.ConnectionFailListener.Please.PE_Please invokeCallback(final BleServer.ConnectionFailListener.ConnectionFailEvent e)
	{
		final BleServer.ConnectionFailListener.Please.PE_Please ePlease;

		if( m_connectionFailListener != null )
		{
			final BleServer.ConnectionFailListener.Please please = m_connectionFailListener.onEvent(e);

			ePlease = please != null ? please.please() : BleServer.ConnectionFailListener.Please.PE_Please.DO_NOT_RETRY;
		}
		else
		{
			final BleServer.ConnectionFailListener.Please please = m_server.getManager().m_defaultConnectionFailListener_server.onEvent(e);

			ePlease = please != null ? please.please() : BleServer.ConnectionFailListener.Please.PE_Please.DO_NOT_RETRY;
		}

		return ePlease;
	}
}
