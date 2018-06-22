package com.yuanhan.yuanhan.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-06-01
 * @modified:	2017-06-01
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Service
@CommonAutowired
public @interface ServiceComponent
{
	/**
	 * will be used as a service bean name. and also will be used as a soa service name. 
	 * So we suggest you to definite a good service name in good pattern. 
	 * Like user.baseinfo.update, user.baseinfo.query, and etc. 
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an auto-detected component.
	 * @return the suggested component name, if any
	 */
	@AliasFor(annotation=Service.class, attribute="value")
	String value() default "";

	@AliasFor(annotation=Service.class, attribute="value")
	String method() default "";
    
    /**
     * define the remote protocols that will be supported by current service. 
     * soa remote protocol, such as phprpc, hessian, rmi, spring-remote, dubbo, and so on
     *
     * @return the protocols if any
     */
    String[] protocol() default {};
    
    /**
     * soa group that the current service beyond, default group name is "DEFAULT"
     *
     * @return the suggested group name, if any
     */
    String group() default "DEFAULT";

    /**
     * Chinese group name can be set here. when generate API doc, it will be used.
     *
     * @return the suggested group name, if any
     */
    String groupTitle() default "DEFAULT";

    /**
     * tags, you can set some tags to a service.
     *
     * @return the suggested tags' name, if any
     */
    String[] tags() default {};

    /**
     * 
     *
     * @return a million second time if any
     */
    int timeout() default -1;

    /**
     * service version, multi versions from one particular same service. 
     * when a service has a upgrade version, and you want to keep the old version running.
     * then you can use version to separate different versions.
     *
     * @return the version number if there is any
     */
    String version() default "";
	
	
	
	@AliasFor(annotation=CommonAutowired.class, attribute="method")
	AutowiredMethod autowired() default AutowiredMethod.BY_TYPE;

}
