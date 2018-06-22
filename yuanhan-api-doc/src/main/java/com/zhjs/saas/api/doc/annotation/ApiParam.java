package com.yuanhan.yuanhan.api.doc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-05-16
 * @modified:	2018-05-16
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ApiParam
{
	/**
	 * api request parameter name
	 */
	String name() default "";
	
	/**
	 * coordinate if it is required
	 */
	boolean required() default false;
	
	/**
	 * field default value, only effects as request parameter
	 */
	String defaultValue() default "";
	
	/**
	 * field data type, only effects as request parameter
	 */
	String dataType() default "String";
	
	/**
	 * description of the API parameter
	 */
	String description() default "";

}
