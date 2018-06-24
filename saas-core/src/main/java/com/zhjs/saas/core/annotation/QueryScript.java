package com.zhjs.saas.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.Query;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-02-27
 * @modified:	2018-02-27
 * @version:	
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Documented
@QueryAnnotation
@Query
public @interface QueryScript
{
	/**
	 * Defines the script file path and name which will be invoked to execute query.
	 * @return the query sql statement
	 */
	String script() default "";

	/**
	 * Defines the JPA query to be executed when the annotated method is called.
	 */
	@AliasFor(annotation=Query.class)
	String value() default "";

	/**
	 * Defines a special count query that shall be used for pagination queries to lookup the total number of elements for
	 * a page. If non is configured we will derive the count query from the method name.
	 */
	@AliasFor(annotation=Query.class)
	String countQuery() default "";

	/**
	 * Defines the projection part of the count query that is generated for pagination. If neither {@link #countQuery()}
	 * not {@link #countProjection()} is configured we will derive the count query from the method name.
	 * 
	 * @return
	 * @since 1.6
	 */
	@AliasFor(annotation=Query.class)
	String countProjection() default "";

	/**
	 * Configures whether the given query is a native one. Defaults to {@literal false}.
	 */
	@AliasFor(annotation=Query.class)
	boolean nativeQuery() default false;

	/**
	 * The named query to be used. If not defined, a {@link javax.persistence.NamedQuery} with name of
	 * {@code $ domainClass}.${queryMethodName}} will be used.
	 */
	@AliasFor(annotation=Query.class)
	String name() default "";

	/**
	 * Returns the name of the {@link javax.persistence.NamedQuery} to be used to execute count queries when pagination is
	 * used. Will default to the named query name configured suffixed by {@code .count}.
	 * 
	 * @see #name()
	 * @return
	 */
	@AliasFor(annotation=Query.class)
	String countName() default "";

}
