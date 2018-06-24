package com.zhjs.saas.core.grid;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:		Jackie Wong 
 * @since:		May 4, 2010
 * @modified:	May 4, 2010
 * @version:	  
 */

@SuppressWarnings("rawtypes")
public class SearchCriteria implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	private String orderBy;
	private String orderType;
	
	private Map mapCriteria = new HashMap();
	private Object beanCriteria;
    
    public SearchCriteria()
    {
        super();
    }

    public SearchCriteria(String orderBy, String orderType)
    {
        super();
        this.orderBy = orderBy;
        this.orderType = orderType;
    }

	/**
	 * @return the orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * set the order field,
	 * it is mapping with the column of table in database
	 * or mapping with the property of pojo from db table.
	 * 
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}

	/**
	 * set the sort pattern, ascending or descending.
	 * 
	 * @param orderType the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	/**
	 * Notice! If the beanCriteria not null, it always return bean criteria.<br>
	 * otherwise, return map criteria.
	 * @return the criteria
	 */
	public Object getCriteria() {
		
		if(this.beanCriteria!=null)
			return this.beanCriteria;
		
		return mapCriteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(Object criteria) {
		if(criteria instanceof Map)
			this.mapCriteria = (Map)criteria;
		
		this.beanCriteria = criteria;
	}

}
