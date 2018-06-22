package com.yuanhan.yuanhan.core.grid;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.yuanhan.yuanhan.core.util.CollectionUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */

public class PagingDisplay<T> {

	private Pagination pagination = new Pagination();
	private SearchCriteria searchCriteria = new SearchCriteria();
	private List<T> resultItems;
	private List<Object> addition;

	/**
	 * @return the gridPaging
	 */
	public Pagination getPagination() {
		return pagination;
	}

	/**
	 * @param gridPaging the gridPaging to set
	 */
	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	/**
	 * @return the searchCriteria
	 */
	@Transient
	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * @param searchCriteria the searchCriteria to set
	 */
	public void setSearchCriteria(SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * @return the resultItems
	 */
	public List<T> getResultItems() {
		return resultItems;
	}

	/**
	 * @param resultItems the resultItems to set
	 */
	public void setResultItems(List<T> resultItems) {
		this.resultItems = resultItems;
	}
	
	/**
	 * push an object into additional list
	 * 
	 * @param o
	 * @return the additional list
	 */
	public List<Object> pushAddition(Object o)
	{
		if(CollectionUtil.isEmpty(addition))
			addition = new ArrayList<>();
		addition.add(o);
		return addition;
	}

	
	/**
	 * @return the additional
	 */
	public List<Object> getAddition()
	{
		return addition;
	}

	/**
	 * @param additional the additiona list to set
	 */
	public void setAddition(List<Object> additional)
	{
		this.addition = additional;
	}

}
