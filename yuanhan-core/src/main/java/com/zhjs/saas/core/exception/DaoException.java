package com.yuanhan.yuanhan.core.exception;

public class DaoException extends BaseException
{

	private static final long serialVersionUID = -540034043968947014L;
	
	public static final String DAO_ERROR = "isv.error.db_access_error";
	public static final String Empty_Data = "isv.error.result_data_empty";

	public DaoException(Throwable cause)
	{
		super(DAO_ERROR, cause);
	}

	public DaoException(String errorCode, String message)
	{
		super(errorCode, message);
	}

	public DaoException(String errorCode, Throwable cause)
	{
		super(errorCode, cause);
	}

	public DaoException(String errorCode, String message, Throwable cause)
	{
		super(errorCode, message, cause);
	}


}
