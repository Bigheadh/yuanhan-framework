package com.zhjs.saas.core.config;

import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public interface PropertiesHolder {
	
	public void refresh();
	
	public Properties getProperties();
	
	public String getValue(String key);
	
	public String getNamedValue(String key, Map<String,Object> arguments, boolean sqlFormat);
	
	public String getValue(String key, Object[] arguments);
	
	public String getValue(String key, String defaultValue);
	
	public String getValue(String key, Object[] arguments, String defaultValue);

}
