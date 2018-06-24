package com.zhjs.saas.core.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.zhjs.saas.core.annotation.InitMethod;
import com.zhjs.saas.core.grid.Pagination;
import com.zhjs.saas.core.grid.PagingDisplay;
import com.zhjs.saas.core.grid.SearchCriteria;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.pojo.BaseObject;
import com.zhjs.saas.core.util.AopUtil;
import com.zhjs.saas.core.util.ApplicationContextUtil;
import com.zhjs.saas.core.util.BeanUtil;
import com.zhjs.saas.core.util.CollectionUtil;
import com.zhjs.saas.core.util.StringUtil;

/**
 * 1. wrap some common operations for getting list by paging.<br>
 * 2. sessionFactory and dataSource will be injected automatically.<br>
 * 3. support JDBC and Hibernate API. getHibernateTemplate(), getJdbcTemplate() and getNamedJdbcTemplate().<br> 
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */

@SuppressWarnings({"unchecked","rawtypes"})
public class AbstractDao extends HibernateAndJdbcDaoSupport implements BaseDao {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@InitMethod
	public void initDataSourceAndSessionFactory()
	{
		super.initDataSourceAndSessionFactory();
	}
	
	protected void initializeProperties(BaseObject entity, Field field) throws HibernateException
	{
		boolean accessible = false;
		//accessible = field.canAccess(entity);
		accessible = field.isAccessible();
		if (!accessible) {
			field.setAccessible(true);
			accessible = true;
		}
		try {
			Object value = field.get(entity);
			if (value instanceof PersistentCollection)
				Hibernate.initialize(value);
			else if (value instanceof BaseObject)
				Hibernate.initialize(value);
		} catch (IllegalArgumentException e) {
			logger.error("Can't cast the entity field when initialize the Hibernate proxy.", e);
		} catch (IllegalAccessException e) {
			logger.error("Can't access the entity field when initialize the Hibernate proxy.", e);
		}

		if (accessible)
			field.setAccessible(false);		
	}
	
	public <T extends BaseObject> T initializeObject(T entity) throws HibernateException
	{
		this.initializeProperties(entity);
		return entity;
	}
	
	protected void initializeProperties(BaseObject entity) throws HibernateException
	{
		Field[] fields = entity.getClass().getDeclaredFields();
		for(Field field : fields)
		{
			initializeProperties(entity, field);
		}
	}

