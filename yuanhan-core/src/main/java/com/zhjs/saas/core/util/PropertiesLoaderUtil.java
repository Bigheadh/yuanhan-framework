package com.yuanhan.yuanhan.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.util.Assert;

import com.yuanhan.yuanhan.core.config.PropertiesHolder;
import com.yuanhan.yuanhan.core.config.PropertyConfig;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */

@SuppressWarnings({"unchecked","rawtypes"})
public abstract class PropertiesLoaderUtil extends PropertiesLoaderUtils {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private PropertiesLoaderUtil(){}

	private static final char[] PARAMETER_SEPARATORS =
			new char[] {'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};


	private static final String[] START_SKIP =
			new String[] {"'", "\"", "--", "/*"};


	private static final String[] STOP_SKIP =
			new String[] {"'", "\"", "\n", "*/"};
	
	private static final String Default_Path = "classpath:/**/*.properties";
	
	
	public static PropertiesHolder getPropertiesHolder()
	{
		return  ApplicationContextUtil.getApplicationContext()
				.getBean(PropertyConfig.PropertyBeanName, PropertiesHolder.class);
	}
	
	public static Properties loadProperties(String path, Class clazz)
	{
		try
		{
			return loadProperties(new ClassPathResource(path, clazz));
		}
		catch(IOException ex)
		{
			throw new IllegalStateException("Could not load " +path+ ": " + ex.getMessage());
		}
	}
	
	public static Properties loadProperties(String path)
	{
		try
		{
			return loadProperties(new ClassPathResource(path));
		}
		catch(IOException ex)
		{
			throw new IllegalStateException("Could not load " +path+ ": " + ex.getMessage());
		}
	}
	
	public static Properties loadAllProperties(String path)
	{
		Properties prop = new Properties();
		try
		{
	        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	        Resource[] resources = resolver.getResources(path==null ? Default_Path : path);
	        for(Resource resource : resources)
	        {
	        	fillProperties(prop, new EncodedResource(resource,CodecUtil.Charset));
	        }
			return prop;
		}
		catch(IOException ex)
		{
			throw new IllegalStateException("Could not load " +path+ ": " + ex.getMessage());
		}
	}
	
	public static NamedParameterObject parseToNamedString(String input) {
		Assert.notNull(input, "Input string must not be null");

		Set<String> namedParameters = new HashSet<String>();
		String inputToUse = input;
		List<ParameterHolder> parameterList = new ArrayList<ParameterHolder>();

		char[] statement = input.toCharArray();
		int namedParameterCount = 0;
		int unnamedParameterCount = 0;
		int totalParameterCount = 0;


		int escapes = 0;
		int i = 0;
		while (i < statement.length) {
			int skipToPosition = i;
			while (i < statement.length) {
				skipToPosition = skipCommentsAndQuotes(statement, i);
				if (i == skipToPosition) {
					break;
				}
				else {
					i = skipToPosition;
				}
			}
			if (i >= statement.length) {
				break;
			}
			char c = statement[i];
			if (c == ':' || c == '&') {
				int j = i + 1;
				if (j < statement.length && statement[j] == ':' && c == ':') {
					// Postgres-style "::" casting operator should be skipped
					i = i + 2;
					continue;
				}
				String parameter = null;
				if (j < statement.length && c == ':' && statement[j] == '{') {
					// :{x} style parameter
					while (j < statement.length && !('}' == statement[j])) {
						j++;
						if (':' == statement[j] || '{' == statement[j]) {
							throw new InvalidDataAccessApiUsageException("Parameter name contains invalid character '" +
									statement[j] + "' at position " + i + " in statement: " + input);
						}
					}
					if (j >= statement.length) {
						throw new InvalidDataAccessApiUsageException(
								"Non-terminated named parameter declaration at position " + i + " in statement: " + input);
					}
					if (j - i > 3) {
						parameter = input.substring(i + 2, j);
						namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
						totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j + 1, parameter);
					}
					j++;
				}
				else {
					while (j < statement.length && !isParameterSeparator(statement[j])) {
						j++;
					}
					if (j - i > 1) {
						parameter = input.substring(i + 1, j);
						namedParameterCount = addNewNamedParameter(namedParameters, namedParameterCount, parameter);
						totalParameterCount = addNamedParameter(parameterList, totalParameterCount, escapes, i, j, parameter);
					}
				}
				i = j - 1;
			}
			else {
				if (c == '\\') {
					int j = i + 1;
					if (j < statement.length && statement[j] == ':') {
						// escaped ":" should be skipped
						inputToUse = inputToUse.substring(0, i - escapes) + inputToUse.substring(i - escapes + 1);
						escapes++;
						i = i + 2;
						continue;
					}
				}
				if (c == '?') {
					int j = i + 1;
					if (j < statement.length && (statement[j] == '?' || statement[j] == '|' || statement[j] == '&')) {
						// Postgres-style "??", "?|", "?&" operator should be skipped
						i = i + 2;
						continue;
					}
					unnamedParameterCount++;
					totalParameterCount++;
				}
			}
			i++;
		}
		NamedParameterObject parsedInput = new NamedParameterObject(inputToUse);
		for (ParameterHolder ph : parameterList) {
			parsedInput.addNamedParameter(ph.getParameterName(), ph.getStartIndex(), ph.getEndIndex());
		}

		parsedInput.setNamedParameterCount(namedParameterCount);
		parsedInput.setUnnamedParameterCount(unnamedParameterCount);
		parsedInput.setTotalParameterCount(totalParameterCount);
		return parsedInput;
	}
	
