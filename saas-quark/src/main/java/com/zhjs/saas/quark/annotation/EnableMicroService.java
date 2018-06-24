package com.zhjs.saas.quark.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import com.zhjs.saas.quark.annotation.support.EnableMicroServiceRegistrar;

/**
 * 
 * @author:		Jackie Wang
 * @since:		2017-10-25
 * @modified:	2017-10-25
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@EnableFeignClients
@EnableDiscoveryClient
//@EnableCircuitBreaker
//@EnableHystrix
//@SpringCloudApplication
@Import(EnableMicroServiceRegistrar.class)
public @interface EnableMicroService
{

	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
	 * declarations e.g.: {@code @ComponentScan("org.my.pkg")} instead of
	 * {@code @ComponentScan(basePackages="org.my.pkg")}.
	 * @return the array of 'basePackages'.
	 */
	@AliasFor(annotation=EnableFeignClients.class, attribute="basePackages")
	String[] value() default {};
	
	/**
	 * Base packages to scan for annotated components.
	 * <p>
	 * {@link #value()} is an alias for (and mutually exclusive with) this attribute.
	 * <p>
	 * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
	 * package names.
	 *
	 * @return the array of 'basePackages'.
	 */
	@AliasFor(annotation=EnableFeignClients.class)
	String[] basePackages() default {};

	/**
	 * A custom <code>@Configuration</code> for all feign clients. Can contain override
	 * <code>@Bean</code> definition for the pieces that make up the client, for instance
	 * {@link feign.codec.Decoder}, {@link feign.codec.Encoder}, {@link feign.Contract}.
	 *
	 * @see FeignClientsConfiguration for the defaults
	 */
	@AliasFor(annotation=EnableFeignClients.class)
	Class<?>[] defaultConfiguration() default {};

	/**
	 * If true, the ServiceRegistry will automatically register the local server.
	 */
	@AliasFor(annotation=EnableDiscoveryClient.class)
	boolean autoRegister() default true;

}
