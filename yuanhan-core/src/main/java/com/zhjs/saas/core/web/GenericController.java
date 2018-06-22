package com.yuanhan.yuanhan.core.web;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.yuanhan.yuanhan.core.annotation.ApiMapping;
import com.yuanhan.yuanhan.core.annotation.RestComponent;
import com.yuanhan.yuanhan.core.config.PropertyConfig;
import com.yuanhan.yuanhan.core.pojo.BaseObject;
import com.yuanhan.yuanhan.core.service.GenericService;
import com.yuanhan.yuanhan.core.util.BeanUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-13
 * @modified:	2017-11-13
 * @version:	
 */
@ConditionalOnProperty(name=PropertyConfig.Generic_CRUD_Enable, havingValue="true")
@RestComponent(namespace="${yuanhan.generic.namespace.path}")
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