	public static String resolveNamedArguments(String src, Map arguments, boolean sqlFormat)
	{
		NamedParameterObject parsed = parseToNamedString(src);
		String originalInput = parsed.getOriginalInput();
		StringBuffer actualString = new StringBuffer();
		List paramNames = parsed.getParameterNames();
		int lastIndex = 0;
		for (int i = 0; i < paramNames.size(); i++) {
			String paramName = (String) paramNames.get(i);
			int[] indexes = parsed.getParameterIndexes(i);
			int startIndex = indexes[0];
			int endIndex = indexes[1];
			actualString.append(originalInput.substring(lastIndex, startIndex));
			if (arguments != null && arguments.containsKey(paramName)) {
				Object value = arguments.get(paramName);
				if (value instanceof Collection) {
					Iterator entryIter = ((Collection) value).iterator();
					int k = 0;
					actualString.append("(");
					while (entryIter.hasNext()) {
						if (k > 0) {
							actualString.append(", ");
						}
						k++;
						actualString.append(entryIter.next());
					}
					actualString.append(")");
				}
				else if(value instanceof String)
				{
					if(sqlFormat)
						actualString.append("'").append(value).append("'");
					else
						actualString.append(value);
				}
				else {
					actualString.append(value);
				}
			}
			lastIndex = endIndex;
		}
		actualString.append(originalInput.substring(lastIndex, originalInput.length()));
		return actualString.toString();
	}
	
	private static int skipCommentsAndQuotes(char[] statement, int position) {
		for (int i = 0; i < START_SKIP.length; i++) {
			if (statement[position] == START_SKIP[i].charAt(0)) {
				boolean match = true;
				for (int j = 1; j < START_SKIP[i].length(); j++) {
					if (!(statement[position + j] == START_SKIP[i].charAt(j))) {
						match = false;
						break;
					}
				}
				if (match) {
					int offset = START_SKIP[i].length();
					for (int m = position + offset; m < statement.length; m++) {
						if (statement[m] == STOP_SKIP[i].charAt(0)) {
							boolean endMatch = true;
							int endPos = m;
							for (int n = 1; n < STOP_SKIP[i].length(); n++) {
								if (m + n >= statement.length) {
									// last comment not closed properly
									return statement.length;
								}
								if (!(statement[m + n] == STOP_SKIP[i].charAt(n))) {
									endMatch = false;
									break;
								}
								endPos = m + n;
							}
							if (endMatch) {
								// found character sequence ending comment or quote
								return endPos + 1;
							}
						}
					}
					// character sequence ending comment or quote not found
					return statement.length;
				}

			}
		}
		return position;
	}

	private static int addNamedParameter(
			List<ParameterHolder> parameterList, int totalParameterCount, int escapes, int i, int j, String parameter) {

		parameterList.add(new ParameterHolder(parameter, i - escapes, j - escapes));
		totalParameterCount++;
		return totalParameterCount;
	}

	private static int addNewNamedParameter(Set<String> namedParameters, int namedParameterCount, String parameter) {
		if (!namedParameters.contains(parameter)) {
			namedParameters.add(parameter);
			namedParameterCount++;
		}
		return namedParameterCount;
	}


	private static class ParameterHolder {

		private final String parameterName;

		private final int startIndex;

		private final int endIndex;

		public ParameterHolder(String parameterName, int startIndex, int endIndex) {
			this.parameterName = parameterName;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public String getParameterName() {
			return this.parameterName;
		}

		public int getStartIndex() {
			return this.startIndex;
		}

		public int getEndIndex() {
			return this.endIndex;
		}
	}
	
	private static boolean isParameterSeparator(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		}
		for (int i = 0; i < PARAMETER_SEPARATORS.length; i++) {
			if (c == PARAMETER_SEPARATORS[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map argu = new HashMap();
		argu.put("argu1", "oh my god");
		argu.put("argu2", 2);
		argu.put("argu3", 3567);
		System.out.println(resolveNamedArguments("I said, :argu1 , give me :argu2 days, and :argu3 :argu4 dollors.",argu,true));
	}

}
