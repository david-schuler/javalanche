
package org.softevo.mutation.run.threaded;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class PipeThread extends Thread {

	private static final int BUFFER_SIZE = 1024;

	private final InputStream is;

	private final OutputStream os;

	private byte[] buffer;

	private boolean running;

	private static volatile int pipeIdCounter;

	private int pipeID;

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
		System.out.println("Pipe started " + pipeID);
		try {
			int bytesRead;
			while (running == true && is != null
					&& (bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			if (is != null) {
				if ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
			}
		} catch (IOException e) {
			System.out.println("Exception in pipe " + pipeID);
			e.printStackTrace();
		}
		System.out.println("Pipe ended " + pipeID);
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}
}