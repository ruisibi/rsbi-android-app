package com.ruisi.bi.app.net;

/**
 * 服务器返回数据回调接口
 */
public interface ServerCallbackInterface {  

	/**
	 * 返回成功时进行回调
	 * @param <T>
	 * @param map 返回封装的数据
	 * @param uuid 返回线程的唯一标识码
	 */
	public <T> void succeedReceiveData(T object, String uuid);

	/**
	 * 返回失败时进行调用
	 * @param errorMessage 返回封装的失败信息
	 * @param uuid 返回线程唯一标识码
	 */
	public void failedWithErrorInfo(ServerErrorMessage errorMessage, String uuid);

}
