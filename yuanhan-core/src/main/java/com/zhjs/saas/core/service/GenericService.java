package com.yuanhan.yuanhan.core.service;

import java.util.List;

import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * 
 * @author:		yuanhan
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
