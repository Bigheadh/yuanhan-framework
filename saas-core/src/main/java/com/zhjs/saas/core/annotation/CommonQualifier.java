package com.zhjs.saas.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

/**
 * This is a annotation same as spring @Qualifier, it is used with @CommonAutowired.
 *  * 
 * @author:		Jackie Wang 
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */

@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface CommonQualifier {
	
	@AliasFor(annotation=Qualifier.class, attribute="value")
	String value() default "";
	
}
