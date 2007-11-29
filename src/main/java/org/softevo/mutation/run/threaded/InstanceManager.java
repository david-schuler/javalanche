package org.softevo.mutation.run.threaded;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;


public class InstanceManager {

	private static Logger logger = Logger.getLogger(InstanceManager.class);

	private List<String> freeInstances;

	public InstanceManager() {
		freeInstances = new Vector<String>();
		freeInstances.add("/scratch/schuler/output-mutation-test/instance1");
		freeInstances.add("/scratch/schuler/output-mutation-test/instance2");
		freeInstances.add("/scratch/schuler/output-mutation-test/instance3");
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
			} catch (Exception e) {
				logger.warn("Caugth Exception" + e);
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

}