	/**
	 * This method will initialize all fields of the classes included in the
	 * list. the fieldsNames array will contain the field names to be
	 * initialized. It invokes hibernate API. 
	 * 
	 * @param session
	 * @param list
	 * @param fieldNames
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws HibernateException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	protected void initializeProperties(List<BaseObject> list, String... fieldNames) throws SecurityException,
			NoSuchFieldException, HibernateException {
		for (BaseObject listItem : list) {
			for (int i = 0; i < fieldNames.length; i++) {
				if (fieldNames[i] == null || fieldNames[i].trim().length() == 0)
					continue;
				Field field = listItem.getClass().getDeclaredField(fieldNames[i]);
				initializeProperties(listItem, field);
			}
		}
	}

	/**
	 * This method will initialize all fields of the classes included in the
	 * list. the fieldsNames array will contain the field names to be
	 * initialized. It invokes hibernate API. 
	 * 
	 * @param session
	 * @param list
	 * @param fieldNames
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws HibernateException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	protected void initializeProperties(Set<BaseObject> set, String... fieldNames) throws SecurityException,
			NoSuchFieldException, HibernateException {
		while(set.iterator().hasNext())
		{
			BaseObject item = set.iterator().next();
			for (int i = 0; i < fieldNames.length; i++) {
				if (fieldNames[i] == null || fieldNames[i].trim().length() == 0)
					continue;
				Field field = item.getClass().getDeclaredField(fieldNames[i]);
				initializeProperties(item, field);
			}
		}
	}

	/**
	 * This method performs the query by paging for any listing report.
	 * It invokes hibernate API. 
	 * 
	 * @param gridDisplay -
	 *           The paging and search criteria is provided here
	 * @param countQuery -
	 *           The select clause/query for counting the rows
	 * @param dataQuery -
	 *           The select clause/query for listing the rows data
	 * @param queryBody -
	 *           The query body. If it is null, then the body is already part of
	 *           the count and data queries.
	 * @return the PagingDisplay, its resultItems filled with a list containing the rows data. 
	 *         with the recordCount
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws HibernateException
	 */
	protected PagingDisplay getList(final PagingDisplay gridDisplay, String countQuery, String dataQuery,
			String queryBody, String... propToInitialize) throws DataAccessException {
		final SearchCriteria search = gridDisplay.getSearchCriteria();
		final String dataQ = (queryBody != null ? dataQuery + queryBody : dataQuery);
		final String countQ = (countQuery != null ? (queryBody != null ? countQuery + queryBody : countQuery) : dataQ);
		final String[] propsToInitialize = propToInitialize;
		Long count = getHibernateTemplate().execute(session -> {
						Query q = session.createQuery(countQ);
						if (search != null)
							q.setProperties(search);
						return (List<Long>)q.list();
					}).get(0);
		
		if (count > 0) {
			gridDisplay.getPagination().setRecordCount(count.intValue());
			List<BaseObject> result = getHibernateTemplate().execute( session -> {
							Query q = session.createQuery(dataQ);
							if (search != null)
								q.setProperties(search);
							q.setFirstResult(gridDisplay.getPagination().getStartIndex());
							q.setMaxResults(gridDisplay.getPagination().getPageSize());
							List<BaseObject> resultList = q.list();
							try {
								initializeProperties(resultList, propsToInitialize);
							} catch (Exception e) {
								logger.error("", e);
							}
							return resultList;
					});
			gridDisplay.setResultItems(result);
			return gridDisplay;
		} else {
			gridDisplay.getPagination().setRecordCount(0);
			gridDisplay.setResultItems(new ArrayList());
			return gridDisplay;
		}
	}

	/**
	 * This method performs the query by paging based on criteria.
	 * It invokes hibernate API. 
	 * 
	 * @param gridDisplay -
	 *           The paging and search criteria is provided here
	 * @param countCriteria -
	 *           criteria for counting the rows
	 * @param selectCriteria -
	 *           criteria for listing the rows data
	 * @return the PagingDisplay, its resultItems filled with a list containing the rows data. 
	 *         with the recordCount
	 */
	protected PagingDisplay getList(final PagingDisplay gridDisplay,
			DetachedCriteria countCriteria, DetachedCriteria selectCriteria) throws DataAccessException {
		List result = null;
		Integer count = (Integer) getHibernateTemplate().findByCriteria(countCriteria).get(0);

		if (count > 0) {
			gridDisplay.getPagination().setRecordCount(count.intValue());
			result = getHibernateTemplate().findByCriteria(selectCriteria,
					gridDisplay.getPagination().getStartIndex(),
					gridDisplay.getPagination().getPageSize());
			gridDisplay.setResultItems(result);
		} else {
			gridDisplay.getPagination().setRecordCount(0);
			gridDisplay.setResultItems(new ArrayList());
		}
		return gridDisplay;
	}
	
	private String defaultWithCount = "select count(*) over() recordCount, ";
	
	protected String generatePagingSql(String oringinalSql, final PagingDisplay gridDisplay)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select * from (");
		sql.append("select rownum row_num, tbl_a.* from (");
		
		sql.append(oringinalSql);
		if(StringUtil.isNotEmpty(gridDisplay.getSearchCriteria().getOrderBy()))
				sql.append(" order by " + gridDisplay.getSearchCriteria().getOrderBy()
							+ " " + gridDisplay.getSearchCriteria().getOrderType());
		
		//sql.append(") tbl_a) tbl_b where tbl_b.row_num>=" + gridDisplay.getGridPaging().getStartRow());
		//sql.append(" and tbl_b.row_num<=" + gridDisplay.getGridPaging().getEndRow());
		sql.append(") tbl_a) tbl_b where tbl_b.row_num>=:startRow");
		sql.append(" and tbl_b.row_num<=:endRow");
		//sql.append(") tbl_b");
		logger.info("JDBC SQL statment: " + sql.toString());
		return sql.toString();
	}
	
