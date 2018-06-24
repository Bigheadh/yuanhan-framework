package com.zhjs.saas.core.event;

import org.springframework.core.Ordered;


/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-17
 * @modified:	2017-05-17
 * @version:	
 */
public class EventListener implements Ordered
{
	
	private String type;
	private boolean capture;
	private int order = Integer.MAX_VALUE;;
	private boolean async;
	private String method;
	
	/**
	 * @return the capture
	 */
	public boolean isCapture() {
		return capture;
	}
	/**
	 * @param capture the capture to set
	 */
	public void setCapture(boolean capture) {
		this.capture = capture;
	}
	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	/**
	 * @return the async
	 */
	public boolean isAsync() {
		return async;
	}
	/**
	 * @param async the async to set
	 */
	public void setAsync(boolean async) {
		this.async = async;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the eventType
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param eventType the eventType to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
