package com.zhjs.saas.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-08
 * @modified:	2017-05-08
 * @version:	
 */
public class BaseEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 7104004791102260005L;
	
	private String type;
	// from: is a field point to the controller/service bean where fire this event
	private String from;
	private Object[] args;
	
	
	/**
	 * 
	 * @param type
	 * 			event type
	 * @param source
	 * 			event source object
	 */
	public BaseEvent(String type, Object source) {
		super(source);
		this.type = type;
	}


	/**
	 * @param type
	 * @param source
	 * @param from from where the event is fired, pattern : class.method()
	 */
	public BaseEvent(String type, Object source, String from) {
		super(source);
		this.type = type;
		this.from = from;
	}

	/**
	 * 
	 * @param type
	 * 			event type
	 * @param source
	 * 			event source object
	 * @param args
	 * 			event args to invoke a method
	 */
	public BaseEvent(String type, Object source, Object[] args) {
		super(source);
		this.type = type;
		this.args = args;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * @param args the args to set
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

	/**
	 * @return the from
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

}
