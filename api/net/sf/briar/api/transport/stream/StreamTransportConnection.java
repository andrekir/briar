package net.sf.briar.api.transport.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface for reading and writing data over a stream-mode transport. The
 * connection is not responsible for encrypting/decrypting or authenticating
 * the data.
 */
public interface StreamTransportConnection {

	/** Returns an input stream for reading from the connection. */
	InputStream getInputStream() throws IOException;

	/** Returns an output stream for writing to the connection. */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Finishes using the transport. This method should be called after closing
	 * the input and output streams.
	 */
	void finish() throws IOException;

	/**
	 * Disposes of any associated state. This method must be called even if the
	 * connection is not used, or if an exception is thrown while using the
	 * connection.
	 */
	void close() throws IOException;
}
