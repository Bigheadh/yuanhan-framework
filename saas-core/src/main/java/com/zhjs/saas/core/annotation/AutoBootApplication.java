package com.zhjs.saas.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
@EnableScheduling
@ServletComponentScan
@SpringBootApplication
@EnableConfigurationProperties
//@EnableJpaRepositories(repositoryFactoryBeanClass=CommonRepositoryFactoryBean.class)
public @interface AutoBootApplication
{
	
	@AliasFor(annotation=SpringBootApplication.class, attribute="scanBasePackages")
	String[] value() default {};
	
	//@AliasFor(annotation=EnableJpaRepositories.class, attribute="value")
	//String[] repoPackages() default {};

	/**
	 * Alias for the {@link ServletComponentScan#basePackages()} and {@link ServletComponentScan#value()} attribute. 
	 * Allows for more concise annotation declarations e.g.: {@code @ServletComponentScan("com.zhjs.pkg")} or 
	 * {@code @ServletComponentScan(basePackages="com.zhjs.pkg")}.
	 * @return the base packages to scan
	 */
	@AliasFor(annotation=ServletComponentScan.class, attribute="value")
	String[] servletPackages() default {};

	/**
	 * Type-safe alternative to {@link ServletComponentScan#basePackageClasses()} for specifying the packages to
	 * scan for annotated servlet components. The package of each class specified will be
	 * scanned.
	 * @return classes from the base packages to scan
	 */	
	@AliasFor(annotation=ServletComponentScan.class, attribute="basePackageClasses")
	Class<?>[] servletClasses() default {};
	
	
	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * @return the classes to exclude
	 */
	@AliasFor(annotation=SpringBootApplication.class, attribute="exclude")
	Class<?>[] exclude() default {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * @return the class names to exclude
	 * @since 1.3.0
	 */
	@AliasFor(annotation=SpringBootApplication.class, attribute="excludeName")
	String[] excludeName() default {};

	/**
	 * Base packages to scan for annotated components. Use {@link #scanBasePackageClasses}
	 * for a type-safe alternative to String-based package names.
	 * @return base packages to scan
	 * @since 1.3.0
	 */
	@AliasFor(annotation=SpringBootApplication.class, attribute="scanBasePackages")
	String[] scanBasePackages() default {};

	/**
	 * Type-safe alternative to {@link #scanBasePackages} for specifying the packages to
	 * scan for annotated components. The package of each class specified will be scanned.
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package that
	 * serves no purpose other than being referenced by this attribute.
	 * @return base packages to scan
	 * @since 1.3.0
	 */
	@AliasFor(annotation=SpringBootApplication.class, attribute="scanBasePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};



	/**
	 * Convenient way to quickly register {@link ConfigurationProperties} annotated beans
	 * with Spring. Standard Spring Beans will also be scanned regardless of this value.
	 * @return {@link ConfigurationProperties} annotated beans to register
	 */
	@AliasFor(annotation=EnableConfigurationProperties.class, attribute="value")
	Class<?>[] propertiesClasses() default {};

}
