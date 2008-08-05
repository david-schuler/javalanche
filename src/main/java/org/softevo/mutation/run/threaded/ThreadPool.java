package org.softevo.mutation.run.threaded;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.softevo.mutation.io.XmlIo;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.run.threaded.task.MutationTask;
import org.softevo.mutation.runtime.RunResult;
import org.softevo.mutation.util.Formater;
import org.softevo.mutation.util.Util;

/**
 * Class executes several instances of the mutation test tool in parallel using
 * the underlying thread pool.
 *
 * @author David Schuler
 *
 */
public class ThreadPool {

	public static Logger logger = Logger.getLogger(ThreadPool.class);

	/**
	 * Time interval when the processes are checked.
	 */
	private static final int CHECK_PERIOD = 60;

	/**
	 * Number of parallel running threads.
	 */
	private static final int NUMBER_OF_THREADS = MutationProperties.NUMBER_OF_THREADS;

	/**
	 * Maximum running time for one sub process.
	 */
	private static final long MAX_TIME_FOR_SUB_PROCESS = MutationProperties.MAX_TIME_FOR_SUB_PROCESS;

	static {
		File resultDir = new File(MutationProperties.RESULT_DIR);
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
	}

	/**
	 * Command that is used to execute on mutation task.
	 */
	private static final String SCRIPT_COMMAND = MutationProperties.SCRIPT_COMMAND;

	private final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(NUMBER_OF_THREADS);

	/**
	 * All processes that where added to the thread pool.
	 */
	private List<ProcessWrapper> processes = new ArrayList<ProcessWrapper>();

	private int processCounter;

	private final long mutationResultsPre = QueryManager
			.getNumberOfMutationsWithResult();

	private int triedShutdowns = 0;

	private InstanceManager freeInstances = null; // TODO

	// InstanceManager.aspectJInstanceManager();

	public static void main(String[] args) {
		ThreadPool tp = new ThreadPool();
		tp.startTimed();
	}

	/**
	 * Start the processes and collect timing information.
	 */
	private void startTimed() {
		printAndCheckProperties();
		long startTime = System.currentTimeMillis();
		runTasks();
		long duration = System.currentTimeMillis() - startTime;
		long actuallMutationsInDb = QueryManager
				.getNumberOfMutationsWithResult()
				- mutationResultsPre;
		logger.info("Got " + actuallMutationsInDb + "  results and ran for: "
				+ Formater.formatMilliseconds(duration));
		writeResults();
	}

	private void printAndCheckProperties() {
		if (SCRIPT_COMMAND != null) {
			logger.info("Command to start mutations " + SCRIPT_COMMAND);
		} else {
			String message = "Command to start mutations is not set. This can be done with the system property "
					+ MutationProperties.SCRIPT_COMMAND_KEY;
			logger.fatal(message);
			throw new RuntimeException(message);
		}
		if (NUMBER_OF_THREADS > 0) {
			String message = "Number of parallel threads that will be used: "
					+ NUMBER_OF_THREADS;
			logger.info(message);
		} else {
			String message = "Invalid number of threads set: "
					+ NUMBER_OF_THREADS
					+ " The number of thread can be set with the system property "
					+ MutationProperties.NUMBER_OF_THREADS_KEY;
			logger.fatal(message);
			throw new RuntimeException(message);
		}
		if (MAX_TIME_FOR_SUB_PROCESS > 0) {
			String message = "Time limit for subprocesses: "
					+ Formater.formatMilliseconds(MAX_TIME_FOR_SUB_PROCESS);
			logger.info(message);
		} else {
			String message = "Invalid time limit for subprocesses set: "
					+ MAX_TIME_FOR_SUB_PROCESS
					+ " The time limit can be set with the system property "
					+ MutationProperties.MAX_TIME_FOR_SUB_PROCESS_KEY;
			logger.fatal(message);
			throw new RuntimeException(message);
		}

	}

	private void writeResults() {
		// logger.info("Getting" + allQueriedMutations.size() + " Mutations");
		// List<Mutation> dbMutations = QueryManager
		// .getMutationsFromDbByID(allQueriedMutations
		// .toArray(new Long[0]));
		List<Long> mutationsWithResult = new ArrayList<Long>();
		// for (Mutation m : dbMutations) {
		// if (m.getMutationResult() != null) {
		// mutationsWithResult.add(m.getId());
		// }
		// }
		XmlIo.toXML(mutationsWithResult, new File(MutationProperties.RESULT_DIR
				+ "/all-mutations.xml"));
		List<Long> mutationsFromResultFiles = new ArrayList<Long>();
		for (ProcessWrapper ps : processes) {
			RunResult runResult = ps.getRunResult();
			if (runResult != null) {
				List<Long> reportedMutations = runResult.getReportedIds();
				mutationsFromResultFiles.addAll(reportedMutations);
			}
		}
		List<Long> notContainedIds = new ArrayList<Long>();
		for (Long id : mutationsWithResult) {
			if (!mutationsFromResultFiles.contains(id)) {
				notContainedIds.add(id);
			}
		}
		XmlIo.toXML(notContainedIds, new File(MutationProperties.RESULT_DIR
				+ "/ids-not-in-result-files.xml"));

		List<Long> dbNotContainedIds = new ArrayList<Long>();
		for (Long m : mutationsFromResultFiles) {
			if (!mutationsWithResult.contains(m)) {
				dbNotContainedIds.add(m);
			}
		}
		XmlIo.toXML(dbNotContainedIds, new File(MutationProperties.RESULT_DIR
				+ "/ids-not-in-db.xml"));
	}

