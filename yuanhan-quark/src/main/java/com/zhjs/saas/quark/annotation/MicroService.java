package com.yuanhan.yuanhan.quark.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.alibaba.dubbo.config.annotation.Service;
import com.yuanhan.yuanhan.quark.protocol.ServiceProtocol;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-10-25
 * @modified:	2017-10-25
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
//@Import(MicroServiceRegistrar.class)
public @interface MicroService
{
	
	public static final String Service = "service";
	public static final String ServiceInterface = "serviceInterface";
	public static final String Annotation = "services";
	public static final String Port = "registryPort";
	public static final String PortConfig = "yuanhan.cloud.remote.port";
    
    /**
     * define the remote protocols that will be supported by current service. 
     * micro-service protocol, such as hessian, rmi, spring-remote, dubbo, and so on
     *
     * @return the protocol if any
     */
	@AliasFor("protocol")
	ServiceProtocol[] value() default {ServiceProtocol.Dubbo};

    
    /**
     * define the remote protocols that will be supported by current service. 
     * micro-service protocol, such as hessian, rmi, spring-remote, dubbo, and so on
     *
     * @return the protocol if any
     */
	@AliasFor("value")
	ServiceProtocol[] protocol() default {ServiceProtocol.Dubbo};
	
	
	/**
	 * 
	 * @return
	 */
	int timeout() default 10000;
	

	/**
	 * expose a remote port, only support RMI for now
	 * @return
	 */
	int port() default 0; // Registry.REGISTRY_PORT;
	
	/**
	 * which interface class to be exposed as remote micro service
	 * @return the specified interface class
	 */
	Class<?> interfaceClass() default void.class;
	
	
	/**
	 * define a url path for exporting service, like namespace path.
	 * @return the url sub-path if any
	 */
	@AliasFor("path")
	String url() default "/remote";
	

	/**
	 * define a url path for exporting service, like namespace path.
	 * @return the url sub-path if any
	 */
	@AliasFor("url")
	String path() default "/remote";
	

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
	 * expose service by dubbo way, giving a dubbo {@link <code>@Service</code>} 
	 * @return the dubbo service from <code>@Service</code>
	 */
	Service[] dubbo() default {};

}
