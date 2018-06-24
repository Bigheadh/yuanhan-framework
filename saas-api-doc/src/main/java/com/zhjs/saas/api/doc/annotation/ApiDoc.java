package com.zhjs.saas.api.doc.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-03-05
 * @modified:	2018-03-05
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, ANNOTATION_TYPE, FIELD })
public @interface ApiDoc
{
	/**
	 * 
	 */
	String value() default "";
	
	/**
	 * 
	 */
	String path() default "";

    /**
     * Corresponds to the `method` field as the HTTP method used.
     * <p>
     * If not stated, in JAX-RS applications, the following JAX-RS annotations would be scanned
     * and used: {@code @GET}, {@code @HEAD}, {@code @POST}, {@code @PUT}, {@code @DELETE} and {@code @OPTIONS}.
     * Note that even though not part of the JAX-RS specification, if you create and use the {@code @PATCH} annotation,
     * it will also be parsed and used. If the httpMethod property is set, it will override the JAX-RS annotation.
     * <p>
     * For Servlets, you must specify the HTTP method manually.
     * <p>
     * Acceptable values are "GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS" and "PATCH".
     */
    String httpMethod() default "GET";
	
	/**
	 * description of the API
	 */
	String description() default "";
	
	/**
	 * 
	 * @return
	 */
	ApiParam[] params() default {};
	
}
