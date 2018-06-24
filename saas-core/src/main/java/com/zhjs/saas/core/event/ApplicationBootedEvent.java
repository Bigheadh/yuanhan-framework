package com.zhjs.saas.core.event;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-21
 * @modified:	2017-11-21
 * @version:	
 */
public class ApplicationBootedEvent extends BaseEvent
{
	private static final long serialVersionUID = 6868492897451677028L;
	
	public static final String EVENT = ApplicationBootedEvent.class.getName();
	
	public ApplicationBootedEvent(Object source)
	{
		super(EVENT, source);
	}

}
