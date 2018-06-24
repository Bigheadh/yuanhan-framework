package com.zhjs.saas.core.event;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-17
 * @modified:	2017-05-17
 * @version:	
 */
public abstract class EventPublisherFactory {
	
	public static BaseEventPublisher getEventPublisher()
	{
		return new BaseEventMulticaster();
	}

}
