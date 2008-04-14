package org.softevo.mutation.run.threaded;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * Thread that forwards the output of a {@link InputStream} to an
 * {@link OutputStream}.
 *
 * @author David Schuler
 *
 */
/**
 * @author David Schuler
 *
 */
class PipeThread extends Thread {

	/**
	 * Size of the buffer
	 */
	private static final int BUFFER_SIZE = 1024;

	private static final boolean DEBUG = false;

	private final InputStream is;

	private final OutputStream os;

	private byte[] buffer;

	private boolean running;

	private static volatile int pipeIdCounter;

	private int pipeID;

	private OutputStream sysout = System.out;

	public PipeThread(InputStream is) {
		this(is, System.out);
	}

	public PipeThread(InputStream is, OutputStream os) {
		setDaemon(true);
		this.is = is;
		this.os = os;
		pipeID = pipeIdCounter++;
		buffer = new byte[BUFFER_SIZE];
	}

	@Override
	public void run() {
		super.run();
		synchronized (this) {
			running = true;
		}
		System.out.println("Pipe started " + pipeID + " Debug: " + DEBUG);
		try {
			int bytesRead;
			while (running == true && is != null
					&& (bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
				if (DEBUG) {
					sysout.write(buffer, 0, bytesRead);
				}
			}
			synchronized (is) {
				for (int i = 0; i < 10; i++) {
					if (is != null && (bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Exception in pipe " + pipeID);
			e.printStackTrace();
		}
		try {
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Pipe ended " + pipeID);
	}

	/**
	 * @return True, if pipe should be running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Return the ID of the pipe.
	 *
	 * @return the ID of the pipe
	 */
	public int getPipeID() {
		return pipeID;
	}

	/**
	 * Stop the Pipe.
	 */
	public synchronized void stopPipe() {
		this.running = false;
	}
}