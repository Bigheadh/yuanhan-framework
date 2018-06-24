package com.zhjs.saas.core.logger.support;

import org.slf4j.spi.LocationAwareLogger;

import com.zhjs.saas.core.logger.Logger;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-08
 * @modified:	2017-11-08
 * @version:	
 */
public class DefaultLogger implements Logger
{
	
    private final static String FQCN = DefaultLogger.class.getName();

	private final org.slf4j.Logger logger;

    private final LocationAwareLogger locationAwareLogger;
	
	public DefaultLogger(org.slf4j.Logger logger)
	{
        if (logger instanceof LocationAwareLogger) {
            this.locationAwareLogger = (LocationAwareLogger) logger;
        } else {
            this.locationAwareLogger = null;
        }
		this.logger = logger;
	}

	@Override
	public String getName()
	{
		return this.logger.getName();
	}

	@Override
	public boolean isTraceEnabled()
	{
		return this.logger.isTraceEnabled();
	}

	@Override
	public void trace(String msg)
	{
        if (this.locationAwareLogger != null) {
        	this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, null, null);
            return;
        }
		this.logger.trace(msg);
	}

	@Override
	public void trace(String format, Object... arguments)
	{
        if (this.locationAwareLogger != null) {
        	this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.TRACE_INT, format, arguments, null);
            return;
        }
		this.logger.trace(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, null, t);
            return;
        }
		this.logger.trace(msg, t);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return this.logger.isDebugEnabled();
	}

	@Override
	public void debug(String msg)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
            return;
        }
		this.logger.debug(msg);
	}

	@Override
	public void debug(String format, Object... arguments)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, arguments, null);
            return;
        }
		this.logger.debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, t);
            return;
        }
		this.logger.debug(msg, t);
	}

	@Override
	public boolean isInfoEnabled()
	{
		return this.logger.isInfoEnabled();
	}

	@Override
	public void info(String msg)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, null);
            return;
        }
		this.logger.info(msg);
	}

	@Override
	public void info(String format, Object... arguments)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, format, arguments, null);
            return;
        }
		this.logger.info(format, arguments);
	}

	@Override
	public void info(String msg, Throwable t)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, t);
            return;
        }
		this.logger.info(msg, t);
	}

	@Override
	public boolean isWarnEnabled()
	{
		return this.logger.isWarnEnabled();
	}

	@Override
	public void warn(String msg)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, null);
            return;
        }
		this.logger.warn(msg);
	}

	@Override
	public void warn(String format, Object... arguments)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.WARN_INT, format, arguments, null);
            return;
        }
		this.logger.warn(format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, t);
            return;
        }
		this.logger.warn(msg, t);
	}

	@Override
	public boolean isErrorEnabled()
	{
		return this.logger.isErrorEnabled();
	}

	@Override
	public void error(String msg)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
            return;
        }
		this.logger.error(msg);
	}

	@Override
	public void error(String format, Object... arguments)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.ERROR_INT, format, arguments, null);
            return;
        }
		this.logger.error(format, arguments);
	}

	@Override
	public void error(String msg, Throwable t)
	{
        if (this.locationAwareLogger != null) {
            this.locationAwareLogger.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, t);
            return;
        }
		this.logger.error(msg, t);
	}

}
