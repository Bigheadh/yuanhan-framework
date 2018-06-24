package com.zhjs.saas.core.web;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.zhjs.saas.core.annotation.ApiMapping;
import com.zhjs.saas.core.annotation.RestComponent;
import com.zhjs.saas.core.config.PropertyConfig;
import com.zhjs.saas.core.pojo.BaseObject;
import com.zhjs.saas.core.service.GenericService;
import com.zhjs.saas.core.util.BeanUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-13
 * @modified:	2017-11-13
 * @version:	
 */
@ConditionalOnProperty(name=PropertyConfig.Generic_CRUD_Enable, havingValue="true")
@RestComponent(namespace="${saas.generic.namespace.path}")
public class GenericController extends AbstractController
{
	
	private GenericService generic;

	@ApiMapping("generic.crud.list")
	public <T extends BaseObject> List<T> list(String[] ids, String entityClass) throws Exception
	{
		logger.info("list");
		return generic.list(ids, entityClass);
	}

	@ApiMapping("generic.crud.list")
	public <T extends BaseObject> T get(String id, String entityClass) throws Exception
	{
		logger.info("list");
		return generic.get(id, entityClass);
	}

	@SuppressWarnings("unchecked")
	@ApiMapping("generic.crud.saveorupdate")
	public <T extends BaseObject> T saveOrUpdate(String entityClass) throws Exception
	{
		logger.info("success");
		T t = BeanUtil.bindRequestToBean((Class<T>)Class.forName(entityClass));
		return generic.saveOrUpdate(t);
	}

	@ApiMapping("generic.crud.remove")
	public boolean remove(String id, String entityClass) throws Exception
	{
		logger.info("success");
		return generic.remove(id, entityClass);
	}

}
