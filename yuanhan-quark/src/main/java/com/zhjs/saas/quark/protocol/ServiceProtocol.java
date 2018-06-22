package com.yuanhan.yuanhan.quark.protocol;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-05
 * @modified:	2017-11-05
 * @version:	
 */
public enum ServiceProtocol
{
	/**
	 * feign client which support JSON
	 */
	Rest,
	
	/**
	 * feign client which support JSON
	 */
	Feign,
	
	/**
	 * RMI
	 */
	RMI,
	
	/**
	 * Hessian2 protocol
	 */
	Hessian,
	
	/**
	 * Dubbo rpc
	 */
	Dubbo,
	
	/**
	 * spring http-invoker
	 */
	HttpInvoker, 
	
	/**
	 * high performance RPC implemented by yuanhan-Tech, better than Dubbo protocol.
	 * But it only supports the yuanhan-framework for now.
	 */
	Quark
}
