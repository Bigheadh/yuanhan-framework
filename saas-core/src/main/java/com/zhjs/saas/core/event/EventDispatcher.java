package com.zhjs.saas.core.event;

import org.springframework.context.ApplicationEvent;

import com.zhjs.saas.core.util.ApplicationContextUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-17
 * @modified:	2017-05-17
 * @version:	
 */
public abstract class EventDispatcher {
	
	public static void dispatchEvent(BaseEvent event)
	{
		
		// [0] is java.lang.Stack
		// [1] is current stack EventDispatcher
		// [2] is pre stack 
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		event.setFrom(element.getClassName() + "." + element.getMethodName() + "()(line:" + element.getLineNumber() + ")");
		new Thread(new BaseEventWrapper(event)).start();
	}
	
	public static void dispatchEvent(BaseEvent event, boolean publishRemote)
	{
		new Thread(new BaseEventWrapper(event,publishRemote)).start();
	}
	
	public static void dispatchEvent(ApplicationEvent event)
	{
		dispatchEvent((Object)event);
	}
	
	public static void dispatchEvent(Object event)
	{
		ApplicationContextUtil.getApplicationContext().publishEvent(event);
	}
}
