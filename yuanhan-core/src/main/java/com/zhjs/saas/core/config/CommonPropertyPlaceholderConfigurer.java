package com.yuanhan.yuanhan.core.config;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

import com.yuanhan.yuanhan.core.util.PropertiesLoaderUtil;
import com.yuanhan.yuanhan.core.util.StringUtil;

import org.springframework.util.StringValueResolver;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public class CommonPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements PropertiesHolder
{

	private Properties properties;
	private DataBaseProperties deltaProperties;
	
	public CommonPropertyPlaceholderConfigurer(){}
	
	public CommonPropertyPlaceholderConfigurer(String... locations)
	{
		Set<Resource> resources = new HashSet<>();
		Arrays.asList(locations).forEach(location -> {
			resources.add(new ClassPathResource(location));
		});
		super.setLocations(resources.toArray(new Resource[0]));
	}
	
	public CommonPropertyPlaceholderConfigurer(Resource... resources)
	{
		super.setLocations(resources);
	}

	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException
	{
		if (this.deltaProperties != null)
			props.putAll(deltaProperties.getProperties());
		super.processProperties(beanFactoryToProcess, props);

		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
		props.forEach((key,value) -> {
			props.put(key, valueResolver.resolveStringValue((String)value));
		});
		setProperties(props);
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties()
	{
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}

	public String getValue(String key)
	{
		return this.getProperties().getProperty(key);
	}

	public String getValue(String key, String defaultValue)
	{
		return this.getProperties().getProperty(key, defaultValue);
	}

	/**
	 * @param deltaProperties
	 *            the deltaProperties to set
	 */
	public void setDeltaProperties(DataBaseProperties deltaProperties)
	{
		this.deltaProperties = deltaProperties;
	}

	public void refresh()
	{
		logger.info("Begin to clear the properties...");
		synchronized (this.properties)
		{
			Properties temp = this.properties;
			this.properties = new Properties();
			logger.info("Cleared properties!!!");
			try
			{
				logger.info("Begin to refill new properties, please be waiting...");
				this.loadProperties(properties);
				if (this.deltaProperties != null)
				{
					deltaProperties.refresh();
					this.properties.putAll(deltaProperties.getProperties());
				}
				logger.info("Successfully to generate new properties!");
			} catch (IOException e)
			{
				logger.error("IO Exception: when refreshing the properties from configured files or database."
						+ " Now roll back to origianl properties after the application context startup!", e);
				this.properties = temp;
			}
		}
	}

	public String getValue(String key, Object[] arguments)
	{
		if (ObjectUtils.isEmpty(arguments))
			return this.getValue(key);
		return MessageFormat.format(this.getValue(key), arguments);
	}

	public String getValue(String key, Object[] arguments, String defaultValue)
	{
		if (ObjectUtils.isEmpty(arguments))
			return this.getValue(key, defaultValue);
		String result = this.getValue(key, arguments);
		if (StringUtil.isEmpty(result))
			return defaultValue;
		return result;
	}

	public String getNamedValue(String key, Map<String, Object> arguments, boolean sqlFormat)
	{
		String result = this.getValue(key);
		if (StringUtil.isNotEmpty(result))
		{
			result = PropertiesLoaderUtil.resolveNamedArguments(result, arguments, sqlFormat);
		}
		return result;
	}



	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final PropertyPlaceholderHelper helper;

		private final PlaceholderResolver resolver;

		public PlaceholderResolvingStringValueResolver(Properties props) {
			this.helper = new PropertyPlaceholderHelper(
					placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
			this.resolver = new PropertyPlaceholderConfigurerResolver(props);
		}

		@Override
		public String resolveStringValue(String strVal) throws BeansException {
			String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
			if (trimValues) {
				resolved = resolved.trim();
			}
			return (resolved.equals(nullValue) ? null : resolved);
		}
	}

	private class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

		private final Properties props;

		private PropertyPlaceholderConfigurerResolver(Properties props) {
			this.props = props;
		}

		@Override
		public String resolvePlaceholder(String placeholderName) {
			return CommonPropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, props,
								PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
		}
	}

}
