package com.zhjs.saas.core.config;

import java.util.Properties;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public abstract class DataBaseProperties
{//extends HibernateAndJdbcDaoSupport{

	protected Properties prop = new Properties();

	/**
	 * @return the properties
	 */
	public Properties getProperties()
	{
		return prop;
	}

	public String getValue(String key)
	{
		return this.getProperties().getProperty(key);
	}

	public String getValue(String key, String defaultValue)
	{
		return this.getProperties().getProperty(key, defaultValue);
	}

	public abstract void refresh();

}
