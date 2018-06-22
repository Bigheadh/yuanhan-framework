package com.yuanhan.yuanhan.core.exception;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-12
 * @modified:	2017-12-12
 * @version:	
 */
public class CommonResponseErrorHandler extends DefaultResponseErrorHandler
{
	
	public void handleError(ClientHttpResponse response) throws IOException 
	{
		super.handleError(response);
	}

}
