package com.ruisi.bi.app.net;

public class ServerErrorMessage {

	private String errormessage = "";
	private String errorDes = "";
	private String errorurl = "";
	private int HttpStatusCode = 0;

	public int getHttpStatusCode() {
		return HttpStatusCode;
	}

	public String getErrorDes() {
		return errorDes;
	}

	public void setErrorDes(String errorDes) {
		this.errorDes = errorDes;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		HttpStatusCode = httpStatusCode;
	}

	public String getErrormessage() {
		return errormessage;
	}

	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}

	public String getErrorurl() {
		return errorurl;
	}

	public void setErrorurl(String errorurl) {
		this.errorurl = errorurl;
	}
}
