package org.briarproject.plugins.tor;

import android.content.Context;
import android.os.Build;

import org.briarproject.android.util.AndroidUtils;
import org.briarproject.api.TransportId;
import org.briarproject.api.event.EventBus;
import org.briarproject.api.plugins.duplex.DuplexPlugin;
import org.briarproject.api.plugins.duplex.DuplexPluginCallback;
import org.briarproject.api.plugins.duplex.DuplexPluginFactory;
import org.briarproject.api.system.Clock;
import org.briarproject.api.system.LocationUtils;
import org.briarproject.system.SystemClock;

import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class TorPluginFactory implements DuplexPluginFactory {

	private static final Logger LOG =
			Logger.getLogger(TorPluginFactory.class.getName());

	private static final int MAX_LATENCY = 30 * 1000; // 30 seconds
	private static final int MAX_IDLE_TIME = 30 * 1000; // 30 seconds
	private static final int POLLING_INTERVAL = 2 * 60 * 1000; // 2 minutes

	private final Executor ioExecutor;
	private final Context appContext;
	private final LocationUtils locationUtils;
	private final EventBus eventBus;
	private final Clock clock;

	public TorPluginFactory(Executor ioExecutor, Context appContext,
			LocationUtils locationUtils, EventBus eventBus) {
		this.ioExecutor = ioExecutor;
		this.appContext = appContext;
		this.locationUtils = locationUtils;
		this.eventBus = eventBus;
		clock = new SystemClock();
	}

	public TransportId getId() {
		return TorPlugin.ID;
	}

	public int getMaxLatency() {
		return MAX_LATENCY;
	}

	public DuplexPlugin createPlugin(DuplexPluginCallback callback) {

		// Check that we have a Tor binary for this architecture
		String architecture = null;
		for (String abi : AndroidUtils.getSupportedArchitectures()) {
			if (abi.startsWith("x86")) {
				architecture = "x86";
				break;
			} else if (abi.startsWith("armeabi")) {
				architecture = "arm";
				break;
			}
		}
		if (architecture == null) {
			LOG.info("Tor is not supported on this architecture");
			return null;
		}
		// Use position-independent executable for SDK >= 16
		if (Build.VERSION.SDK_INT >= 16) architecture += "-pie";

		TorPlugin plugin = new TorPlugin(ioExecutor, appContext, locationUtils,
				clock, callback, architecture, MAX_LATENCY, MAX_IDLE_TIME,
				POLLING_INTERVAL);
		eventBus.addListener(plugin);
		return plugin;
	}
}
