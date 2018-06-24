package com.zhjs.saas.core.web;

import com.zhjs.saas.core.pojo.BaseObject;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-13
 * @modified:	2017-11-13
 * @version:	
 */
public interface WebInterceptor
{
	
	public <T extends BaseObject> void before(T entity);
	
	public <T extends BaseObject> void after(T entity);

}
