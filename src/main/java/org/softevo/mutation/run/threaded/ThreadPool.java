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
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.QueryManager;
import org.softevo.mutation.run.threaded.task.MutationTask;
import org.softevo.mutation.runtime.RunResult;
import org.softevo.mutation.util.Formater;

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
	private static final int NUMBER_OF_THREADS = 2;

	/**
	 * Maximum running time for one sub process.
	 */
	private static final long MAX_TIME_FOR_SUB_PROCESS = 60 * 60 * 1000;

	static {
		File resultDir = new File(MutationProperties.RESULT_DIR);
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
	}

	// private static final String TASK_NAME = "test-no-compile";

	/**
	 * Command that is used to execute on mutation task.
	 */
	// private static final String SCRIPT_COMMAND =
	// "/scratch/schuler/mutationTest/src/scripts/threaded-run-tests.sh";
	private static final String SCRIPT_COMMAND = System
			.getProperty(MutationProperties.SCRIPT_COMMAND_KEY);

	// /**
	// * Processes that are added to the thread pool per turn. after one turn
	// the
	// * ids of mutations without results are refreshed.
	// */
	// private static final int PROCESSES_PER_TURN = 5;

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
		long startTime = System.currentTimeMillis();
		runTasks();
		long duration = System.currentTimeMillis() - startTime;
		long actuallMutationsInDb = QueryManager
				.getNumberOfMutationsWithResult()
				- mutationResultsPre;
		logger.info("Got " + actuallMutationsInDb + "  results\nRun for: "
				+ Formater.formatMilliseconds(duration));
		writeResults();
	}

	private void writeResults() {
//		logger.info("Getting" + allQueriedMutations.size() + " Mutations");
//		List<Mutation> dbMutations = QueryManager
//				.getMutationsFromDbByID(allQueriedMutations
//						.toArray(new Long[0]));
		List<Long> mutationsWithResult = new ArrayList<Long>();
//		for (Mutation m : dbMutations) {
//			if (m.getMutationResult() != null) {
//				mutationsWithResult.add(m.getId());
//			}
//		}
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
				+ "/process-output-%02d.txt", task.getID());

		File taskIdFile = task.getTaskFile();
		String resultFile = String.format(MutationProperties.RESULT_DIR
				+ "/process-result-%02d.xml", task.getID());
		ProcessWrapper ps = new ProcessWrapper(SCRIPT_COMMAND, taskIdFile,
				new File(MutationProperties.EXEC_DIR), new File(outputFile),
				new File(resultFile), processCounter, freeInstances);
		processes.add(ps);
		logger.info("Process: " + ps.toString());
		pool.submit(ps);
	}

	/**
	 * This method is called in regular intervals. It destroys processes that
	 * run for two long and logs statistics.
	 */
	private void handleProcesses() {
		int processesFinished = getNumberOfFinishedProcesses();
		int processesRunning = handleRunningProcess();
		logger.info(processesFinished + " processes are finished and "
				+ processesRunning + " are running");
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
		int processesRunning = 0;
		for (ProcessWrapper ps : processes) {
			if (ps.isRunning()) {
				processesRunning++;
				long timeRunning = ps.getTimeRunnning();
				logger
						.info("Process is running for "
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
