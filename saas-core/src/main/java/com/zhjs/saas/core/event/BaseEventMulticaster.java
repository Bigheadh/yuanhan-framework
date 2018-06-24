package com.zhjs.saas.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import com.zhjs.saas.core.util.ApplicationContextUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-17
 * @modified:	2017-05-17
 * @version:	
 */
public class BaseEventMulticaster implements BaseEventPublisher
{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    /**
	 * publish event by spring web application context
	 */
	public void publishEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		if(event instanceof BaseEvent)
		{			
			logger.debug("an event {} from {} is triggered : {}", ((BaseEvent) event).getType(),
					((BaseEvent) event).getFrom(), ((BaseEvent) event).getSource().toString());
			ApplicationContextUtil.getApplicationContext().publishEvent(event);
		}
	}

	@Override
	public void publishEvent(Object event)
	{
		//this.publishEvent(event);
	}

}
