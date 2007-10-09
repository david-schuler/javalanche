package org.softevo.mutation.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessStarter implements Runnable {

	private static class PipeThread extends Thread {

		private static final int BUFFER_SIZE = 1024;

		private final InputStream is;

		private final OutputStream os;

		private byte[] buffer;

		private boolean running;

		public PipeThread(InputStream is) {
			this(is, System.out);
		}

		public PipeThread(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
			buffer = new byte[BUFFER_SIZE];
		}

		@Override
		public void run() {
			synchronized (this) {
				running = true;
			}
			super.run();
			System.out.println("Pipe started");
			try {
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Pipe ended");
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

	private String command;

	private String[] args;

	private File dir;

	private Process process;

	private boolean running;

	private int exitvalue;

	private final File outputFile;

	public ProcessStarter(String command, String[] args, File dir,
			File outputFile) {
		super();
		this.command = command;
		this.args = args;
		this.dir = dir;
		this.outputFile = outputFile;
		System.out.println("Process created" + this);
	}

	public void run() {
		System.out.println("Process started" + this);
		try {
			process = Runtime.getRuntime().exec(command, args, dir);
			running = true;
			InputStream is = process.getInputStream();
			FileOutputStream fw = new FileOutputStream(outputFile);

			PipeThread pt = new PipeThread(is, fw);
			PipeThread pt2 = new PipeThread(process.getErrorStream());
			pt.start();
			pt2.start();
			process.waitFor();
			pt.setRunning(false);
			pt2.setRunning(false);
			// is.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		exitvalue = process.exitValue();
		running = false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Command" + command);
		sb.append('\n');
		sb.append("Output File" + outputFile.getAbsolutePath());
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * @return the exit value
	 */
	public int getExitvalue() {
		return exitvalue;
	}

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return running;
	}

	public static void main(String[] args) {
		String cmd = "/scratch/schuler/mutationTest/src/scripts/run-tests.sh";
		// String cmd = "java";

		ProcessStarter ps = new ProcessStarter(cmd, new String[] {}, new File(
				"/scratch/schuler/mutationTest/src/scripts/"), new File(
				"processoutput.txt"));
		Thread t = new Thread(ps);
		t.start();
	}
}
