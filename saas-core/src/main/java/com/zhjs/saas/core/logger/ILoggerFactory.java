package com.zhjs.saas.core.logger;

/**
 * <code>ILoggerFactory</code> instances manufacture {@link Logger}
 * instances by name.
 * 
 * <p>Most users retrieve {@link Logger} instances through the static
 * {@link LoggerFactory#getLogger(String)} method. An instance of of this
 * interface is bound internally with {@link LoggerFactory} class at 
 * compile time. 
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-08
 * @modified:	2017-11-08
 * @version:	
 */
public interface ILoggerFactory
{

    /**
     * Return an appropriate {@link Logger} instance as specified by the
     * <code>name</code> parameter.
     * 
     * <p>If the name parameter is equal to {@link Logger#ROOT_LOGGER_NAME}, that is 
     * the string value "ROOT" (case insensitive), then the root logger of the 
     * underlying logging system is returned.
     * 
     * <p>Null-valued name arguments are considered invalid.
     *
     * <p>Certain extremely simple logging systems, e.g. NOP, may always
     * return the same logger instance regardless of the requested name.
     * 
     * @param name the name of the Logger to return
     * @return a Logger instance 
     */
    public Logger getLogger(String name);
    

    /**
     * Return an appropriate {@link Logger} instance as specified by the given class
     * <code>clazz</code> parameter.
     * 
     * @param clazz the class name will be translate to the name of the Logger to return
     * @return a Logger instance 
     */
    public Logger getLogger(Class<?> clazz);

}
