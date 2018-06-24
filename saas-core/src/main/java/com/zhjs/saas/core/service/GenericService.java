package com.zhjs.saas.core.service;

import java.util.List;

import com.zhjs.saas.core.pojo.BaseObject;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-13
 * @modified:	2017-11-13
 * @version:	
 */
public interface GenericService
{
	public <T extends BaseObject> List<T> list(String[] ids, String entityClass) throws Exception;

	public <T extends BaseObject> T get(String id, String entityClass) throws Exception;

	public <T extends BaseObject> T saveOrUpdate(T t) throws Exception;

	public boolean remove(String userid, String entityClass) throws Exception;

}
