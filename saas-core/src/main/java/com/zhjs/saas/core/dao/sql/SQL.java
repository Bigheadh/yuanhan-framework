package com.zhjs.saas.core.dao.sql;

import static com.zhjs.saas.core.dao.DaoConstants.SQL_Select;
import static com.zhjs.saas.core.dao.DaoConstants.SQL_WhereCondition;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.pojo.ObjectCache;
import com.zhjs.saas.core.util.CodecUtil;
import com.zhjs.saas.core.util.StringUtil;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2017-07-05
 * @modified: 	2017-07-05
 * @version:
 */
public abstract class SQL
{
	private static Logger logger = LoggerFactory.getLogger(SQL.class);
	
	private static ObjectCache sqlCache = new ObjectCache(200);
	
	private static String location = null;
	private static String sqlEncode = null;
	private static String sqlComment = null;
	
	public static void init(String path, String encoding, String comment){
		location = StringUtil.isNotBlank(path) ? path : "classpath:sql";
		sqlEncode = StringUtil.isNotBlank(encoding) ? encoding : CodecUtil.Charset;
		sqlComment = StringUtil.isNotBlank(comment) ? comment : "--";
	}

	/**
	 * initial the SQL statement into cache, <br>
	 * so that it can prevent to read the SQL file every time.
	 */
	public static void initCache()
	{
		clearCache();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(location+"/**/*.sql");
            for(Resource resource : resources)
            {
				try
				{
					URL url = resource.getURL();
					logger.info("Resolving sql file " + url.toString());
			        String urlPath = url.getPath().replace("%20", "");
			        String sqlPackage = location.replace("classpath:","");
			        if(!sqlPackage.startsWith(File.separator)) sqlPackage=File.separator+sqlPackage;
			        if(!sqlPackage.endsWith(File.separator)) sqlPackage+=File.separator;
			        String filename = urlPath.substring(urlPath.lastIndexOf(sqlPackage)+sqlPackage.length());
					loadSQL(filename, resource);
				} catch (Exception e)
				{
					logger.error("Error occured while loading sql statement from file.", e);
				}
            }
        } catch (IOException e) {
            logger.error("Error occured while loading all sql file. " + e);
        }
	}

	public static void clearCache()
	{
		sqlCache.clear();
	}

	/**
	 * load SQL statement from file, remove all comments, and replace with
	 * '<b><i>--:whereCondition</i></b>', and inject additional columns to SQL,
	 * such as
	 * <p>
	 * <code>
	 *  select t.* from table t where 1=1 <b><i>--:whereCondition</i></b>
	 * </code>
	 * </p>
	 * will be rendered to
	 * <p>
	 * <code>
	 * 	select addCol1 col1, addCol2 col2, t.* from table t where 1=1 
	 *  and t.contractno='2017070232323112323' and t.approvetime='2017/07/05'
	 * </code>
	 * </p>
	 * 
	 * @param file
	 * @param whereCondition
	 * @param additionColumns
	 * @return
	 * @throws Exception
	 */
	public static String loadSQL(String file, String whereCondition, String[][] additionColumns) throws Exception
	{
		String sql = loadSQL(file);
		sql = Pattern.compile(sqlComment+SQL_WhereCondition, Pattern.CASE_INSENSITIVE).matcher(sql)
				.replaceFirst(whereCondition);
		if (additionColumns != null)
		{
			String displayColumns = "";
			for (String[] column : additionColumns)
				displayColumns += "'" + column[1] + "' " + column[0] + ", ";
			sql = Pattern.compile(SQL_Select, Pattern.CASE_INSENSITIVE).matcher(sql)
					.replaceFirst(SQL_Select + " " + displayColumns);
		}
		return sql;
	}

	/**
	 * load SQL statement, and remove all comments
	 * 
	 * @param sqlFile
	 *            SQL file name, also support sub path
	 * @return
	 * @throws Exception
	 */
	public static String loadSQL(String sqlFile) throws Exception
	{
		String cache = (String) (sqlCache.getCacheObject(sqlFile));
		if (StringUtil.isNotBlank(cache))
			return cache;
		return loadSQL(sqlFile, new ClassPathResource(location.replace("classpath:", "")+File.separator+sqlFile));
	}

	/**
	 * load SQL statement in the special package, and remove all comments
	 * 
	 * @param fileName
	 *            SQL file name
	 * @param packagePath
	 * @return
	 * @throws Exception
	 */
	public static String loadSQL(String fileName, Resource resource) throws Exception
	{

        if(fileName.startsWith(File.separator)) fileName=fileName.substring(1);
		String cache = (String) (sqlCache.getCacheObject(fileName));
		if (StringUtil.isNotBlank(cache))
			return cache;

		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		if (!resource.exists())
		{
			throw new Exception("SQL file doesn't exist: " + fileName);
		}
		try
		{
			InputStreamReader inReader = new InputStreamReader(resource.getInputStream(), sqlEncode);
			reader = new BufferedReader(inReader);
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				int index = -1;
				if ((index = line.indexOf(sqlComment)) >= 0
						&& !line.contains(sqlComment+SQL_WhereCondition))
					line = line.substring(0, index);
				builder.append(" ").append(line);
			}
			reader.close();
		} catch (Exception e)
		{
			if (reader != null)
				reader.close();
			throw e;
		}
		String sql = builder.toString();
		sqlCache.setCache(fileName, sql);
		return sql;
	}

}
