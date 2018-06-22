package com.yuanhan.yuanhan.core.dao.sql;

import static com.yuanhan.yuanhan.core.config.PropertyConfig.Global_DateTimeFormat;
import static com.yuanhan.yuanhan.core.config.PropertyConfig.SQL_Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.env.Environment;

import com.yuanhan.yuanhan.core.dao.DaoConstants;
import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;
import com.yuanhan.yuanhan.core.pojo.BaseObject;
import com.yuanhan.yuanhan.core.util.ApplicationContextUtil;
import com.yuanhan.yuanhan.core.util.BeanUtil;
import com.yuanhan.yuanhan.core.util.StringUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-07-10
 * @modified:	2017-07-10
 * @version:	
 */
public class SQLBuilder
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static SimpleDateFormat df;
	private static String comment = "--";
	
	private String sql = "";	
	private StringBuilder whereCondition = new StringBuilder();
	private String[][] additionColumns;
	private boolean ingoreNext = false;
	
	private Map<String,String> namedParam;
	

	public SQLBuilder(String sqlScript)
	{
		this(sqlScript, new HashMap<>());
	}
	 
	/**
	 * it will load SQL script file by <code><b><i>SQL.loadSQL()</i></b></code>
	 * 
	 * @param sqlScript the SQL file
	 * @param namedParam named parameter hash map
	 */
	public SQLBuilder(String sqlScript, Map<String,String> namedParam)
	{
		initialize();
		try
		{
			this.sql = SQL.loadSQL(sqlScript);
		} catch (Exception e)
		{
			logger.error("Error occured when try to load SQL file.", e);
		}
		if(namedParam!=null)
			this.namedParam = namedParam;
		else
			this.namedParam = new HashMap<>();
	}		
	 
	/**
	 * it will load SQL script file by <code><b><i>SQL.loadSQL()</i></b></code>
	 * 
	 * @param sqlScript the SQL file
	 * @param object object will convert to hashmap
	 */
	public SQLBuilder(String sqlScript, BaseObject object)
	{
		initialize();
		try
		{
			this.sql = SQL.loadSQL(sqlScript);
		} catch (Exception e)
		{
			logger.error("Error occured when try to load SQL file.", e);
		}
		if(object!=null) 
		{
			try
			{
				this.namedParam = BeanUtil.describe(object);
			} catch (Exception e)
			{
				logger.error("Cannot convert a bean to HashMap", e);
				this.namedParam = new HashMap<>();
			}
		}
		else
			this.namedParam = new HashMap<>();
	}
	
	private void initialize()
	{
		Environment env = ApplicationContextUtil.getApplicationContext().getEnvironment();
		if( env!=null && StringUtil.isNotBlank(env.getProperty(Global_DateTimeFormat)) )
		{
			df = new SimpleDateFormat(env.getProperty(Global_DateTimeFormat));
		}
		else
		{
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		if( env!=null && StringUtil.isNotBlank(env.getProperty(SQL_Comment)))
			comment = env.getProperty(SQL_Comment);
	}
	
	public SQLBuilder elseNext()
	{
		return this;
	}
	
	/**
	 * greater than 
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder gt(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
			whereCondition.append(" and " + column + " > :" + key);
		return this;
	}
	
	/**
	 * less than
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder lt(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " < :" + key);
		}
		return this;
	}	
	
	/**
	 * greater than or equal to
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder ge(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
			whereCondition.append(" and " + column + " >= :" + key);
		return this;
	}
	
	/**
	 * less than or equal to
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder le(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " <= :" + key);
		}
		return this;
	}	
	
	/**
	 * greater than 
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder gtDate(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
			whereCondition.append(" and " + column + " >= :" + key);
		return this;
	}
	
	/**
	 * less than
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder ltDate(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			String newKey = key+"Next";
			namedParam.put(newKey, nextDate(namedParam.get(key)));
			whereCondition.append(" and " + column + " < :" + newKey);
		}
		return this;
	}
	
	/**
	 * be equals to
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder eq(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " = :" + key);
		}
		return this;
	}
	
	/**
	 * be equals to
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder equal(String column, String key)
	{
		return eq(column, key);
	}
	
	/**
	 * not equals to
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder notEqual(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " = :" + key);
		}
		return this;
	}
	
	/**
	 * be in (val1, val2, val3...)
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder in(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " in ( :" + key +" )");
		}
		return this;
	}
	
	/**
	 * be in (val1, val2, val3...)
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder ifIn(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " in ( :" + key +" )");
			this.ingoreNext = true;
		}
		return this;
	}
	
	/**
	 * be in (val1, val2, val3...)
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder elseIn(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)) && !ingoreNext)
		{
			whereCondition.append(" and " + column + " in ( :" + key +" )");
		}
		return this;
	}
	
	/**
	 * be in (val1, val2, val3...)
	 * 
	 * @param column
	 * @param key
	 * @return
	 */
	public SQLBuilder endIf()
	{
		this.ingoreNext = false;
		return this;
	}
	
	/**
	 * between startVal and endVal. 
	 * Note that including start and end
	 * 
	 * @param column
	 * @param keyStart
	 * @param keyEnd
	 * @return
	 */
	public SQLBuilder between(String column, String keyStart, String keyEnd)
	{
		if(StringUtil.isNotBlank(namedParam.get(keyStart))
				&& StringUtil.isNotBlank(namedParam.get(keyEnd)))
		{
			whereCondition.append(" and " + column + " between :" + keyStart +" and :" + keyEnd);
		}
		return this;
	}
	
	public SQLBuilder like(String column, String key)
	{
		if(StringUtil.isNotBlank(namedParam.get(key)))
		{
			whereCondition.append(" and " + column + " like '%'|| :" + key +" ||'%'");
		}
		return this;
	}
	
	/**
	 * append any condition that are not supported by SQLBuilder.in().like().lt().gt().eq()
	 * 
	 * @param condition
	 * @return
	 */
	public SQLBuilder append(String condition)
	{
		whereCondition.append(condition);
		return this;
	}
	
	/**
	 * left brackets, such as, such as
	 *  <pre>select * from table where col1='111' or <b><i>(</i></b>col2='222' and col3='333') </pre>
	 * @return
	 */
	public SQLBuilder lb()
	{
		whereCondition.append(" (");
		return this;
	}
	
	/**
	 * right brackets, such as
	 *  <pre>select * from table where col1='111' or (col2='222' and col3='333'<b><i>)</i></b> </pre>
	 * @return
	 */
	public SQLBuilder rb()
	{
		whereCondition.append(" )");
		return this;
	}
	
	/**
	 * additional columns need to display, but by your customized. Such as
	 * <pre>
	 * 	select addCol1 col1, addCol2 col2, t.* from table t
	 * </pre>
	 * 
	 * @param additionColumns
	 * @return
	 */
	public SQLBuilder additionColumns(String[][] additionColumns)
	{
		this.additionColumns = additionColumns;
		return this;
	}
	
	public String generate()
	{
		String returnSQL = new String(sql);
		returnSQL = Pattern.compile(comment+DaoConstants.SQL_WhereCondition,Pattern.CASE_INSENSITIVE)
				.matcher(returnSQL).replaceFirst(whereCondition.toString());
		if(additionColumns!=null)
		{
			String displayColumns = "";
			for(String[] column : additionColumns)
				displayColumns += "'"+column[1]+"' "+column[0]+", ";
			returnSQL = Pattern.compile(DaoConstants.SQL_Select,Pattern.CASE_INSENSITIVE)
					.matcher(returnSQL).replaceFirst(DaoConstants.SQL_Select+" "+displayColumns);
		}
		return returnSQL;
	}
	
	public String toSQL()
	{
		return generate();
	}
	
	public String toString()
	{
		return generate();
	}
	
	protected String nextDate(String date)
	{
		Date day = new Date();
		try
		{
			day = df.parse(date);
		} catch (ParseException e)
		{
			logger.error("Error occured when try to cast string to Date type.", e);
		}
		day = DateUtils.addDays(day, 1);
		return df.format(day);
	}
	
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		Map<String,String> namedParam = new HashMap<>();
		namedParam.put("startDate", "2017/07/01");
		namedParam.put("endDate", "2017/07/05");
		namedParam.put("teamid", "2017070732939");
		namedParam.put("contractno", "20170707323s4323239");
		namedParam.put("teamid", "2017070732939");
		
		
		SQLBuilder builder = new SQLBuilder("m3_before.sql", namedParam);		
		String[][] additionColumns = { { "countDate", "2017-07-30" } };
		
		String sql = builder.gtDate("t.approvetime", "startDate")
							.ltDate("t.approvetime", "endDate")
							.eq("t.contractno", "contractno")
							.in("t.team", "teamid")
							.between("t.putoutdate", "startDate", "endDate")
							.additionColumns(additionColumns)
							.toSQL();
		System.out.println(sql);

	}


}
