package com.ruisi.bi.app.net;

import com.ruisi.bi.app.bean.RequestVo;

public class ServerInterfaceContext {
	private ServerEngine serverEngine;

	public ServerInterfaceContext(ServerCallbackInterface callbackInterface) {
		serverEngine = new ServerEngine(callbackInterface);
	}

	public void closeConnectionById(String identifier) {
		serverEngine.CloseConnection(identifier);
	}

	public void closeAllConnection() {
		serverEngine.CloseAllConnection();
	}

	public void sendRequest(RequestVo vo) {
		serverEngine.addTaskWithConnection(vo);
	}
	

}
