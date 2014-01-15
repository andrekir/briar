package org.briarproject.messaging.simplex;

import static org.briarproject.api.transport.TransportConstants.MAX_FRAME_LENGTH;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.briarproject.api.plugins.simplex.SimplexTransportWriter;

class TestSimplexTransportWriter implements SimplexTransportWriter {

	private final ByteArrayOutputStream out;
	private final long capacity, maxLatency;

	private boolean disposed = false, exception = false;

	TestSimplexTransportWriter(ByteArrayOutputStream out, long capacity,
			long maxLatency) {
		this.out = out;
		this.capacity = capacity;
		this.maxLatency = maxLatency;
	}

	public long getCapacity() {
		return capacity;
	}

	public int getMaxFrameLength() {
		return MAX_FRAME_LENGTH;
	}

	public long getMaxLatency() {
		return maxLatency;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public void dispose(boolean exception) {
		assert !disposed;
		disposed = true;
		this.exception = exception;
	}

	boolean getDisposed() {
		return disposed;
	}

	boolean getException() {
		return exception;
	}
}