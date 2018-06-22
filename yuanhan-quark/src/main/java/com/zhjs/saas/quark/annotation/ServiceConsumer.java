package com.yuanhan.yuanhan.quark.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.core.annotation.AliasFor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yuanhan.yuanhan.quark.config.ServiceConsumerConfig;
import com.yuanhan.yuanhan.quark.protocol.ServiceProtocol;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-07
 * @modified:	2017-11-07
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@FeignClient
public @interface ServiceConsumer
{

	/**
	 * The name of the service with optional protocol prefix. Synonym for {@link #name()
	 * name}. A name must be specified for all clients, whether or not a url is provided.
	 * Can be specified as property key, eg: ${propertyKey}.
	 */
	@AliasFor(annotation=FeignClient.class)
	String value() default "";

	/**
	 * The service id with optional protocol prefix. Synonym for {@link #value() value}.
	 */
	@AliasFor(annotation=FeignClient.class)
	String name() default "";
	
	/**
	 * Sets the <code>@Qualifier</code> value for the feign client.
	 */
	@AliasFor(annotation=FeignClient.class)
	String qualifier() default "";

	/**
	 * An absolute URL or resolvable hostname (the protocol is optional).
	 */
	@AliasFor(annotation=FeignClient.class)
	String url() default "";

	/**
	 * Whether 404s should be decoded instead of throwing FeignExceptions
	 */
	@AliasFor(annotation=FeignClient.class)
	boolean decode404() default false;

	/**
	 * A custom <code>@Configuration</code> for the feign client. Can contain override
	 * <code>@Bean</code> definition for the pieces that make up the client, for instance
	 * {@link feign.codec.Decoder}, {@link feign.codec.Encoder}, {@link feign.Contract}.
	 *
	 * @see FeignClientsConfiguration for the defaults
	 */
	@AliasFor(annotation=FeignClient.class)
	Class<?>[] configuration() default ServiceConsumerConfig.class;

	/**
	 * Fallback class for the specified Feign client interface. The fallback class must
	 * implement the interface annotated by this annotation and be a valid spring bean.
	 */
	@AliasFor(annotation=FeignClient.class)
	Class<?> fallback() default void.class;

	/**
	 * Define a fallback factory for the specified Feign client interface. The fallback
	 * factory must produce instances of fallback classes that implement the interface
	 * annotated by {@link FeignClient}. The fallback factory must be a valid spring
	 * bean.
	 *
	 * @see feign.hystrix.FallbackFactory for details.
	 */
	@AliasFor(annotation=FeignClient.class)
	Class<?> fallbackFactory() default void.class;

	/**
	 * Path prefix to be used by all method-level mappings. Can be used with or without
	 * <code>@RibbonClient</code>.
	 */
	@AliasFor(annotation=FeignClient.class)
	String path() default "";
	

	/**
	 * Whether to mark the feign proxy as a primary bean. Defaults to true.
	 */
	@AliasFor(annotation=FeignClient.class)
	boolean primary() default true;
	
	
	ServiceProtocol protocol() default ServiceProtocol.Rest;


    /**
     * service version, multi versions from one particular same service. 
     * when a service has a upgrade version, and you want to keep the old version running.
     * then you can use version to separate different versions.
     *
     * @return the version number if there is any
     */
	@AliasFor("v")
	String version() default "1.0.0";
	

    /**
     * service version, multi versions from one particular same service. 
     * when a service has a upgrade version, and you want to keep the old version running.
     * then you can use version to separate different versions.
     *
     * @return the version number if there is any
     */
	@AliasFor("version")
	String v() default "1.0.0";
	
	
	/**
	 * refer to a service by dubbo, like a dubbo {@link <code>@Reference</code>} 
	 * @return the dubbo service from remote <code>@Service</code>
	 */
	Reference[] dubbo() default {};

}
