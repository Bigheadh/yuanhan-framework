package com.zhjs.saas.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.zhjs.saas.core.dao.ParamWrapper;
import com.zhjs.saas.core.pojo.BaseObject;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-10-11
 * @modified:	2017-10-11
 * @version:	
 */
public abstract class NamedParameterUtil extends NamedParameterUtils
{
	
	private static Logger logger = LoggerFactory.getLogger(NamedParameterUtil.class);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private NamedParameterUtil(){}
	
	/**
	 * Convert a Map of named parameter values to a corresponding array.
	 * @param parsedObject the parsed SQL statement
	 * @param paramSource the source for all the named in parameters
	 * @param declaredParams the List of declared SqlParameter objects
	 * (may be {@code null}). If specified, the parameter metadata will
	 * be built into the value array in the form of SqlParameterValue objects.
	 * @return the array of values
	 */
	public static Object[] buildProcedureValueArray(
			NamedParameterObject parsedObject, SqlParameterSource paramSource, List<SqlParameter> declaredParams)
	{
		List<Object> values = new ArrayList<>();
		List<String> paramNames = parsedObject.getParameterNames();
		for (int i = 0; i < paramNames.size(); i++) {
			String paramName = paramNames.get(i);
			try {
				values.add(paramSource.getValue(paramName));
			}
			catch (IllegalArgumentException ex) {
				logger.warn("No value supplied for the SQL parameter '{}': {}", paramName, ex.getMessage());
				break;
			}
		}
		return values.toArray();
	}

	/**
	 * Convert parameter declarations from an SqlParameterSource to a corresponding List of SqlParameters.
	 * This is necessary in order to reuse existing methods on JdbcTemplate.
	 * The SqlParameter for a named parameter is placed in the correct position in the
	 * resulting list based on the parsed SQL statement info.
	 * @param parsedSql the parsed SQL statement
	 * @param inParams the source for all the in parameters
	 * @param ParamWrapper the return bean class or map for all the out parameters
	 */
	public static <T extends BaseObject> List<SqlParameter> buildProcedureParameterList(NamedParameterObject parsedObject,
			SqlParameterSource inParams, ParamWrapper outParams)
	{
		List<String> paramNames = parsedObject.getParameterNames();
		List<SqlParameter> params = new LinkedList<SqlParameter>();
		for (int i = 0; i < paramNames.size(); i++) {
			String paramName = paramNames.get(i);
			boolean out = false;
			Class<?> paramType = null;
			if(outParams.isBeanWrap())
			{
				Field field = FieldUtils.getDeclaredField(outParams.getOutType(), paramName, true);
				if(out=(field!=null))
					paramType=field.getType();
			}
			else
			{
				if(out=(outParams.getOutMap()!=null&&outParams.getOutMap().containsKey(paramName)))
					paramType=outParams.getOutMap().get(paramName);
			}
			if(inParams.hasValue(paramName))
			{
				if(out)
					params.add(new SqlInOutParameter(paramName, inParams.getSqlType(paramName), inParams.getTypeName(paramName)));
				else
					params.add(new SqlParameter(paramName, inParams.getSqlType(paramName), inParams.getTypeName(paramName)));
			}
			else if(out)
			{
				params.add(new SqlOutParameter(paramName, StatementCreatorUtils.javaTypeToSqlParameterType(paramType)));
			}
			else
				logger.warn("No compatiable field or key from {} for the procedure parameter '{}' so that saas-framework will skip it.",
						outParams.isBeanWrap()?outParams.getOutType().getName():"outParameter Map", paramName);
		}
		return params;
	}

	/**
	 * Parse the SQL statement and locate any placeholders or named parameters. Named
	 * parameters are substituted for a JDBC placeholder, and any select list is expanded
	 * to the required number of placeholders. Select lists may contain an array of
	 * objects, and in that case the placeholders will be grouped and enclosed with
	 * parentheses. This allows for the use of "expression lists" in the SQL statement
	 * like: <br /><br />
	 * {@code select id, name, state from table where (name, age) in (('John', 35), ('Ann', 50))}
	 * <p>The parameter values passed in are used to determine the number of placeholders to
	 * be used for a select list. Select lists should be limited to 100 or fewer elements.
	 * A larger number of elements is not guaranteed to be supported by the database and
	 * is strictly vendor-dependent.
	 * @param parsedObject the parsed representation of the SQL statement
	 * @param paramSource the source for named parameters
	 * @return the SQL statement with substituted parameters
	 * @see #parseSqlStatement
	 */
	public static String parseNamedParameters(NamedParameterObject parsedObject, SqlParameterSource paramSource) {
		String originalSql = parsedObject.getOriginalInput();
		StringBuilder actualSql = new StringBuilder();
		List<String> paramNames = parsedObject.getParameterNames();
		int lastIndex = 0;
		for (int i = 0; i < paramNames.size(); i++) {
			String paramName = paramNames.get(i);
			int[] indexes = parsedObject.getParameterIndexes(i);
			int startIndex = indexes[0];
			int endIndex = indexes[1];
			actualSql.append(originalSql, lastIndex, startIndex);
			if (paramSource != null && paramSource.hasValue(paramName)) {
				Object value = paramSource.getValue(paramName);
				if (value instanceof SqlParameterValue) {
					value = ((SqlParameterValue) value).getValue();
				}
				if (value instanceof Collection) {
					Iterator<?> entryIter = ((Collection<?>) value).iterator();
					int k = 0;
					while (entryIter.hasNext()) {
						if (k > 0) {
							actualSql.append(", ");
						}
						k++;
						Object entryItem = entryIter.next();
						if (entryItem instanceof Object[]) {
							Object[] expressionList = (Object[]) entryItem;
							actualSql.append("(");
							for (int m = 0; m < expressionList.length; m++) {
								if (m > 0) {
									actualSql.append(", ");
								}
								actualSql.append("?");
							}
							actualSql.append(")");
						}
						else {
							actualSql.append("?");
						}
					}
				}
				else {
					actualSql.append("?");
				}
			}
			else {
				actualSql.append("?");
			}
			lastIndex = endIndex;
		}
		actualSql.append(originalSql, lastIndex, originalSql.length());
		return actualSql.toString();
	}

}
