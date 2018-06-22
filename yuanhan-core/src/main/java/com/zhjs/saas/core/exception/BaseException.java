package com.yuanhan.yuanhan.core.exception;

import java.util.HashMap;
import java.util.Map;

import com.yuanhan.yuanhan.core.util.ArrayUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-08
 * @modified:	2017-05-08
 * @version:	
 */
public class BaseException extends Exception
{

	private static final long serialVersionUID = 1L;
	
	public final static String Not_Classified_Error = "isp.error.not.classified";
	public final static String CauseKey = "cause";
	public final static String MessageKey = "message";
	
	private String errorCode;	
	private String errorMsg;
	private Map<String, Object> errorModel;
	private Object[] arguments;
	
	private String traceID;
	

	/**
	 * 
	 */
	public BaseException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BaseException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 */
	public BaseException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * @param cause
	 */
	public BaseException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}
	/**
	 * 
	 */
	public BaseException(String errorCode, Map<String,Object> errorModel) {
		super(errorCode);
		this.errorCode = errorCode;
		this.errorModel = errorModel;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BaseException(String errorCode, String message, Map<String,Object> errorModel, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.errorModel = errorModel;
	}

	/**
	 * @param message
	 */
	public BaseException(String errorCode, String message, Map<String,Object> errorModel) {
		super(message);
		this.errorCode = errorCode;
		this.errorModel = errorModel;
	}

	/**
	 * @param cause
	 */
	public BaseException(String errorCode, Map<String,Object> errorModel, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
		this.errorModel = errorModel;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public Object[] addArgument(Object arg)
	{
		if(arguments==null)
			arguments = new Object[]{arg};
		else
			arguments = ArrayUtil.add(arguments, arg);
		return arguments;
	}

	/**
	 * @return the arguments
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(Object...arguments) {
		this.arguments = arguments;
	}

	/**
	 * @return the errorModel
	 */
	public Map<String, Object> getErrorModel() {
		return errorModel;
	}

	/**
	 * @param errorModel the errorModel to set
	 */
	public void setErrorModel(Map<String, Object> errorModel) {
		this.errorModel = errorModel;
	}
	
	public Map<String, Object> addErrorValue(String key, Object value) {
		if(this.errorModel==null)
			this.errorModel = new HashMap<String,Object>();
		this.errorModel.put(key, value);
		return this.errorModel;
	}

	public String getTraceID()
	{
		return traceID;
	}

	public void setTraceID(String traceID)
	{
		this.traceID = traceID;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

}
