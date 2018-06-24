package com.zhjs.saas.core.dao;

import java.io.Serializable;

import com.zhjs.saas.core.pojo.BaseObject;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-24
 * @modified:	2017-05-24
 * @version:	
 */
public interface BaseDao
{
	public <R extends CommonRepository<T,ID>, T extends BaseObject, ID extends Serializable> R commonDao(Class<T> type, Class<ID> keyType);

	public <R extends CommonRepository<T,?>, T extends BaseObject> R commonDao(Class<T> type);
}
