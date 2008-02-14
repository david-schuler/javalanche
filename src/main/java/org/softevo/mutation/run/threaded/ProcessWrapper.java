package org.softevo.mutation.run.threaded;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.runtime.RunResult;

/**
 *
 * This class wraps around a process that is started via {@link Runtime}.exec().
 *
 * @author David Schuler
 *
 */
public class ProcessWrapper extends Thread {

	private static Logger logger = Logger.getLogger(ProcessWrapper.class);

	private static final String KILL_COMMAND = MutationProperties.EXEC_DIR
			+ "/kill-by-id.sh";

	private String command;

	private File dir;

	private Process process;

	private boolean running;

	private boolean finished;

	private int exitvalue;

	private final File outputFile;

	private final File resultFile;

	public long startTime;

	private PipeThread errorPipe;

	private PipeThread outputPipe;

	private RunResult runResult;

	private File taskFile;

	private int debugPort;

	private int taskId;

	private String instanceDir;

	private InstanceManager instances;

	public ProcessWrapper(String command, File taskFile, File dir,
			File outputFile, File resultFile, int taskId) {
		this(command, taskFile, dir, outputFile, resultFile, taskId, null);
	}

	/**
	 * Construct a new ProcessWrapper.
	 *
	 * @param command
	 *            The actual command
	 * @param taskFile
	 *            Arguments of the command
	 * @param dir
	 *            Directory where the command is executed
	 * @param outputFile
	 *            The file to which the output of the process is written
	 * @param resultFile
	 *            The result file the tasks writes its mutation results to.
	 */
	public ProcessWrapper(String command, File taskFile, File dir,
			File outputFile, File resultFile, int taskId,
			InstanceManager instances) {
		super();
		this.taskId = taskId;
		this.debugPort = 1045 + taskId;
		this.command = command;
		this.taskFile = taskFile;
		this.dir = dir;
		this.outputFile = outputFile;
		this.resultFile = resultFile;
		this.instances = instances;
		System.out.println("Process created" + this);
	}

	/**
	 * Starts the underlying process.
	 *
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		logger.info("Process started:\n" + this);
		try {
			if (instances != null) {
				while (instanceDir == null) {
					instanceDir = instances.getInstance();
					sleep(5000);
				}
			}
			String[] cmdArray = getCommand();
			process = Runtime.getRuntime().exec(cmdArray, new String[0], dir);
			logger.info(Arrays.toString(cmdArray));
			running = true;
			startTime = System.currentTimeMillis();
			InputStream is = process.getInputStream();
			FileOutputStream fw = new FileOutputStream(outputFile);
			outputPipe = new PipeThread(is, fw);
			errorPipe = new PipeThread(process.getErrorStream());
			outputPipe.start();
			errorPipe.start();
			process.waitFor();
			if (instances != null) {
				instances.addInstance(instanceDir);
			}
			closePipes();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		exitvalue = process.exitValue();
		end();
	}

	private void end() {
		running = false;
		finished = true;
	}

	/**
	 * Return the command to start the process.
	 *
	 * @return The command to start the process.
	 */
	private String[] getCommand() {
		List<String> commandList = new ArrayList<String>(5);
		commandList.add(command);
		String resultFileOption = getPropertyParameter(
				MutationProperties.RESULT_FILE_KEY, resultFile
						.getAbsolutePath());
		commandList.add(resultFileOption);
		String taskFileOption = getPropertyParameter(
				MutationProperties.MUTATION_FILE_KEY, taskFile
						.getAbsolutePath());
		commandList.add(taskFileOption);
		String debugPortOption = getPropertyParameter(
				MutationProperties.DEBUG_PORT_KEY, debugPort);
		commandList.add(debugPortOption);
		if (instances != null) {
			commandList.add(instanceDir);
		}
		return commandList.toArray(new String[commandList.size()]);
	}

	/**
	 * Return the key value pair as java commandline property argument in the
	 * form -Dkey=value.
	 *
	 * @param key
	 *            the key of the property
	 * @param value
	 *            the value of the property
	 * @return key value pair as Java commandline argument
	 */
	private String getPropertyParameter(String key, Object value) {
		return "-D" + key + "=" + value.toString();
	}

	private void closePipes() {
		logger.info("shutting down pipe" + outputPipe.getPipeID());
		closePipe(outputPipe);
		logger.info("shutting down pipe" + errorPipe.getPipeID());
		closePipe(errorPipe);
	}

	/**
	 * Close the pipe and wait for it to finish.
	 *
	 * @param pipeThread
	 *            The {@link PipeThread} that should be closed.
	 */
	private void closePipe(PipeThread pipeThread) {
		if (pipeThread != null) {
			pipeThread.stopPipe();
			try {
				pipeThread.join(100 * 1000);
			} catch (InterruptedException e) {
				logger.warn("Exception thrown when trying to close pipe. "
						+ e.getMessage() + "\n" + e.getStackTrace().toString());
				e.printStackTrace();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Command" + command);
		sb.append('\n');
		sb.append("Output File: " + outputFile.getAbsolutePath());
		sb.append('\n');
		sb.append("Task File: " + taskFile.getAbsolutePath());
		sb.append('\n');
		sb.append("Command: " + Arrays.toString(getCommand()));
		sb.append('\n');
		sb.append("Aspectj Dir: " + instanceDir);
		return sb.toString();
	}

	/**
	 * @return The exit value of the process.
	 */
	public int getExitvalue() {
		return exitvalue;
	}

	/**
	 * @return True, if the process is running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Read the result file that is written by the mutation testing.
	 *
	 * @return
	 */
	public RunResult getRunResult() {
		if (finished && runResult == null) {
			if (resultFile.exists()) {
				runResult = (RunResult) XmlIo.fromXml(resultFile);
			} else {
				logger.info("File " + resultFile + " does not exist");
			}
		}
		return runResult;

	}

	/**
	 * Return the time this process is running in milliseconds.
	 *
	 * @return The time this process is running in milliseconds.
	 */
	public long getTimeRunnning() {
		if (startTime > 0l) {
			return System.currentTimeMillis() - startTime;
		}
		return 0l;
	}

	/**
	 * @return True, if the process has finished.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Destroys the underlying process.
	 */
	public void destroyProcess() {
		logger.info("Destroying Process" + this);
		closePipes();
		logger.info("Pipes closed");
		logger.info("Trying to destroy sub process");
		process.destroy();
		logger.info("Sub process destroyed");
		try {
			logger.info("exit value: " + process.exitValue());
		} catch (IllegalThreadStateException e) {
			logger.info("could not get exit value" + e.getMessage());
		}
		try {
			sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		killProcess();
		try {
			sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		end();
	}

	private void killProcess() {
		KillProcess.killProcess(taskId);
	}

}
