package com.yuanhan.yuanhan.core.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-12-25
 * @modified:	2017-12-25
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface JsonQuery
{
	String value() default "";
}
