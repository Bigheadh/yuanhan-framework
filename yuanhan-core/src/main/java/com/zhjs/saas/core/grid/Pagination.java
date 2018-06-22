package com.yuanhan.yuanhan.core.grid;

import com.yuanhan.yuanhan.core.util.StringUtil;

import java.io.Serializable;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public class Pagination implements Serializable {
	
	public final static String OrderAsc = "asc";
	public final static String OrderDesc = "desc";
	
	private static final long serialVersionUID = -1L;

	private int pageSize;
	private int recordCount;
	private int currentPage;
	private String sortBy;
	private String sortOrder = OrderAsc;
	private String sorting;
	
	public Pagination()
	{
		this(PagingConstants.DEFAULT_PAGE_SIZE);
	}
	
	public Pagination(int pageSize)
	{
		this(0, pageSize, 1);
	}
	
	public Pagination(int recordCount, int pageSize, int currentPage)
	{
		setRecordCount(recordCount);
		setPageSize(pageSize);
		setCurrentPage(currentPage);
	}
	
	
	/**
	 * return the total record
	 * 
	 * @return the recordCount
	 */
	public int getRecordCount() {
		return recordCount;
	}
	
	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount>=0 ? recordCount : 0;
	}
	
	/**
	 * return the page size, how many record to display for each page
	 * 
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize>0 ? pageSize : PagingConstants.DEFAULT_PAGE_SIZE;
	}
	
	/**
	 * return the current page number, notice it is not the index number
	 * 
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}
	
	/**
	 * Notice!!!
	 * Make sure you have set pageSize and recordCount, so that
	 * the total page number can be calculated, and then we can
	 * detect the current page can't exceed total page number.
	 *  
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		//this.currentPage = currentPage<=0 ? 1 : (currentPage>getTotalPageNum() ? getTotalPageNum() : currentPage);
		//currentPage must >0
		this.currentPage = currentPage>0 ?  currentPage : 1;
	}
	
	/**
	 * return the start index to query from database,
	 * this can be calculated automatically by internal properties.
	 * formula is (currentPage-1)*pageSize
	 * 
	 * for example, use if HQL:
	 *  session.createQuery(hql).setFirstResult(page.getStartIndex()).setMaxResults(page.getPageSize()).list();
	 * 
	 * @return start index to query
	 */
	public int getStartIndex()
	{
		return currentPage>0 ? (currentPage - 1) * pageSize : 0;
	}
	
	public int getEndIndex()
	{
		//int supposeEnd = pageSize * currentPage;
		//return (supposeEnd > recordCount ? recordCount : supposeEnd) - 1;
		//return currentPage>0 ? currentPage * pageSize - 1 : pageSize - 1 ;
		return getStartIndex()+ pageSize - 1;
	}
	
	public int getStartRow()
	{
		return getStartIndex() + 1;
	}
	
	public int getEndRow()
	{
		return getEndIndex() + 1 ;
	}
	
	/**
	 * return the total number of page,
	 * this can be calculated automatically by internal properties.
	 * formula is (recordCount-1)/pageSize+1
	 * 
	 * @return total page number
	 */
	public int getTotalPageNum()
	{
		return recordCount>0 ? (recordCount - 1) / pageSize + 1 : 0;
	}

	/**
	 * Return if the current page is the first one.
	 */
	public boolean isFirstPage() {
		return currentPage == 1;
	}

	/**
	 * Return if the current page is the last one.
	 */
	public boolean isLastPage() {
		return currentPage == getTotalPageNum();
	}

	/**
	 * Switch to previous page.
	 * Will stay on first page if already on first page.
	 */
	public void getPreviousPage() {
		if (!isFirstPage()) {
			currentPage--;
		}
	}

	/**
	 * Switch to next page.
	 * Will stay on last page if already on last page.
	 */
	public void getNextPage() {
		if (!isLastPage()) {
			currentPage++;
		}
	}

	/**
	 * Return the element index of the first element on the current page.
	 * Element numbering starts with 0.
	 */
	public int getFirstElementOnPage() {
		return (currentPage - 1) * pageSize;
	}

	/**
	 * Return the element index of the last element on the current page.
	 * Element numbering starts with 0.
	 */
	public int getLastElementOnPage() {
		int supposeEnd = pageSize * currentPage;
		return (supposeEnd > recordCount ? recordCount : supposeEnd) - 1;
	}

	public String getSorting() {
		if(StringUtil.isNotEmpty(sorting)) {
			return sorting;
		}else if(StringUtil.isNotEmpty(sortBy)){
			return " order by " + underscoreName(sortBy) + " " + sortOrder + " ";
		}else{
			return  "";
		}
	}

	public void setSorting(String sorting) {
		this.sorting = sorting;
	}

	/**
	 * @return the sortBy
	 */
	public String getSortBy()
	{
		return sortBy;
	}

	/**
	 * @param sortBy the sortBy to set
	 */
	public void setSortBy(String sortBy)
	{
		this.sortBy = sortBy;
	}

	/**
	 * @return the sortOrder
	 */
	public String getSortOrder()
	{
		return sortOrder;
	}

	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(String sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	private String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			result.append(name.substring(0,1).toLowerCase());
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i+1);
				if (s.equals(s.toUpperCase())) {
					result.append("_");
					result.append(s.toLowerCase());
				}
				else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}
}