	protected String fetchPagingSQL(String orgSql, final PagingDisplay gridDisplay)
	{
		/**
		 * select * from table where column=:condition order by column
		 * 
		 * select rownum row_num, * from table where rownum<=:endRow and column=:condition order by column
		 * 
		 * select * from (select rownum row_num, * from table where rownum<=:endRow and column=:condition order by column) tmp
		 * where tmp.row_num>=:startRow
		 * 
		 */		
		
		/*orgSql = StringUtil.lowerCase(orgSql);
		orgSql = StringUtil.trim(orgSql);
		orgSql = StringUtil.removeStart(orgSql, "select ");*/
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from (").append("select rownum row_num, tbl_a.* from(").append(orgSql);
		if(StringUtil.isNotEmpty(gridDisplay.getSearchCriteria().getOrderBy()))
			sql.append(" order by " + gridDisplay.getSearchCriteria().getOrderBy()
						+ " " + gridDisplay.getSearchCriteria().getOrderType());
		sql.append(") tbl_a where rownum<=:endRow ) tbl_b where row_num>=:startRow");
		
		logger.info("JDBC SQL statment: " + sql.toString());
		
		return sql.toString();		
	}
	
	public PagingDisplay fetchData(final PagingDisplay gridDisplay, String countQuery, String dataQuery,
	String queryBody) throws DataAccessException 
	{
		String sql = countQuery + queryBody;
		
		SearchCriteria criteria  = gridDisplay.getSearchCriteria();
		SqlParameterSource paramSource;
		if(criteria.getCriteria() instanceof Map)
		{
			paramSource = new MapSqlParameterSource((Map)criteria.getCriteria());
			((MapSqlParameterSource)paramSource).addValue("startRow", gridDisplay.getPagination().getStartRow());
			((MapSqlParameterSource)paramSource).addValue("endRow", gridDisplay.getPagination().getEndRow());
		}
		else
			paramSource = new BeanPropertySqlParameterSource(criteria.getCriteria());
		
		int count = getNamedJdbcTemplate().queryForObject(sql, paramSource, Integer.class);
		if(count>0)
		{
			gridDisplay.getPagination().setRecordCount(count);			
			List list = getNamedJdbcTemplate().queryForList(fetchPagingSQL(dataQuery+queryBody,gridDisplay), paramSource);
			/*List list = getNamedJdbcTemplate().query(generatePagingSql(dataQuery+queryBody, gridDisplay),
													paramSource, new GmBeanPropertyRowMapper(clazz));*/
			gridDisplay.setResultItems(list);
		}
		else
		{
			gridDisplay.getPagination().setRecordCount(0);
			gridDisplay.setResultItems(new ArrayList());
		}
		return gridDisplay;
	}
	
	/**
	 * 统计记录数与检索的sql语句分开
	 * @param gridDisplay
	 * @param countQuery
	 * @param dataQuery
	 * @param queryBody
	 * @return
	 * @throws DataAccessException
	 */
	public PagingDisplay fetchSeparateCountSelect(final PagingDisplay gridDisplay, String countQuery,String queryBody) throws DataAccessException 
			{
				SearchCriteria criteria  = gridDisplay.getSearchCriteria();
				SqlParameterSource paramSource;
				if(criteria.getCriteria() instanceof Map)
				{
					paramSource = new MapSqlParameterSource((Map)criteria.getCriteria());
					((MapSqlParameterSource)paramSource).addValue("startRow", gridDisplay.getPagination().getStartRow());
					((MapSqlParameterSource)paramSource).addValue("endRow", gridDisplay.getPagination().getEndRow());
				}
				else
					paramSource = new BeanPropertySqlParameterSource(criteria.getCriteria());
				
				int count = getNamedJdbcTemplate().queryForObject(countQuery, paramSource, Integer.class);
				if(count>0)
				{
					gridDisplay.getPagination().setRecordCount(count);			
					List list = getNamedJdbcTemplate().queryForList(fetchPagingSQL(queryBody,gridDisplay), paramSource);

					gridDisplay.setResultItems(list);
				}
				else
				{
					gridDisplay.getPagination().setRecordCount(0);
					gridDisplay.setResultItems(new ArrayList());
				}
				return gridDisplay;
			}
	
	/**
	 * It performs listing by paging base on search criteria.
	 * Implemented by JDBC API.
	 * <p>
	 * for example queryBody:
	 *  country_name, country_name_cn from gm$countries
	 * </p>
	 * it will be generate to:
	 * <p>
	 *  select count(*) over() recordCount, country_name, country_name_cn from gm$countries
	 * </p>
	 * you will find that the recordCount included.
	 * 
	 * @param gridDisplay
	 * @param queryBody a sql statement without "select" beginning
	 * @return PagingDisplay it contains result list with map.
	 */
	protected PagingDisplay getList(final PagingDisplay gridDisplay, String queryBody) throws DataAccessException 
	{
		String sql = defaultWithCount + queryBody;
		SearchCriteria criteria  = gridDisplay.getSearchCriteria();
		SqlParameterSource paramSource;
		if(criteria.getCriteria() instanceof Map)
		{
			paramSource = new MapSqlParameterSource((Map)criteria.getCriteria());
			((MapSqlParameterSource)paramSource).addValue("startRow", gridDisplay.getPagination().getStartRow());
			((MapSqlParameterSource)paramSource).addValue("endRow", gridDisplay.getPagination().getEndRow());
		}
		else
			paramSource = new BeanPropertySqlParameterSource(criteria.getCriteria());
		List list = getNamedJdbcTemplate().queryForList(generatePagingSql(sql,gridDisplay), paramSource);
		if(list.size()>0)
		{
			gridDisplay.getPagination().setRecordCount(((BigDecimal)((Map)list.get(0)).get("recordCount")).intValue());
			gridDisplay.setResultItems(list);
		}
		else
		{
			gridDisplay.getPagination().setRecordCount(0);
			gridDisplay.setResultItems(new ArrayList());
		}
		return gridDisplay;
	}

	/**
	 * It performs listing by paging base on search criteria.
	 * Implemented by JDBC API.
	 * 
	 * @param gridDisplay
	 * @param queryBody a full sql statement without "select" beginning
	 * @param class
	 * @return PagingDisplay it contains result list with class object.
	 */
	protected PagingDisplay getList(final PagingDisplay gridDisplay, String countQuery, String dataQuery,
			String queryBody) throws DataAccessException 
	{
		String sql = countQuery + queryBody;
		
		SearchCriteria criteria  = gridDisplay.getSearchCriteria();
		SqlParameterSource paramSource;
		if(criteria.getCriteria() instanceof Map)
		{
			paramSource = new MapSqlParameterSource((Map)criteria.getCriteria());
			((MapSqlParameterSource)paramSource).addValue("startRow", gridDisplay.getPagination().getStartRow());
			((MapSqlParameterSource)paramSource).addValue("endRow", gridDisplay.getPagination().getEndRow());
		}
		else
			paramSource = new BeanPropertySqlParameterSource(criteria.getCriteria());
		
		int count = getNamedJdbcTemplate().queryForObject(sql, paramSource, Integer.class);
		if(count>0)
		{
			gridDisplay.getPagination().setRecordCount(count);			
			List list = getNamedJdbcTemplate().queryForList(generatePagingSql(dataQuery+queryBody,gridDisplay), paramSource);
			/*List list = getNamedJdbcTemplate().query(generatePagingSql(dataQuery+queryBody, gridDisplay),
													paramSource, new GmBeanPropertyRowMapper(clazz));*/
			gridDisplay.setResultItems(list);
		}
		else
		{
			gridDisplay.getPagination().setRecordCount(0);
			gridDisplay.setResultItems(new ArrayList());
		}
		return gridDisplay;
	}

	/**
	 * It performs listing by paging base on search criteria.
	 * Implemented by JDBC API.
	 * 
	 * @param gridDisplay
	 * @param queryBody a full sql statement without "select" beginning
	 * @param class
	 * @return PagingDisplay it contains result list with class object.
	 */
	protected PagingDisplay getList(final PagingDisplay gridDisplay, String countQuery, String dataQuery,
			String queryBody, Class clazz) throws DataAccessException 
	{
		String sql = countQuery + queryBody;
		SearchCriteria criteria  = gridDisplay.getSearchCriteria();
		SqlParameterSource paramSource;
		if(criteria.getCriteria() instanceof Map)
		{
			paramSource = new MapSqlParameterSource((Map)criteria.getCriteria());
			((MapSqlParameterSource)paramSource).addValue("startRow", gridDisplay.getPagination().getStartRow());
			((MapSqlParameterSource)paramSource).addValue("endRow", gridDisplay.getPagination().getEndRow());
		}
		else
			paramSource = new BeanPropertySqlParameterSource(criteria.getCriteria());
		int count = getNamedJdbcTemplate().queryForObject(sql, paramSource, Integer.class);
		if(count>0)
		{
			gridDisplay.getPagination().setRecordCount(count);
			List list = getNamedJdbcTemplate().query(generatePagingSql(dataQuery+queryBody, gridDisplay),
													paramSource, new BeanPropertyRowMapper(clazz));
			gridDisplay.setResultItems(list);
		}
		else
		{
			gridDisplay.getPagination().setRecordCount(0);
			gridDisplay.setResultItems(new ArrayList());
		}
		return gridDisplay;
	}
	
	private String pagingSQL(String bodySQL, String sorting)
	{
		return "select * from ( select row_number() over(" + sorting + ") rownum, count(*) over() recordCount, " + bodySQL + ")"
				+ " as ptmp where ptmp.rownum>=:startRow limit :pageSize";
	}
	
	public <T extends BaseObject> PagingDisplay<T> pagingQuery(String sql, Pagination page, Class<T> classType) throws DataAccessException
	{
		return pagingQuery(sql, page, classType, new HashMap<>());
	}
	
	public <T extends BaseObject> PagingDisplay<T> pagingQuery(String sql, Pagination page, Class<T> classType, Object paramObject) throws DataAccessException
	{
		sql = pagingSQL(Pattern.compile(DaoConstants.SQL_Select,Pattern.CASE_INSENSITIVE)
								.matcher(sql).replaceFirst(" "), page.getSorting());
		
		PagingDisplay<T> grid = new PagingDisplay<>();		
		Map<String,Object> params = new HashMap<>();
		if(paramObject instanceof Map)
		{
			params = ((Map) paramObject);
		}
		if(paramObject instanceof BaseObject)
		{
			try
			{
				params = PropertyUtils.describe(paramObject);
			}
			catch (IllegalAccessException |InvocationTargetException |NoSuchMethodException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		params.put("pageSize", page.getPageSize());
		params.put("startRow", page.getStartRow());
		List<T> result = this.queryByJdbc(sql, classType, params);
		if(CollectionUtil.isNotEmpty(result))
		{
			page.setRecordCount(result.get(0).getRecordCount());
		}
		else
			page.setRecordCount(0);
		grid.setPagination(page);
		grid.setResultItems(result);
		return grid;
	}
	
	
	protected <T extends BaseObject> BeanPropertyRowMapper<T> registry(Class<T> classType)
	{
		BeanPropertyRowMapper<T> beanMapper = new BeanPropertyRowMapper<>(classType);
		ConverterRegistry registry = (ConverterRegistry)beanMapper.getConversionService();
		registry.addConverter(new PgArrayToArrayConverter(beanMapper.getConversionService()));
		return beanMapper;
	}
	
	public <T extends BaseObject> List<T> queryByJdbc(String sql, Class<T> classType, Object... paramValues) throws DataAccessException
	{
		return this.queryByJdbc(sql, registry(classType), paramValues);
	}
	
	public <T extends BaseObject> List<T> queryByJdbc(String sql, Class<T> classType, Map<String,?> params) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().query(sql, new MapSqlParameterSource(params), registry(classType));
	}	
	
	public <T extends BaseObject> List<T> queryByJdbc(String sql, Class<T> classType, BaseObject t) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(t), registry(classType));
	}	
	
	public <T extends BaseObject> List<T> queryByJdbc(String sql, RowMapper<T> rowMapper, Object...paramValues) throws DataAccessException
	{
		return this.getJdbcTemplate().query(sql, paramValues, rowMapper);
	}
	
	public <T extends BaseObject> T queryForObject(String sql, Class<T> classType, Object...paramValues) throws DataAccessException
	{
		return this.queryForObject(sql, registry(classType), paramValues);
	}
	
	public <T extends BaseObject> T queryForObject(String sql, Class<T> classType, Object t) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().queryForObject(sql, new BeanPropertySqlParameterSource(t), registry(classType));
	}
	
