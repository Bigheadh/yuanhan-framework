package com.yuanhan.yuanhan.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

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
@RepositoryRestResource
@Repository
@CommonAutowired
public @interface DaoComponent
{

	/**
	 * will be used as a dao bean name.
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * @return the suggested component name, if any
	 */
	@AliasFor(annotation=Repository.class, attribute="value")
	String value() default "";
	
	@AliasFor(annotation=CommonAutowired.class, attribute="method")
	AutowiredMethod autowired() default AutowiredMethod.BY_TYPE;

	/**
	 * Flag indicating whether this resource is exported at all.
	 * 
	 * @return {@literal true} if the resource is to be exported, {@literal false} otherwise.
	 */
	@AliasFor(annotation=RepositoryRestResource.class, attribute="exported")
	boolean rest() default false;

	/**
	 * The path segment under which this resource is to be exported.
	 * 
	 * @return A valid path segment.
	 */
	@AliasFor(annotation=RepositoryRestResource.class)
	String path() default "";

	/**
	 * The rel value to use when generating links to the collection resource.
	 * 
	 * @return A valid rel value.
	 */
	@AliasFor(annotation=RepositoryRestResource.class)
	String collectionResourceRel() default "";

	/**
	 * The description of the collection resource.
	 * 
	 * @return
	 */
	@AliasFor(annotation=RepositoryRestResource.class)
	Description collectionResourceDescription() default @Description(value = "");

	/**
	 * The rel value to use when generating links to the item resource.
	 * 
	 * @return A valid rel value.
	 */
	@AliasFor(annotation=RepositoryRestResource.class)
	String itemResourceRel() default "";

	/**
	 * The description of the item resource.
	 * 
	 * @return
	 */
	@AliasFor(annotation=RepositoryRestResource.class)
	Description itemResourceDescription() default @Description(value = "");


}
