package com.yuanhan.yuanhan.core.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * 
 * @author:		 yuanhan
 * @since:		 2017-05-18
 * @modified: 	 2017-05-18
 * @version:
 */
public abstract class MessageUtil
{
	protected static Logger logger = LoggerFactory.getLogger(MessageUtil.class);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private MessageUtil(){}

	public static String getMessage(String code)
	{
		return getMessage(code, null);
	}

	public static String getMessage(String code, Object[] args)
	{
		return getMessage(code, args, WebUtil.getLocale());
	}

	public static String getMessage(String code, Object[] args, Locale locale)
	{
		ApplicationContext context = ApplicationContextUtil.getApplicationContext();
		try
		{
			return context.getMessage(code, args, locale);
		}
		catch(NoSuchMessageException e)
		{
			return null;
		}
	}

	/*
	 * get message by code from message.properties
	 */
	public static String getMessage(HttpServletRequest request, String code, Object[] arguments)
	{
		RequestContext requestContext = new RequestContext(request);
		MessageSource messageSource = requestContext.getWebApplicationContext();
		if (messageSource == null)
			return code;
		return messageSource.getMessage(code, arguments, code, requestContext.getLocale());
	}

	public static String getMessage(HttpServletRequest request, String code)
	{
		return getMessage(request, code, null);
	}

	public static String getMessage(HttpServletRequest request, String code, Object[] args, Locale locale)
	{
		RequestContext requestContext = new RequestContext(request);
		MessageSource messageSource = requestContext.getWebApplicationContext();
		if (messageSource == null)
			return code;
		return messageSource.getMessage(code, args, locale);
	}

	/*
	 * create ObjectError instance
	 */
	public static ObjectError createObjectError(String code, Object[] arguments)
	{
		if (StringUtil.isNotBlank(code))
		{
			ObjectError objError = new ObjectError(null, new String[] { code }, arguments, code);
			return objError;
		}
		return null;
	}

	/*
	 * get message by ObjectError.code and ObjectError.arguments from
	 * message.properties, return default message if not found try to match
	 * argument from message.properties, use orginal argument if not found
	 * 
	 */
	public static String getObjectErrorMessage(HttpServletRequest request, ObjectError objError)
	{
		String code = objError.getCode();
		Object[] args = objError.getArguments();
		String defaultMsg = objError.getDefaultMessage();

		if (StringUtil.isNotBlank(code))
			return getMessageDefaultMessage(request, code, defaultMsg, args);
		return "";
	}

	/*
	 * get message list by list of codes with no arguments from
	 * message.properties, return list of code if not found
	 */
	public static String[] getMessageListDefaultCode(HttpServletRequest request, Object[] codes)
	{
		String[] messages = ArrayUtil.EMPTY_STRING_ARRAY;
		if (!ArrayUtil.isEmpty(codes))
		{
			for (int i = 0; i < codes.length; i++)
			{
				String code = (codes[i] == null) ? "" : codes[i].toString();
				String message = getMessageDefaultCode(request, code, null);
				messages = (String[]) ArrayUtil.add(messages, message);
			}
		}
		return messages;
	}

	/*
	 * get message by code from message.properties, return code if not found try
	 * to match argument from message.properties, use orginal argument if not
	 * found
	 */
	public static String getMessageDefaultCode(HttpServletRequest request, String code, Object[] arguments)
	{
		return getMessageDefaultMessage(request, code, code, arguments);
	}

	/*
	 * get message by code from message.properties, return default message if
	 * not found try to match argument from message.properties, use orginal
	 * argument if not found
	 */
	public static String getMessageDefaultMessage(HttpServletRequest request, String code, String defaultMessage,
			Object[] arguments)
	{
		return getMessageDefaultMessage(request, code, defaultMessage, arguments, false);
	}

	/*
	 * get message by code from message.properties, return default message if
	 * not found try to match argument from message.properties, use orginal
	 * argument if not found
	 */
	public static String getMessageDefaultMessage(HttpServletRequest request, String code, String defaultMessage,
			Object[] arguments, boolean useDefaultArgs)
	{
		try
		{
			MessageSource messageSource = RequestContextUtils.findWebApplicationContext(request);
			if (messageSource == null)
				return code;
			
			Locale locale = RequestContextUtils.getLocale((HttpServletRequest) request);
			if (useDefaultArgs)
				return messageSource.getMessage(code, arguments, defaultMessage, locale);
			else
				return messageSource.getMessage(code, getMessageListDefaultCode(request, arguments), defaultMessage,
						locale);
		} catch (Exception e)
		{
			logger.error("Fail get Message with code:" + code + ", " + e.getMessage());
		}
		return code;
	}
}
