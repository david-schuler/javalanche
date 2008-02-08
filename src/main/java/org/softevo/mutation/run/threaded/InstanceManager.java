package org.softevo.mutation.run.threaded;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class InstanceManager {

	private static Logger logger = Logger.getLogger(InstanceManager.class);

	private List<String> freeInstances;

	private InstanceManager(String[] instances) {
		freeInstances = new Vector<String>();
		for (String str : instances) {
			freeInstances.add(str);
		}
	}

	public synchronized boolean hasInstance() {
		return freeInstances.size() > 0;
	}

	public synchronized String getInstance() {
		logger.info("trying to get instance - enter method");
		String result = null;
		if (freeInstances.size() > 0) {
			logger.info("trying to get instance now");
			result = freeInstances.get(0);
			logger.info("Trying to remove instance");
			try {
				freeInstances.remove(0);
			} catch (Throwable e) {
				logger.warn("Caught Exception" + e);

			}
			logger.info("got instance" + result);
		}
		return result;
	}

	public synchronized void addInstance(String instance) {
		logger.info("adding instance");
		freeInstances.add(instance);
		logger.info("instance added");
	}


	public static InstanceManager aspectJInstanceManager(){
		String[] aspectJInstances = new String[]{
				"/scratch/schuler/output-mutation-test/instance1",
				"/scratch/schuler/output-mutation-test/instance2",
				"/scratch/schuler/output-mutation-test/instance3",
		};
		return new InstanceManager(aspectJInstances);
	}
}