	private void runTasks() {
		addProcesses();
		while (!pool.isTerminated()) {
			try {
				boolean processesFinished = pool.awaitTermination(CHECK_PERIOD,
						TimeUnit.SECONDS);
				handleProcesses();
				logger.info("Processes finished:" + processesFinished);
				if (processesFinished) {
					pool.shutdown();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		handleResults(processes);
	}

	/**
	 * Creates and adds the given number of processes to the ThreadPool.
	 *
	 * @param numberOfProcesses
	 *            Number of processes the should be added.
	 */
	private void addProcesses() {
		List<MutationTask> tasks = MutationTask.getTasks();
		logger.info(String.format("Adding %d  processes", tasks.size()));
		for (MutationTask task : tasks) {
			createProcess(task);
		}
	}

	/**
	 * Creates a new process and adds it to the ThreadPool.
	 *
	 * @param task
	 */
	private void createProcess(MutationTask task) {
		processCounter++;
		String outputFile = String.format(MutationProperties.RESULT_DIR
				+ "/process-output-"
				+ MutationProperties.PROJECT_PREFIX.replace('.', '_')
				+ "_%02d.txt", task.getID());
		File taskIdFile = task.getTaskFile();
		String resultFile = String.format(MutationProperties.RESULT_DIR
				+ "/process-result-"
				+ MutationProperties.PROJECT_PREFIX.replace('.', '_')
				+ "%02d.xml", task.getID());
		ProcessWrapper ps = new ProcessWrapper(SCRIPT_COMMAND, taskIdFile,
				new File(MutationProperties.EXEC_DIR), new File(outputFile),
				new File(resultFile), processCounter, freeInstances);
		processes.add(ps);
		logger.info("Created process: " + ps.toString());
		pool.submit(ps);
	}

	/**
	 * This method is called in regular intervals. It destroys processes that
	 * run for two long and logs statistics.
	 */
	private void handleProcesses() {
		int processesFinished = getNumberOfFinishedProcesses();
		int processesRunning = handleRunningProcess();
		logger.info(processesFinished + " out of " + processes.size()
				+ " processes are finished and " + processesRunning
				+ " are running");
		// if (processesRunning < NUMBER_OF_THREADS
		// && NUMBER_OF_TASKS > processCounter) {
		// logger.info("Adding new processes");
		// addProcesses(Math.min(PROCESSES_PER_TURN, NUMBER_OF_TASKS
		// - processCounter));
		// }
		if (processesRunning == 0) {
			logger.info("trying to shut down the thread pool");
			pool.shutdown();
			triedShutdowns++;
			if (triedShutdowns > 3) {
				logger.info("Forcing the thread pool to shutdown");
				pool.shutdownNow();
			}
		}
		handleResults(processes);
	}

	private int handleRunningProcess() {
		logger.info("Time " + Util.getTimeString());
		int processesRunning = 0;
		for (ProcessWrapper ps : processes) {
			if (ps.isRunning()) {
				processesRunning++;
				long timeRunning = ps.getTimeRunnning();
				logger
						.info("Process ["
								+ ps.getShortDescription()
								+ "] is running for "
								+ Formater.formatMilliseconds(timeRunning)
								+ " out of "
								+ Formater
										.formatMilliseconds(MAX_TIME_FOR_SUB_PROCESS));
				if (timeRunning >= MAX_TIME_FOR_SUB_PROCESS) {
					logger.info("Destroying process" + ps
							+ " because time running exceeded limit: "
							+ Formater.formatMilliseconds(timeRunning));
					boolean couldBeRemoved = pool.remove(ps);
					logger.info(couldBeRemoved ? "Task was removed"
							: " Task could not be removed");
					ps.destroyProcess();
				}
			}

		}
		return processesRunning;
	}

	private int getNumberOfFinishedProcesses() {
		int processesFinished = 0;
		for (ProcessWrapper ps : processes) {
			if (ps.isFinished()) {
				processesFinished++;
			}
		}
		return processesFinished;
	}

	private void handleResults(List<ProcessWrapper> processes) {
		int totalMutations = 0;
		for (ProcessWrapper ps : processes) {
			RunResult runResult = ps.getRunResult();
			if (ps.isFinished() && runResult != null) {
				totalMutations += runResult.getMutations();
			}
		}
		logger.info(totalMutations
				+ " mutation where actually executed - regarding result files");
		logger
				.info((QueryManager.getNumberOfMutationsWithResult() - mutationResultsPre)
						+ " mutation results where added to db");// TODO
		// handle
		// NOMUTATIONS
	}

}