	public <T extends BaseObject> T queryForObject(String sql, RowMapper<T> rowMapper, Object...paramValues) throws DataAccessException
	{
		return this.getJdbcTemplate().queryForObject(sql, paramValues, rowMapper);
	}
	
	public <T extends BaseObject> T call(String sql, Object in, Class<T> out) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().call(sql, new BeanPropertySqlParameterSource(in), out);
	}
	
	public Map<String,?> call(String sql, Map<String,?> in, Map<String,Class<?>> out) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().call(sql, new MapSqlParameterSource(in), out);
	}
	
	public Map<String,?> call(String sql, Object in) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().call(sql, new BeanPropertySqlParameterSource(in));
	}
	
	public List<Map<String, Object>> callForList(String sql, Object in) throws DataAccessException
	{
		return this.getNamedJdbcTemplate().callForList(sql, new BeanPropertySqlParameterSource(in));
	}
	
	public <T extends BaseObject> T callForObject(String sql, Class<T> out, Object in) throws DataAccessException
	{
		return BeanUtil.dataToBean(this.call(sql, in), out);
	}

	public List<BaseObject> findByNamedParam(String queryString, Map<String,Object> param, boolean forceInit)
			throws DataAccessException {
		List<BaseObject> results = (List<BaseObject>)this.getHibernateTemplate().findByNamedParam(queryString, 
												param.keySet().toArray(new String[param.size()]), param.values().toArray());
		if(forceInit)
		{
			for(BaseObject entity : results)
				this.initializeProperties(entity);
		}
		return results;
	}

	public BaseObject findObjectByNamedParam(String queryString, Map<String,Object> param, boolean forceInit)
			throws DataAccessException {		
		List<BaseObject> results = (List<BaseObject>)this.getHibernateTemplate().findByNamedParam(queryString,
												param.keySet().toArray(new String[param.size()]), param.values().toArray());		
		BaseObject entity = DataAccessUtils.requiredSingleResult(results);
		if(forceInit)
			this.initializeProperties(entity);
		return entity;
	}
	
	public <R extends CommonRepository<T,ID>, T extends BaseObject, ID extends Serializable> R commonDao(Class<T> type, Class<ID> keyType)
	{
		R r = null;
		Map<String,CommonRepository> beans = ApplicationContextUtil.getApplicationContext().getBeansOfType(CommonRepository.class);
		for(Entry<String,CommonRepository> entry : beans.entrySet())
		{
			AdvisedSupport advised = AopUtil.getAdvised(entry.getValue());
			Type t = ((ParameterizedType)advised.getProxiedInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
			if(type.equals(t))
				r = (R)entry.getValue();
		}
		return r;
	}
	
	public <R extends CommonRepository<T,?>, T extends BaseObject> R commonDao(Class<T> type)
	{
		Class<?> keyClass = String.class;
		Field[] fields = type.getDeclaredFields();
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(Id.class))
			{
				keyClass = field.getType();
				break;
			}
		}
		R r = (R)commonDao(type, (Class<Serializable>)keyClass);
		return r;
	}

}
