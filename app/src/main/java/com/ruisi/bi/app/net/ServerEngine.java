package com.ruisi.bi.app.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.ruisi.bi.app.bean.RequestVo;


/**
 * 服务器访问引擎类，用于建立连接，关闭连接，将任务添加到线程池等操作
 */
public class ServerEngine {

	private ServerCallbackInterface serverCallbackInterface;
	private HashMap<String, Future<?>> ConnectionHashMap;
	private final static int MAX_THREAD_Pool_COUNT = 10;
	private final static ExecutorService executorService = Executors
			.newFixedThreadPool(MAX_THREAD_Pool_COUNT);

	public ServerEngine(ServerCallbackInterface serverCallbackInterface) {
		ConnectionHashMap = new HashMap<String, Future<?>>();
		this.serverCallbackInterface = serverCallbackInterface;
	}

	/** 检测连接是否存在 **/
	public boolean HaveConnectionWithIdentifier(String identifier) {
		return (ConnectionHashMap.containsKey(identifier));
	}

	/** 关闭连接 **/
	public boolean CloseConnection(String identifier) {
		if (identifier != null && ConnectionHashMap.containsKey(identifier)) {
			Future<?> future = ConnectionHashMap.get(identifier);
			if (future != null) {
				future.cancel(true);
				ConnectionHashMap.remove(identifier);
			}
			return true;
		}
		return false;
	}

	/** 关闭所有连接 **/
	public void CloseAllConnection() {
		Iterator<String> iterator = ConnectionHashMap.keySet().iterator();
		if (iterator.hasNext()) {
			String key = iterator.next();
			Future<?> future = ConnectionHashMap.get(key);
			if (future != null) {
				future.cancel(true);
			}
		}
		ConnectionHashMap.clear();
	}

	public void addTaskWithConnection(RequestVo vo) {
		// TODO Auto-generated method stub
		synchronized (executorService) {
				ServerUrlConnection serverUrlConnection = new ServerUrlConnection(
					this, serverCallbackInterface, vo);
			Future<?> task = executorService.submit(serverUrlConnection);
			ConnectionHashMap.put(vo.uuId, task);
		}
	}

}
