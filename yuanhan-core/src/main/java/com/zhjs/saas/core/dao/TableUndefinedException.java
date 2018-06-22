package com.yuanhan.yuanhan.core.dao;

import org.springframework.dao.DataAccessException;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-20
 * @modified:	2017-05-20
 * @version:	
 */
public class TableUndefinedException extends DataAccessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5834975104547348030L;

	/**
	 * @param msg
	 */
	public TableUndefinedException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public TableUndefinedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
