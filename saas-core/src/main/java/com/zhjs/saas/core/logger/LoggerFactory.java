package com.zhjs.saas.core.logger;

import java.util.Properties;

import com.zhjs.saas.core.config.PropertyConfig;
import com.zhjs.saas.core.logger.support.DefaultLoggerFactory;
import com.zhjs.saas.core.logger.support.SystemLoggerPrintStream;
import com.zhjs.saas.core.util.PropertiesLoaderUtil;
import com.zhjs.saas.core.util.StringUtil;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-08
 * @modified:	2017-11-08
 * @version:	
 */
public final class LoggerFactory
{
	
	static {
		System.setOut(new SystemLoggerPrintStream(System.out));
	}
	
    // private constructor prevents instantiation
    private LoggerFactory() { }
    
    private static ILoggerFactory factory;
    private static boolean customized = false;
    private static Properties config;
    
    public static ILoggerFactory getLoggerFactory()
    {
    	if(factory==null || !customized)
    	{
    		try
    		{
    			if(config==null)
    				config = PropertiesLoaderUtil.loadAllProperties("classpath:/**/saas*.properties");
    			String factoryClass = config.getProperty(PropertyConfig.Logger_Factory);
	    		if(StringUtil.isNotBlank(factoryClass))
	    		{
	    			org.slf4j.LoggerFactory.getLogger(LoggerFactory.class).info("Try to initializing customized factory [{}]", factoryClass);
	    			factory = (ILoggerFactory)Class.forName(factoryClass).getDeclaredConstructor().newInstance();
	    			customized = true;
	    		}
	    		else if(factory==null)
	    		{
	    			org.slf4j.LoggerFactory.getLogger(LoggerFactory.class).info("Cannot find any customized LoggerFactory, use DefaultLoggerFactory instead.");
	    			factory = new DefaultLoggerFactory();
	    		}
    		} catch(Exception e) {
    			org.slf4j.LoggerFactory.getLogger(LoggerFactory.class).warn(e.toString());
    			factory = new DefaultLoggerFactory();
    		}
    	}
    	return factory;
    }

    /**
     * Return a logger named according to the name parameter using the
     * statically bound {@link ILoggerFactory} instance.
     * 
     * @param name
     *            The name of the logger.
     * @return logger
     */
    public static Logger getLogger(String name) {
        return getLoggerFactory().getLogger(name);
    }

    /**
     * Return a logger named corresponding to the class passed as parameter,
     * using the statically bound {@link ILoggerFactory} instance.
     * 
     * <p>
     * In case the the <code>clazz</code> parameter differs from the name of the
     * caller as computed internally by SLF4J, a logger name mismatch warning
     * will be printed but only if the
     * <code>slf4j.detectLoggerNameMismatch</code> system property is set to
     * true. By default, this property is not set and no warnings will be
     * printed even in case of a logger name mismatch.
     * 
     * @param clazz
     *            the returned logger will be named after clazz
     * @return logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }
    
}
