package com.zhjs.saas.core.util;

import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromContextPath;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2017-05-18
 * @modified: 	2017-05-18
 * @version:
 */
public abstract class WebUtil extends WebUtils
{

	protected static Logger logger = LoggerFactory.getLogger(WebUtil.class);

	/**
	 * Private constructor to prevent instantiation.
	 */
	private WebUtil()
	{
	}

	public static HttpSession getSession()
	{
		return getRequest().getSession();
	}

	public static HttpServletRequest getRequest()
	{
		RequestAttributes attr = RequestContextHolder.getRequestAttributes();
		if (attr != null)
			return ((ServletRequestAttributes) attr).getRequest();

		return null;
	}

	public static Locale getLocale()
	{
		return LocaleContextHolder.getLocale();
	}

	/**
	 * get client IP without internal which used in LAN.
	 * 
	 * @return IP address or null
	 */
	public static String getRemoteIp()
	{
		return getRemoteIp(getRequest());
	}

	public static String getRemoteIp(HttpServletRequest request)
	{

		// haproxy
		String ip = request.getHeader("True-Client-IP");
		if (StringUtil.isNotEmpty(ip))
		{
			return ip;
		}

		// CDN
		ip = request.getHeader("Cdn_Src_Ip");
		if (StringUtil.isNotEmpty(ip))
		{
			return ip;
		}

		//nginx
		ip = request.getHeader("X-Real-IP");
		if (StringUtil.isNotEmpty(ip))
		{
			return ip;
		}

		//tomcat
		ip = request.getHeader("X-Forwarded-For");
		if (StringUtil.isNotEmpty(ip))
		{
			return ip;
		}

		ip = request.getHeader("Proxy-Client-IP");
		if (StringUtil.isNotEmpty(ip))
		{
			return ip;
		}

		ip = request.getHeader("WL-Proxy-Client-IP");
		return ip;

	}

	public static String getRemoteIp2(HttpServletRequest request)
	{

		// haproxy
		String ip = request.getHeader("True-Client-IP");
		if (StringUtil.isNotEmpty(ip))
		{
			String[] IPArray = ip.split(",\\s*");
			if (IPArray.length > 0)
			{
				ip = IPArray[IPArray.length - 1];
			}
			return ip;
		}

		// CDN
		ip = request.getHeader("Cdn_Src_Ip");
		if (StringUtil.isNotEmpty(ip))
		{
			String[] IPArray = ip.split(",\\s*");
			if (IPArray.length > 0)
			{
				ip = IPArray[IPArray.length - 1];
			}
			return ip;
		}

		//nginx
		ip = request.getHeader("X-Real-IP");
		if (StringUtil.isNotEmpty(ip))
		{
			String[] proxys = ip.split(",\\s*");//nginx don't have space
			for (String proxy : proxys)
			{
				if (!(proxy.startsWith("127.") || proxy.startsWith("172.") || proxy.startsWith("192.")
						|| proxy.startsWith("10.")))
				{
					ip = proxy;
					break;
				}
			}
			return ip;
		}

		//tomcat
		ip = request.getHeader("X-Forwarded-For");
		if (StringUtil.isNotEmpty(ip))
		{
			String[] proxys = ip.split(",\\s*");
			for (String proxy : proxys)
			{
				if (!(proxy.startsWith("127.") || proxy.startsWith("172.") || proxy.startsWith("192.")
						|| proxy.startsWith("10.")))
				{
					ip = proxy;
					break;
				}
			}
			return ip;
		}

		ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtil.isNotEmpty(ip))
		{
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static UriComponents componentsFrom(HttpServletRequest request, String basePath)
	{

		ServletUriComponentsBuilder builder = fromServletMapping(request, basePath);

		UriComponents components = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request)).build();

		String host = components.getHost();
		if (!hasText(host))
		{
			return builder.build();
		}

		builder.host(host);
		builder.port(components.getPort());

		return builder.build();
	}

	private static ServletUriComponentsBuilder fromServletMapping(HttpServletRequest request, String basePath)
	{

		ServletUriComponentsBuilder builder = fromContextPath(request);

		builder.replacePath(prependForwardedPrefix(request, basePath));
		if (hasText(new UrlPathHelper().getPathWithinServletMapping(request)))
		{
			builder.path(request.getServletPath());
		}

		return builder;
	}

	private static String prependForwardedPrefix(HttpServletRequest request, String path)
	{

		String prefix = request.getHeader("X-Forwarded-Prefix");
		if (prefix != null)
		{
			return prefix + path;
		}
		else
		{
			return path;
		}
	}
}
