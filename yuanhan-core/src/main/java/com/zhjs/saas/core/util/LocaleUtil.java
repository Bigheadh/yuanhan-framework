package com.yuanhan.yuanhan.core.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */
public abstract class LocaleUtil {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private LocaleUtil(){}
	
	public final static String DefaultLocale = "en";


	/**
	 * Parse the given <code>localeString</code> into a {@link Locale}.
	 * <p>This is the inverse operation of {@link Locale#toString Locale's toString}.
	 * @param localeString the locale string, following <code>Locale's</code>
	 * <code>toString()</code> format ("en", "en_UK", etc);
	 * also accepts spaces as separators, as an alternative to underscores
	 * @return a corresponding <code>Locale</code> instance
	 */
	public static Locale toLocale(String localeString) {
		String[] parts = StringUtil.tokenizeToStringArray(localeString, "_ ", false, false);
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		String variant = "";
		if (parts.length >= 2) {
			// There is definitely a variant, and it is everything after the country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the variant.
			variant = StringUtil.trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_")) {
				variant = StringUtil.trimLeadingCharacter(variant, '_');
			}
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	/**
	 * Return the LocaleResolver that has been bound to the request by the
	 * DispatcherServlet.
	 * @param request current HTTP request
	 * @return the current LocaleResolver, or <code>null</code> if not found
	 */
	public static LocaleResolver getLocaleResolver(HttpServletRequest request) {
		return (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
	}

	/**
	 * Retrieves the current locale from the given request,
	 * using the LocaleResolver bound to the request by the DispatcherServlet
	 * (if available), falling back to the request's accept-header Locale.
	 * @param request current HTTP request
	 * @return the current locale, either from the LocaleResolver or from
	 * the plain request
	 * @see #getLocaleResolver
	 * @see javax.servlet.http.HttpServletRequest#getLocale()
	 */
	public static Locale getLocale(HttpServletRequest request) {
		LocaleResolver localeResolver = getLocaleResolver(request);
		if (localeResolver != null) {
			return localeResolver.resolveLocale(request);
		}
		else {
			return request.getLocale();
		}
	}
	
	public static String getLocaleKey(String key)
	{
		return getLocaleKey(key, WebUtil.getRequest());
	}
	
	public static String getLocaleKey(String key, HttpServletRequest request)
	{
		Locale locale = getLocale(request);
		if(locale.equals(Locale.SIMPLIFIED_CHINESE))
			return key+".cn";
		
		return key;
	}


}
