package com.zhjs.saas.scheduler.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-06-11
 * @modified:	2018-06-11
 * @version:	
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
//@Import({JobParserAutoConfiguration.class})
public @interface EnableElasticJob
{

}
