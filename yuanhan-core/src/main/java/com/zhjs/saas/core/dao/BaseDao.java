package com.yuanhan.yuanhan.core.dao;

import java.io.Serializable;

import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-24
 * @modified:	2017-05-24
 * @version:	
 */
public interface BaseDao
{
	public <R extends CommonRepository<T,ID>, T extends BaseObject, ID extends Serializable> R commonDao(Class<T> type, Class<ID> keyType);

	public <R extends CommonRepository<T,?>, T extends BaseObject> R commonDao(Class<T> type);
}
