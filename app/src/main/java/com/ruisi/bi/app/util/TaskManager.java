package com.ruisi.bi.app.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务线程管理
 */
public class TaskManager {
	private static TaskManager executorServiceManager = null;
	private static ExecutorService executorService;
	private static int processorNum;

	private TaskManager() {
		processorNum = Runtime.getRuntime().availableProcessors();
		executorService = Executors.newFixedThreadPool(processorNum * 2);
	}
	
	public static final TaskManager getInstance(){
		if (executorServiceManager == null) {
			executorServiceManager = new TaskManager();
		}
		return executorServiceManager;
	}
	
	public void addTask(Runnable runnable){
		executorService.execute(runnable);
	}
}
