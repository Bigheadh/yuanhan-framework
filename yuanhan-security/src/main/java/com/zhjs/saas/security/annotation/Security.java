package com.yuanhan.yuanhan.security.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * <p>
 * 当value()和entity()同时使用default值时，则权限控制方法的第一个参数必须是long或EntityOwner类型
 * </p>
 * <p>
 * 当value()为default或空，而entity()不是default时，则权限控制方法的第一个参数必须是long类型
 * </p>
 * <p>
 * 当value()非空，则会忽略entity()的值，此时权限控制方法的参数灵活，不受规则约束
 * </p>
 * 
 * @author:		yuanhan
 * @since:		2018-03-10
 * @modified:	2018-03-10
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER })
public @interface Security
{
	/**
	 * expression, only support spring EL
	 * @return
	 */
	String value() default "";
	
	/**
	 * entity type that need to be validate
	 * 
	 * @return
	 */
	Class<? extends BaseObject> entity() default BaseObject.class;
	
	/**
	 * error code or message
	 * @return
	 */
	String error() default "";
	
}
