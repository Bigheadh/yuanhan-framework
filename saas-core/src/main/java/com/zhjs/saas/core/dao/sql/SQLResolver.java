package com.zhjs.saas.core.dao.sql;

import static com.zhjs.saas.core.config.PropertyConfig.*;

import org.springframework.core.env.Environment;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-07-25
 * @modified:	2017-07-25
 * @version:	
 */
public class SQLResolver
{
	
	public void init(Environment env)
	{
		SQL.init(env.getProperty(SQL_Path_Prefix),
					env.getProperty(SQL_File_Encoding),
					env.getProperty(SQL_Comment));
		SQL.initCache();
	}
	
}
