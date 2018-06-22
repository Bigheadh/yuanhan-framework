package com.yuanhan.yuanhan.core.logger.support;

import java.io.OutputStream;
import java.io.PrintStream;

import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-11-14
 * @modified: 	2017-11-14
 * @version:
 */
public class SystemLoggerPrintStream extends PrintStream
{
	private static Logger logger = LoggerFactory.getLogger(SystemLoggerPrintStream.class);

	public SystemLoggerPrintStream(OutputStream out)
	{
		super(out);
	}

	public void print(boolean b)
	{
		println(b);
	}

	public void print(char c)
	{
		println(c);
	}

	public void print(char[] s)
	{
		println(s);
	}

	public void print(double d)
	{
		println(d);
	}

	public void print(float f)
	{
		println(f);
	}

	public void print(int i)
	{
		println(i);
	}

	public void print(long l)
	{
		println(l);
	}

	public void print(Object obj)
	{
		println(obj);
	}

	public void print(String s)
	{
		println(s);
	}
	
	private void logger(Object arg)
	{
		logger.info("{}", arg);
	}

	public void println(boolean x)
	{
		logger(x);
	}

	public void println(char x)
	{
		logger(Character.valueOf(x));
	}

	public void println(char[] x)
	{
		logger(x == null ? null : new String(x));
	}

	public void println(double x)
	{
		logger(Double.valueOf(x));
	}

	public void println(float x)
	{
		logger(Float.valueOf(x));
	}

	public void println(int x)
	{
		logger(Integer.valueOf(x));
	}

	public void println(long x)
	{
		logger(x);
	}

	public void println(Object x)
	{
		logger(x);
	}

	public void println(String x)
	{
		logger(x);
	}
}
