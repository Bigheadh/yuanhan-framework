package com.yuanhan.yuanhan.core.event;

/**
 * 
 * @author:		yuanhan
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
