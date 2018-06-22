package com.yuanhan.yuanhan.core.util;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-14
 * @modified:	2017-11-14
 * @version:	
 */
public abstract class Assert extends org.springframework.util.Assert
{

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Assert(){}
	
	public static void equals(Object target, Object expected, String message)
	{
		if(target instanceof String && expected instanceof String && target!=expected)
		{
			throw new IllegalArgumentException(message);
		}
		else
		{
			if(target!=null)
				isTrue(target.equals(expected), message);
			else if(expected!=null)
				isTrue(expected.equals(target), message);
			else
				throw new IllegalArgumentException(message+". Both two objects are null.");
		}
	}

}
