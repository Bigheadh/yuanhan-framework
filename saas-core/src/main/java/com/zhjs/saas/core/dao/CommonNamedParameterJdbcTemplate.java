package com.zhjs.saas.core.dao;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetSupportingSqlParameter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.SqlReturnUpdateCount;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.pojo.BaseObject;
import com.zhjs.saas.core.util.BeanUtil;
import com.zhjs.saas.core.util.NamedParameterObject;
import com.zhjs.saas.core.util.NamedParameterUtil;
import com.zhjs.saas.core.util.PropertiesLoaderUtil;
import com.zhjs.saas.core.util.StringUtil;


/**
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public class CommonNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String RETURN_RESULT_SET_PREFIX = "#result-set-";

	private static final String RETURN_UPDATE_COUNT_PREFIX = "#update-count-";

	public CommonNamedParameterJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}
	public CommonNamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
		super(classicJdbcTemplate);
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
	
	private void mapping(Class<?> clazz, Map<String,String> mappedFields)
	{//SequenceGenerator
		PropertyDescriptor[] pds = BeanUtil.getPropertyDescriptors(clazz);
		for (PropertyDescriptor pd : pds) {
			Column annotation;
			Field field;
			try
			{
				field = clazz.getDeclaredField(pd.getName());
			}
			catch (Exception e)
			{
				continue;
			}
			if(!field.isAnnotationPresent(Column.class))
				continue;
			annotation = field.getAnnotation(Column.class);			
			if(StringUtil.isNotEmpty(annotation.name()))
			{
				mappedFields.put(annotation.name(), pd.getName());
				continue;
			}
			
			Class<?> type = pd.getPropertyType();
			if (pd.getWriteMethod()!=null && pd.getReadMethod()!=null 
					&& !Class.class.isAssignableFrom(type)
					&& !BaseObject.class.isAssignableFrom(type)
					&& !Collection.class.isAssignableFrom(type)
					&& !org.hibernate.mapping.Collection.class.isAssignableFrom(type)
					&& !PersistentCollection.class.isAssignableFrom(type)) {
				//mappedFields.put(pd.getName().toLowerCase(), pd);
				//mappedFields.put(pd.getName().toLowerCase(), pd.getName());
				String underscoredName = underscoreName(pd.getName());
				//if (!pd.getName().toLowerCase().equals(underscoredName)) {
					//mappedFields.put(underscoredName, pd);
					mappedFields.put(underscoredName, pd.getName());
				//}
			}
		}
	}
	
	private String generateDelete(String tblName, String idField, Object entity, Map<String,String> mappedFields)
	{
		String idColumn = "";
		for(String column : mappedFields.keySet())
		{
			if(mappedFields.get(column).equals(idField))
			{
				idColumn = column;
				break;
			}
		}
		StringBuilder sqlCmd = new StringBuilder("delete from ");
		sqlCmd.append(tblName).append(" where ").append(idColumn).append("=:").append(idField);
		String sql = sqlCmd.toString();
		logger.info("deleteObject==> " + sql);
		return sql;
	}
	
	private String generateUpdate(String tblName, String idField, Object entity, Map<String,String> mappedFields)
	{
		StringBuilder sqlCmd = new StringBuilder("update ");
		sqlCmd.append(tblName).append(" set ");
		String idColumn = "";
		for(String column : mappedFields.keySet())
		{
			String field = mappedFields.get(column);
			if(!field.equals(idField))
			{
				sqlCmd.append(column).append("=");
				sqlCmd.append(":").append(field).append(", ");
			}
			else
				idColumn = column;
		}
		sqlCmd.delete(sqlCmd.length()-2,sqlCmd.length());
		sqlCmd.append(" where ").append(idColumn).append("=:").append(idField);
		String sql = sqlCmd.toString();
		logger.info("updateObject==> " + sql);
		return sql;
	}
	
	private String generateInsert(String tblName, Object entity, Map<String,String> mappedFields)
	{
		//BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(entity);
		StringBuilder column = new StringBuilder(" (");
		StringBuilder values = new StringBuilder(" (");
		for(String field : mappedFields.keySet())
		{
			column.append(field).append(", ");
			//values.append(bw.getPropertyValue(mappedFields.get(field).getName()));
			values.append(":").append(mappedFields.get(field)).append(", ");
		}
		column.delete(column.length()-2,column.length()).append(") ");
		values.delete(values.length()-2,column.length()).append(") ");
		String sql = "insert into " + tblName + column.toString() + "values" + values.toString();
		logger.info("insertObject==> " + sql);
		return sql;
	}

	/**
	 * @param <T>
	 * @param entity
	 * @return
	 * @throws TableUndefinedException
	 */
	private <T extends BaseObject> String analysisTable(T entity) throws TableUndefinedException {
		String tblName;
		Class<?> type = entity.getClass();
		if(type.isAnnotationPresent(Table.class))
		{
			tblName = type.getAnnotation(Table.class).name();
		}
		else
			throw new TableUndefinedException("The @Table annotation and table name must be defined in the "+type.getSimpleName()+" class.");
		if(StringUtil.isEmpty(tblName))
			throw new TableUndefinedException("Table name must be set in @Table annotation in "+type.getSimpleName()+" class.");
		return tblName;
	}
	
	private <T extends BaseObject> String analysisIdColumn(T entity) throws TableUndefinedException {
		String idField = "";
		Class<?> type = entity.getClass();
		// the best way to get property should use BeanUtil.getPropertyDescriptors(class),
		// because it can return all super class's properties by hierarchy.
		Field[] fields = type.getDeclaredFields();
		for(Field field : fields)
		{
			/*if(field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class))
			{
				idField = field.getAnnotation(Column.class).name();
				break;
			}*/
			if(field.isAnnotationPresent(Id.class))
			{
				idField = underscoreName(field.getName());
				break;
			}
		}
		if(StringUtil.isEmpty(idField))
			throw new TableUndefinedException("The @Id annotation for identity must be defined in the "+type.getSimpleName()+" class.");
		
		return idField;
	}
	
	/**
	 * get sequence for jdbc
	 * @param sqName sequence name
	 * @return
	 */
	public Long getSequenceId(String sqName) throws TableUndefinedException {
		StringBuilder strSql = new StringBuilder();
		strSql.append(" SELECT "+sqName+".NEXTVAL");
		strSql.append(" FROM DUAL");
		Long Id = this.getJdbcOperations().queryForObject(strSql.toString(), Long.class);	
		if(Id<=0)
			throw new TableUndefinedException("Wrong value for sequence is generated, as the value is less then 1.");
		return Id;
	}
	
	private <T extends BaseObject> String analysisId(T entity) throws TableUndefinedException {
		return this.analysisId(entity, true);
	}
	
	/**
	 * @param <T>
	 * @param entity
	 * @return
	 * @throws TableUndefinedException
	 */
	private <T extends BaseObject> String analysisId(T entity, boolean generateValue) throws TableUndefinedException {
		String idField = "";
		Class<?> type = entity.getClass();
		// the best way to get property should use BeanUtil.getPropertyDescriptors(class),
		// because it can return all super class's properties by hierarchy.
		Field[] fields = type.getDeclaredFields();
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(Id.class))
			{
				idField = field.getName();
				if(generateValue)
					if(field.isAnnotationPresent(SequenceGenerator.class))
					{
						field.setAccessible(true);
						Long id = getSequenceId(field.getAnnotation(SequenceGenerator.class).sequenceName());
						try {
							field.set(entity, id);
						} catch (Exception e) {
							e.printStackTrace();
							throw new TableUndefinedException("Error occurred when @SequenceGenerator set value to "+type.getSimpleName()+" class.");
						}
					}
					else
						throw new TableUndefinedException("The @SequenceGenerator annotation for sequence value generating" +
								" must be defined in the "+type.getSimpleName()+" class.");
				break;
			}
		}
		if(StringUtil.isEmpty(idField))
			throw new TableUndefinedException("The @Id annotation for identity must be defined in the "+type.getSimpleName()+" class.");
		
		return idField;
	}
	
	public <T extends BaseObject> void deleteObject(T entity) throws DataAccessException
	{
		String tblName = analysisTable(entity);
		String idField = analysisId(entity, false);
		deleteObject(entity, tblName, idField);		
	}
	
	public int deleteObject(BaseObject entity, String tblName, String idField) throws DataAccessException
	{
		Map<String,String> mappedFields = new HashMap<String,String>();
		mapping(entity.getClass(), mappedFields);
		String sql = generateDelete(tblName, idField, entity, mappedFields);
		return this.update(sql, new BeanPropertySqlParameterSource(entity));
	}

	
	/**
	 * only single table update is support for now.
	 * 
	 * @param <T>
	 * @param entity
	 * @param tblName
	 * @return entity if save successfully, otherwise return null
	 */
	public <T extends BaseObject> T updateObject(T entity) throws DataAccessException
	{
		String tblName = analysisTable(entity);
		String idField = analysisId(entity, false);
		return updateObject(entity, tblName, idField);
	}
	
	/**
	 * only single table update is support for now.
	 * 
	 * @param <T>
	 * @param entity
	 * @param tblName
	 * @return entity if save successfully, otherwise return null
	 */
	public <T extends BaseObject> T updateObject(T entity, String tblName, String idField) throws DataAccessException
	{
		/*Map<String,String> mappedFields = new HashMap<String,String>();
		mapping(entity.getClass(), mappedFields);
		String sql = generateUpdate(tblName, idField, entity, mappedFields);
		int result = this.update(sql, new BeanPropertySqlParameterSource(entity));
		return result>0?entity:null;*/
		int result = this.modifyObject(entity, tblName, idField);
		return result>0?entity:null;
	}
	
	public <T extends BaseObject> int modifyObject(T entity, String tblName, String idField) throws DataAccessException
	{
		Map<String,String> mappedFields = new HashMap<String,String>();
		mapping(entity.getClass(), mappedFields);
		String sql = generateUpdate(tblName, idField, entity, mappedFields);
		return this.update(sql, new BeanPropertySqlParameterSource(entity));		
	}
	
	/**
	 * only single table insert is support for now.
	 * 
	 * @param <T>
	 * @param entity
	 * @param tblName
	 * @return entity if save successfully, otherwise return null
	 */
	public <T extends BaseObject> T saveObject(T entity) throws DataAccessException
	{
		String tblName = analysisTable(entity);
		analysisId(entity);
		return saveObject(entity, tblName);
	}
	
	/**
	 * only single table insert is support for now.
	 * 
	 * @param <T>
	 * @param entity
	 * @param tblName
	 * @return entity if save successfully, otherwise return null
	 */
	public <T extends BaseObject> T saveObject(T entity, String tblName) throws DataAccessException
	{
		/*Map<String,String> mappedFields = new HashMap<String,String>();
		mapping(entity.getClass(), mappedFields);
		String sql = generateInsert(tblName, entity, mappedFields);
		int result = this.update(sql, new BeanPropertySqlParameterSource(entity));
		return result>0?entity:null;*/
		int result = this.insertObject(entity, tblName);
		return result>0?entity:null;
	}
	
	public int insertObject(BaseObject entity, String tblName) throws DataAccessException
	{
		Map<String,String> mappedFields = new HashMap<String,String>();
		mapping(entity.getClass(), mappedFields);
		String sql = generateInsert(tblName, entity, mappedFields);
		return this.update(sql, new BeanPropertySqlParameterSource(entity));
	}
	
	public <T extends BaseObject> T loadObject(Class<T> entityClass, Serializable pk) throws DataAccessException
	{
		T entity = BeanUtil.instantiateClass(entityClass);
		String tblName = analysisTable(entity);
		String idField = analysisId(entity, false);
		String column = analysisIdColumn(entity);
		Map<String,Serializable> paramMap = new HashMap<String,Serializable>();
		paramMap.put(idField, pk);
		String sql = "select * from " + tblName + " where " + column + " =:" + idField;
		try
		{
			entity = (T)this.queryForObject(sql, paramMap, new BeanPropertyRowMapper<T>(entityClass));
		}
		catch(EmptyResultDataAccessException e)
		{
			return null;
		}
		return entity;
	}	
	
	public Map<String,?> call(String sql, SqlParameterSource paramSource) throws DataAccessException
	{
		return this.queryForMap(sql, paramSource);
	}		
	
	public List<Map<String, Object>> callForList(String sql, SqlParameterSource paramSource) throws DataAccessException
	{
		return this.queryForList(sql, paramSource);
	}	
	
	public Map<String,?> call(String sql, SqlParameterSource inParameters, Map<String,Class<?>> outParameters) throws DataAccessException
	{
		return this.callProcedure(sql, inParameters, new ParamWrapper(outParameters));
	}

	public <T extends BaseObject> T call(String sql, SqlParameterSource inParameters, Class<T> outType)
			throws DataAccessException 
	{
		return BeanUtil.dataToBean(callProcedure(sql, inParameters, new ParamWrapper(outType)), outType);
	}

	public <T extends BaseObject> Map<String,?> callProcedure(String sql, SqlParameterSource inParameters, ParamWrapper outWrapper)
			throws DataAccessException 
	{
		NamedParameterObject parsedSql = PropertiesLoaderUtil.parseToNamedString(sql);
		String sqlToUse = NamedParameterUtil.parseNamedParameters(parsedSql, inParameters);
		Object[] params = NamedParameterUtil.buildProcedureValueArray(parsedSql, inParameters, null);
		List<SqlParameter> declaredParameters = NamedParameterUtil.buildProcedureParameterList(parsedSql, inParameters, outWrapper);
		CallableStatementCreatorFactory cscf = new CallableStatementCreatorFactory(sqlToUse, declaredParameters);
		cscf.setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
		Map<String,Object> paramMap = new HashMap<>();
		for(int i=0; i<params.length; i++)
		{
			paramMap.put(declaredParameters.get(i).getName(), params[i]);
		}
		final List<SqlParameter> updateCountParameters = new ArrayList<SqlParameter>();
		final List<SqlParameter> resultSetParameters = new ArrayList<SqlParameter>();
		final List<SqlParameter> callParameters = new ArrayList<SqlParameter>();
		for (SqlParameter parameter : declaredParameters) {
			if (parameter.isResultsParameter()) {
				if (parameter instanceof SqlReturnResultSet) {
					resultSetParameters.add(parameter);
				}
				else {
					updateCountParameters.add(parameter);
				}
			}
			else {
				callParameters.add(parameter);
			}
		}
		return getJdbcOperations().execute(cscf.newCallableStatementCreator(paramMap), cs -> {
					boolean retVal = cs.execute();
					int updateCount = cs.getUpdateCount();
					logger.debug("CallableStatement.execute() returned '{}'", retVal);
					logger.debug("CallableStatement.getUpdateCount() returned {}", updateCount);
					Map<String, Object> returnedResults = createResultsMap();
					if (retVal || updateCount != -1) {
						returnedResults.putAll(extractReturnedResults(cs, updateCountParameters, resultSetParameters, updateCount));
					}
					returnedResults.putAll(extractOutputParameters(cs, callParameters));
					/*
					ResultSet rsToUse = cs.getResultSet();
					return DataAccessUtils.requiredSingleResult(
							new RowMapperResultSetExtractor<T>(new BeanPropertyRowMapper<T>(classType)).extractData(rsToUse));*/
					return returnedResults;
				});
		//return BeanUtil.dataToBean(getJdbcOperations().call(cscf.newCallableStatementCreator(paramMap), declaredParameters), classType);
	}
	
	protected <T extends BaseObject> CallableStatementCreator getProcedurePreparedStatementCreator(
			String sql, SqlParameterSource paramSource, Class<T> classType)
	{
		NamedParameterObject parsedSql = PropertiesLoaderUtil.parseToNamedString(sql);
		String sqlToUse = NamedParameterUtil.parseNamedParameters(parsedSql, paramSource);
		Object[] params = NamedParameterUtil.buildProcedureValueArray(parsedSql, paramSource, null);
		List<SqlParameter> declaredParameters = NamedParameterUtil.buildProcedureParameterList(parsedSql, paramSource, new ParamWrapper(classType));
		CallableStatementCreatorFactory cscf = new CallableStatementCreatorFactory(sqlToUse, declaredParameters);
		Map<String,Object> paramMap = new HashMap<>();
		for(int i=0; i<params.length; i++)
		{
			paramMap.put(declaredParameters.get(i).getName(), params[i]);
		}
		return cscf.newCallableStatementCreator(paramMap);
	}
	
	
	protected Map<String, Object> createResultsMap() {
		if (((JdbcTemplate)this.getJdbcOperations()).isResultsMapCaseInsensitive()) {
			return new LinkedCaseInsensitiveMap<Object>();
		}
		else {
			return new LinkedHashMap<String, Object>();
		}
	}


	/**
	 * Extract returned ResultSets from the completed stored procedure.
	 * @param cs JDBC wrapper for the stored procedure
	 * @param updateCountParameters Parameter list of declared update count parameters for the stored procedure
	 * @param resultSetParameters Parameter list of declared resultSet parameters for the stored procedure
	 * @return Map that contains returned results
	 */
	protected Map<String, Object> extractReturnedResults(CallableStatement cs,
			List<SqlParameter> updateCountParameters, List<SqlParameter> resultSetParameters, int updateCount)
			throws SQLException {

		Map<String, Object> returnedResults = new HashMap<String, Object>();
		int rsIndex = 0;
		int updateIndex = 0;
		boolean moreResults;
		if (!((JdbcTemplate)this.getJdbcOperations()).isSkipResultsProcessing()) {
			do {
				if (updateCount == -1) {
					if (resultSetParameters != null && resultSetParameters.size() > rsIndex) {
						SqlReturnResultSet declaredRsParam = (SqlReturnResultSet) resultSetParameters.get(rsIndex);
						returnedResults.putAll(processResultSet(cs.getResultSet(), declaredRsParam));
						rsIndex++;
					}
					else {
						if (!((JdbcTemplate)this.getJdbcOperations()).isSkipUndeclaredResults()) {
							String rsName = RETURN_RESULT_SET_PREFIX + (rsIndex + 1);
							SqlReturnResultSet undeclaredRsParam = new SqlReturnResultSet(rsName, new ColumnMapRowMapper());
							if (logger.isDebugEnabled()) {
								logger.debug("Added default SqlReturnResultSet parameter named '" + rsName + "'");
							}
							returnedResults.putAll(processResultSet(cs.getResultSet(), undeclaredRsParam));
							rsIndex++;
						}
					}
				}
				else {
					if (updateCountParameters != null && updateCountParameters.size() > updateIndex) {
						SqlReturnUpdateCount ucParam = (SqlReturnUpdateCount) updateCountParameters.get(updateIndex);
						String declaredUcName = ucParam.getName();
						returnedResults.put(declaredUcName, updateCount);
						updateIndex++;
					}
					else {
						if (!((JdbcTemplate)this.getJdbcOperations()).isSkipUndeclaredResults()) {
							String undeclaredName = RETURN_UPDATE_COUNT_PREFIX + (updateIndex + 1);
							if (logger.isDebugEnabled()) {
								logger.debug("Added default SqlReturnUpdateCount parameter named '" + undeclaredName + "'");
							}
							returnedResults.put(undeclaredName, updateCount);
							updateIndex++;
						}
					}
				}
				moreResults = cs.getMoreResults();
				updateCount = cs.getUpdateCount();
				if (logger.isDebugEnabled()) {
					logger.debug("CallableStatement.getUpdateCount() returned " + updateCount);
				}
			}
			while (moreResults || updateCount != -1);
		}
		return returnedResults;
	}


	/**
	 * Extract output parameters from the completed stored procedure.
	 * @param cs JDBC wrapper for the stored procedure
	 * @param parameters parameter list for the stored procedure
	 * @return Map that contains returned results
	 */
	protected Map<String, Object> extractOutputParameters(CallableStatement cs, List<SqlParameter> parameters)
			throws SQLException {

		Map<String, Object> returnedResults = new HashMap<String, Object>();
		int sqlColIndex = 1;
		for (SqlParameter param : parameters) {
			if (param instanceof SqlOutParameter) {
				SqlOutParameter outParam = (SqlOutParameter) param;
				if (outParam.isReturnTypeSupported()) {
					Object out = outParam.getSqlReturnType().getTypeValue(
							cs, sqlColIndex, outParam.getSqlType(), outParam.getTypeName());
					returnedResults.put(outParam.getName(), out);
				}
				else {
					Object out = cs.getObject(sqlColIndex);
					if (out instanceof ResultSet) {
						if (outParam.isResultSetSupported()) {
							returnedResults.putAll(processResultSet((ResultSet) out, outParam));
						}
						else {
							String rsName = outParam.getName();
							SqlReturnResultSet rsParam = new SqlReturnResultSet(rsName, new ColumnMapRowMapper());
							returnedResults.putAll(processResultSet((ResultSet) out, rsParam));
							if (logger.isDebugEnabled()) {
								logger.debug("Added default SqlReturnResultSet parameter named '" + rsName + "'");
							}
						}
					}
					else {
						returnedResults.put(outParam.getName(), out);
					}
				}
			}
			if (!(param.isResultsParameter())) {
				sqlColIndex++;
			}
		}
		return returnedResults;
	}

	/**
	 * Process the given ResultSet from a stored procedure.
	 * @param rs the ResultSet to process
	 * @param param the corresponding stored procedure parameter
	 * @return Map that contains returned results
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Map<String, Object> processResultSet(ResultSet rs, ResultSetSupportingSqlParameter param) throws SQLException {
		if (rs == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> returnedResults = new HashMap<String, Object>();
		try {
			ResultSet rsToUse = rs;
			if (param.getRowMapper() != null) {
				RowMapper rowMapper = param.getRowMapper();
				Object result = (new RowMapperResultSetExtractor(rowMapper)).extractData(rsToUse);
				returnedResults.put(param.getName(), result);
			}
			else if (param.getRowCallbackHandler() != null) {
				RowCallbackHandler rch = param.getRowCallbackHandler();
				while (rsToUse.next()) {
					rch.processRow(rsToUse);
				}
				returnedResults.put(param.getName(), "ResultSet returned from stored procedure was processed");
			}
			else if (param.getResultSetExtractor() != null) {
				Object result = param.getResultSetExtractor().extractData(rsToUse);
				returnedResults.put(param.getName(), result);
			}
		}
		finally {
			JdbcUtils.closeResultSet(rs);
		}
		return returnedResults;
	}
	
}
