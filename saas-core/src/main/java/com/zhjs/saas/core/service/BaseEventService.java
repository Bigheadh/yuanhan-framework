package com.zhjs.saas.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.core.OrderComparator;

import com.zhjs.saas.core.event.BaseEvent;
import com.zhjs.saas.core.event.EventListener;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.util.MethodUtil;

/**
 * 
 * @author: Jackie Wang
 * @since: 2017-05-08
 * @modified: 2017-05-17
 * @version:
 */
public abstract class BaseEventService implements ApplicationListener<BaseEvent>
{

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Map<String, List<EventListener>> registries = new HashMap<String, List<EventListener>>();

	public final void onApplicationEvent(BaseEvent event)
	{
		onBaseEvent(event);
	}

	protected void addEventListener(String type, String method)
	{
		addEventListener(type, method, false, 0, false);
	}

	protected void addEventListener(String type, String method, int order)
	{
		addEventListener(type, method, false, order, false);
	}

	protected void addEventListener(String type, String method, boolean capture, int order, boolean async)
	{
		List<EventListener> listeners = registries.get(type);

		EventListener listener = new EventListener();
		listener.setType(type);
		listener.setAsync(async);
		listener.setCapture(capture);
		listener.setMethod(method);
		listener.setOrder(order);

		if (listeners == null)
		{
			listeners = new ArrayList<EventListener>();
		}

		listeners.add(listener);
		registries.put(type, listeners);
	}

	protected void removeEventListener(String type)
	{
		registries.remove(type);
	}

	protected void removeEventListener(String type, String method)
	{
		registries.remove(type);
	}

	protected void removeEventListener(String type, String method, boolean capture, int order, boolean async)
	{

	}

	protected void onBaseEvent(BaseEvent event)
	{
		List<EventListener> listeners = registries.get(event.getType());
		if (listeners != null && listeners.size() > 0)
		{
			Collections.sort(listeners, new OrderComparator());
			for (EventListener listener : listeners)
			{
				if (listener.getType().equals(event.getType()))
					MethodUtil.invoke(this, listener.getMethod(), event);
			}
		}
	}

}
