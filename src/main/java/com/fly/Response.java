package com.fly;


/**
 * http 请求响应
 * 
 * @author panxf 
 * @version V1.0 创建时间：2012-7-13
 */
public class Response {
	private int statusCode;
	private String responseString;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", responseString='" + responseString + '\'' +
                '}';
    }
}
