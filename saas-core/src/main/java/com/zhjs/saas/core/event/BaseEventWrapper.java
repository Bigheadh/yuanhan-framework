package com.zhjs.saas.core.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-17
 * @modified:	2017-05-17
 * @version:	
 */
public class BaseEventWrapper implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private BaseEvent event;
	private boolean publishRemote = false;
	
	public BaseEventWrapper(){}
	
	public BaseEventWrapper(BaseEvent event)
	{
		this.event = event;
	}
	
	public BaseEventWrapper(BaseEvent event, boolean publishRemote)
	{
		this.event = event;
		this.publishRemote = publishRemote;
	}
	
	private List<BaseEventPublisher> publishers;
	private final static BaseEventPublisher publisher = EventPublisherFactory.getEventPublisher();

	public void run() {
		logger.debug("Begin dispatching event! "
				+ event.getClass().getSimpleName() + "." + event.getType());
		this.publishEvent(event);
	}
	
    /**
	 * publish event by spring web application context
	 */
	public void publishEvent(BaseEvent event)
	{
		if(publishRemote)
		{
			for(BaseEventPublisher publisher : publishers)
			{
				publisher.publishEvent(event);
			}
		}
		else
			publisher.publishEvent(event);
	}

	/**
	 * @param event the event to set
	 */
	public final void setEvent(BaseEvent event) {
		this.event = event;
	}

}
