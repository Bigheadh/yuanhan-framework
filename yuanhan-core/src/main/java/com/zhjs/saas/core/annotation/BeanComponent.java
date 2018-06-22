package com.yuanhan.yuanhan.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-02-08
 * @modified:	2018-02-08
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@Component
@CommonAutowired
public @interface BeanComponent
{

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * @return the suggested component name, if any (or empty String otherwise)
	 */
	@AliasFor(annotation=Component.class, attribute="value")
	String value() default "";

}
