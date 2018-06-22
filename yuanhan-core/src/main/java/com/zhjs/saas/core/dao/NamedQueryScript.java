package com.yuanhan.yuanhan.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-02-27
 * @modified:	2018-02-27
 * @version:	
 */
public class NamedQueryScript extends AbstractJpaQuery
{
	
	//private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Creates a new {@link AbstractJpaQuery} from the given {@link JpaQueryMethod}.
	 * 
	 * @param method
	 * @param em
	 */
	public NamedQueryScript(JpaQueryMethod method, EntityManager em) 
	{
		super(method, em);
	}

	@Override
	protected Query doCreateQuery(Object[] values)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Query doCreateCountQuery(Object[] values)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
