package org.softevo.mutation.run.threaded;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.softevo.mutation.properties.MutationProperties;

/**
 * Class that provides a method to kill a process. This is don via an a script
 * that searches for the pid and sends a kill signal.
 *
 * @author David Schuler
 *
 */
public class KillProcess {

	private static Logger logger = Logger.getLogger(KillProcess.class);

	private static final String KILL_COMMAND = MutationProperties.EXEC_DIR
			+ "/kill-by-id.sh";

	/**
	 * Kill process with given task ID.
	 *
	 * @param taskID
	 *            Task ID of the process to kill.
	 */
	public static void killProcess(int taskID) {
		try {
			Process killProcess = Runtime.getRuntime().exec(
					getKillComand(taskID), new String[0]);
			InputStream is = killProcess.getInputStream();
			PipeThread killOutputPipe = new PipeThread(is, System.out);
			try {
				killOutputPipe.start();
				killProcess.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.warn("IoException thrown" + e + " Tried to execute"
					+ getKillComand(taskID));
			e.printStackTrace();
		}
	}

	private static String[] getKillComand(int taskId) {
		return new String[] { KILL_COMMAND, "" + taskId };
	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			killProcess(Integer.parseInt(args[0]));
		}
	}

}
